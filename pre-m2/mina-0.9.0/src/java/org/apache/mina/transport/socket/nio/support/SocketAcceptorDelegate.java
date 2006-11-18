/*
 *   @(#) $Id$
 *
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.mina.transport.socket.nio.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.mina.common.ExceptionMonitor;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoFilterChainBuilder;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.support.BaseIoAcceptor;
import org.apache.mina.transport.socket.nio.SocketSessionManager;
import org.apache.mina.util.Queue;

/**
 * {@link IoAcceptor} for socket transport (TCP/IP).
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public class SocketAcceptorDelegate extends BaseIoAcceptor implements SocketSessionManager
{
    private static volatile int nextId = 0;

    private final IoAcceptor wrapper;
    private final int id = nextId ++ ;
    private final String threadName = "SocketAcceptor-" + id;
    private boolean reuseAddress = false;
    private int backlog = 50;
    private int receiveBufferSize = -1;
    private Selector selector;
    private final Map channels = new HashMap();

    private final Queue registerQueue = new Queue();
    private final Queue cancelQueue = new Queue();
    
    private Worker worker;

    /**
     * Creates a new instance.
     */
    public SocketAcceptorDelegate( IoAcceptor wrapper )
    {
        this.wrapper = wrapper;
    }

    /**
     * Binds to the specified <code>address</code> and handles incoming
     * connections with the specified <code>handler</code>.  Backlog value
     * is configured to the value of <code>backlog</code> property.
     *
     * @throws IOException if failed to bind
     */
    public void bind( SocketAddress address, IoHandler handler, IoFilterChainBuilder filterChainBuilder ) throws IOException
    {
        if( address == null )
        {
            throw new NullPointerException( "address" );
        }

        if( handler == null )
        {
            throw new NullPointerException( "handler" );
        }

        if( !( address instanceof InetSocketAddress ) )
        {
            throw new IllegalArgumentException( "Unexpected address type: " + address.getClass() );
        }

        if( ( ( InetSocketAddress ) address ).getPort() == 0 )
        {
            throw new IllegalArgumentException( "Unsupported port number: 0" );
        }
        
        if( filterChainBuilder == null )
        {
            filterChainBuilder = IoFilterChainBuilder.NOOP;
        }
        
        RegistrationRequest request = new RegistrationRequest( address, handler, filterChainBuilder );

        synchronized( this )
        {
            synchronized( registerQueue )
            {
                registerQueue.push( request );
            }
            startupWorker();
        }
        
        selector.wakeup();
        
        synchronized( request )
        {
            while( !request.done )
            {
                try
                {
                    request.wait();
                }
                catch( InterruptedException e )
                {
                }
            }
        }
        
        if( request.exception != null )
        {
            throw request.exception;
        }
    }


    private synchronized void startupWorker() throws IOException
    {
        if( worker == null )
        {
            selector = Selector.open();
            worker = new Worker();

            worker.start();
        }
    }

    public void unbind( SocketAddress address )
    {
        // TODO: DIRMINA-93
        if( address == null )
        {
            throw new NullPointerException( "address" );
        }

        CancellationRequest request = new CancellationRequest( address );
        synchronized( this )
        {
            try
            {
                startupWorker();
            }
            catch( IOException e )
            {
                // IOException is thrown only when Worker thread is not
                // running and failed to open a selector.  We simply throw
                // IllegalArgumentException here because we can simply
                // conclude that nothing is bound to the selector.
                throw new IllegalArgumentException( "Address not bound: " + address );
            }

            synchronized( cancelQueue )
            {
                cancelQueue.push( request );
            }
        }
        
        selector.wakeup();

        synchronized( request )
        {
            while( !request.done )
            {
                try
                {
                    request.wait();
                }
                catch( InterruptedException e )
                {
                }
            }
        }
        
        if( request.exception != null )
        {
            request.exception.fillInStackTrace();

            throw request.exception;
        }
    }
    
    public int getProcessors()
    {
        throw new UnsupportedOperationException();
    }

    public void setProcessors( int nProcessor )
    {
        throw new UnsupportedOperationException();
    }

    private class Worker extends Thread
    {
        public Worker()
        {
            super( SocketAcceptorDelegate.this.threadName );
        }

        public void run()
        {
            for( ;; )
            {
                try
                {
                    int nKeys = selector.select();

                    registerNew();
                    cancelKeys();

                    if( nKeys > 0 )
                    {
                        processSessions( selector.selectedKeys() );
                    }

                    if( selector.keys().isEmpty() )
                    {
                        synchronized( SocketAcceptorDelegate.this )
                        {
                            if( selector.keys().isEmpty() &&
                                registerQueue.isEmpty() &&
                                cancelQueue.isEmpty() )
                            {
                                worker = null;
                                try
                                {
                                    selector.close();
                                }
                                catch( IOException e )
                                {
                                    ExceptionMonitor.getInstance().exceptionCaught( e );
                                }
                                finally
                                {
                                    selector = null;
                                }
                                break;
                            }
                        }
                    }
                }
                catch( IOException e )
                {
                    ExceptionMonitor.getInstance().exceptionCaught( e );

                    try
                    {
                        Thread.sleep( 1000 );
                    }
                    catch( InterruptedException e1 )
                    {
                    }
                }
            }
        }

        private void processSessions( Set keys ) throws IOException
        {
            Iterator it = keys.iterator();
            while( it.hasNext() )
            {
                SelectionKey key = ( SelectionKey ) it.next();
   
                it.remove();
   
                if( !key.isAcceptable() )
                {
                    continue;
                }
   
                ServerSocketChannel ssc = ( ServerSocketChannel ) key.channel();
   
                SocketChannel ch = ssc.accept();
   
                if( ch == null )
                {
                    continue;
                }
   
                boolean success = false;
                try
                {
                    RegistrationRequest req = ( RegistrationRequest ) key.attachment();
                    SocketSessionImpl session = new SocketSessionImpl( SocketAcceptorDelegate.this.wrapper, ch, req.handler );
                    SocketAcceptorDelegate.this.filterChainBuilder.buildFilterChain( session.getFilterChain() );
                    req.filterChainBuilder.buildFilterChain( session.getFilterChain() );
                    ( ( SocketFilterChain ) session.getFilterChain() ).sessionCreated( session );
                    session.getIoProcessor().addNew( session );
                    success = true;
                }
                catch( Throwable t )
                {
                    ExceptionMonitor.getInstance().exceptionCaught( t );
                }
                finally
                {
                    if( !success )
                    {
                        ch.close();
                    }
                }
            }
        }
    }


    private void registerNew()
    {
        if( registerQueue.isEmpty() )
        {
            return;
        }

        for( ;; )
        {
            RegistrationRequest req;

            synchronized( registerQueue )
            {
                req = ( RegistrationRequest ) registerQueue.pop();
            }

            if( req == null )
            {
                break;
            }

            ServerSocketChannel ssc = null;

            try
            {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking( false );
                
                // Configure the server socket,
                ssc.socket().setReuseAddress( isReuseAddress() );
                if( getReceiveBufferSize() > 0 )
                {
                    ssc.socket().setReceiveBufferSize( getReceiveBufferSize() );
                }
                
                // and bind.
                ssc.socket().bind( req.address, getBacklog() );
                ssc.register( selector, SelectionKey.OP_ACCEPT, req );

                channels.put( req.address, ssc );
            }
            catch( IOException e )
            {
                req.exception = e;
            }
            finally
            {
                synchronized( req )
                {
                    req.done = true;

                    req.notify();
                }

                if( ssc != null && req.exception != null )
                {
                    try
                    {
                        ssc.close();
                    }
                    catch( IOException e )
                    {
                        ExceptionMonitor.getInstance().exceptionCaught( e );
                    }
                }
            }
        }
    }


    private void cancelKeys()
    {
        if( cancelQueue.isEmpty() )
        {
            return;
        }

        for( ;; )
        {
            CancellationRequest request;

            synchronized( cancelQueue )
            {
                request = ( CancellationRequest ) cancelQueue.pop();
            }

            if( request == null )
            {
                break;
            }

            ServerSocketChannel ssc = ( ServerSocketChannel ) channels.remove( request.address );
            
            // close the channel
            try
            {
                if( ssc == null )
                {
                    request.exception = new IllegalArgumentException( "Address not bound: " + request.address );
                }
                else
                {
                    SelectionKey key = ssc.keyFor( selector );

                    key.cancel();

                    selector.wakeup(); // wake up again to trigger thread death

                    ssc.close();
                }
            }
            catch( IOException e )
            {
                ExceptionMonitor.getInstance().exceptionCaught( e );
            }
            finally
            {
                synchronized( request )
                {
                    request.done = true;

                    request.notify();
                }
            }
        }
    }

    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }

    /**
     * @param receiveBufferSize <tt>-1</tt> to use the default value.
     */
    public void setReceiveBufferSize( int receiveBufferSize )
    {
        this.receiveBufferSize = receiveBufferSize;
    }

    public boolean isReuseAddress()
    {
        return reuseAddress;
    }

    public void setReuseAddress( boolean reuseAddress )
    {
        this.reuseAddress = reuseAddress;
    }

    public int getBacklog()
    {
        return backlog;
    }

    public void setBacklog( int backlog )
    {
        if( backlog <= 0 )
        {
            throw new IllegalArgumentException( "backlog: " + backlog );
        }
        this.backlog = backlog;
    }

    private static class RegistrationRequest
    {
        private final SocketAddress address;
        private final IoHandler handler;
        private final IoFilterChainBuilder filterChainBuilder;
        private IOException exception;
        private boolean done;
        
        private RegistrationRequest( SocketAddress address, IoHandler handler, IoFilterChainBuilder filterChainBuilder )
        {
            this.address = address;
            this.handler = handler;
            this.filterChainBuilder = filterChainBuilder;
        }
    }


    private static class CancellationRequest
    {
        private final SocketAddress address;

        private boolean done;

        private RuntimeException exception;
        
        private CancellationRequest( SocketAddress address )
        {
            this.address = address;
        }
    }
}