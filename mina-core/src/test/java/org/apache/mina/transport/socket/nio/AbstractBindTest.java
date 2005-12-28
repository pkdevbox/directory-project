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
package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests {@link IoAcceptor} resource leakage by repeating bind and unbind.
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev$, $Date$ 
 */
public abstract class AbstractBindTest extends TestCase
{
    protected final IoAcceptor acceptor;
    protected int port;

    public AbstractBindTest( IoAcceptor acceptor )
    {
        this.acceptor = acceptor;
    }
    
    private void bind( boolean reuseAddress ) throws IOException
    {
        setReuseAddress( reuseAddress );

        // Find an availble test port and bind to it.
        boolean socketBound = false;

        // Let's start from port #1 to detect possible resource leak
        // because test will fail in port 1-1023 if user run this test
        // as a normal user.
        for( port = 1; port <= 65535; port ++ )
        {
            socketBound = false;
            try
            {
                acceptor.bind( new InetSocketAddress( port ),
                        new EchoProtocolHandler() );
                socketBound = true;
                break;
            }
            catch( IOException e )
            {
            }
        }

        // If there is no port available, test fails.
        if( !socketBound )
        {
            throw new IOException( "Cannot bind any test port." );
        }

        //System.out.println( "Using port " + port + " for testing." );
    }

    private void setReuseAddress( boolean reuseAddress )
    {
        if( acceptor instanceof DatagramAcceptor )
        {
            ( ( DatagramAcceptor ) acceptor ).setReuseAddress( reuseAddress );
        }
        else if( acceptor instanceof SocketAcceptor )
        {
            ( ( SocketAcceptor ) acceptor ).setReuseAddress( reuseAddress );
        }
    }
    
    public void tearDown()
    {
        try
        {
            acceptor.unbind( new InetSocketAddress( port ) );
        }
        catch( Exception e )
        {
            // ignore
        }
    }
    
    public void testDuplicateBind() throws IOException
    {
        bind( false );
        
        try
        {
            acceptor.bind( new InetSocketAddress( port ), new EchoProtocolHandler() );
            Assert.fail( "IOException is not thrown" );
        }
        catch( IOException e )
        {
        }
    }

    public void testDuplicateUnbind() throws IOException
    {
        bind( false );
        
        // this should succeed
        acceptor.unbind( new InetSocketAddress( port ) );
        
        try
        {
            // this should fail
            acceptor.unbind( new InetSocketAddress( port ) );
            Assert.fail( "Exception is not thrown" );
        }
        catch( Exception e )
        {
        }
    }
    
    public void testManyTimes() throws IOException
    {
        bind( true );
        
        InetSocketAddress addr = new InetSocketAddress( port );
        EchoProtocolHandler handler = new EchoProtocolHandler();
        for( int i = 0; i < 1024; i++ ) 
        {
            acceptor.unbind( addr );
            acceptor.bind( addr, handler );
        }
    }
    
    public void _testRegressively() throws IOException
    {
        setReuseAddress( true );

        InetSocketAddress addr = new InetSocketAddress( port );
        EchoProtocolHandler handler = new EchoProtocolHandler();
        for( int i = 0; i < 1048576; i++ )
        {
            acceptor.bind( addr, handler );
            testDuplicateBind();
            testDuplicateUnbind();
            if( i % 100 == 0 )
            {
                System.out.println( i + " (" + new Date() + ")" );
            }
        }
        bind( false );
    }

    static class EchoProtocolHandler extends IoHandlerAdapter
    {
        private static final Logger log = LoggerFactory.getLogger( EchoProtocolHandler.class );

        public void sessionCreated( IoSession session )
        {
            if( session instanceof SocketSession )
            {
                ( ( SocketSession ) session ).setSessionReceiveBufferSize( 2048 );
            }

            session.setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        }

        public void sessionIdle( IoSession session, IdleStatus status )
        {
            log.info( "*** IDLE #" + session.getIdleCount( IdleStatus.BOTH_IDLE ) + " ***" );
        }

        public void exceptionCaught( IoSession session, Throwable cause )
        {
            cause.printStackTrace();
            session.close();
        }

        public void messageReceived( IoSession session, Object message ) throws Exception
        {
            if( !( message instanceof ByteBuffer ) )
            {
                return;
            }

            ByteBuffer rb = ( ByteBuffer ) message;
            // Write the received data back to remote peer
            ByteBuffer wb = ByteBuffer.allocate( rb.remaining() );
            wb.put( rb );
            wb.flip();
            session.write( wb );
        }
    }
}
