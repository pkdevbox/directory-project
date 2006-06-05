/*
 * @(#) $Id$
 */
package org.apache.mina.transport.vmpipe.support;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.mina.common.CloseFuture;
import org.apache.mina.common.ExceptionMonitor;
import org.apache.mina.common.IoFilterChain;
import org.apache.mina.common.IoFilterChainBuilder;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoSessionManager;
import org.apache.mina.common.TransportType;
import org.apache.mina.common.IoFilter.WriteRequest;
import org.apache.mina.common.support.BaseIoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.vmpipe.VmPipeSession;
import org.apache.mina.util.ExceptionUtil;
import org.apache.mina.util.Queue;

/**
 * A {@link IoSession} for in-VM transport (VM_PIPE).
 * 
 * @author The Apache Directory Project (dev@directory.apache.org)
 * @version $Rev$, $Date$
 */
public class VmPipeSessionImpl extends BaseIoSession implements VmPipeSession
{
    private final IoSessionManager manager;
    private final SocketAddress localAddress;
    private final SocketAddress remoteAddress;
    private final IoHandler handler;
    private final VmPipeFilterChain filterChain;
    final VmPipeSessionImpl remoteSession;
    final Object lock;
    final Queue pendingDataQueue;

    /**
     * Constructor for client-side session.
     */
    public VmPipeSessionImpl( IoSessionManager manager, Object lock, SocketAddress localAddress,
                   IoHandler handler, IoFilterChainBuilder filterChainBuilder,
                   VmPipe remoteEntry ) throws IOException
    {
        this.manager = manager;
        this.lock = lock;
        this.localAddress = localAddress;
        this.remoteAddress = remoteEntry.getAddress();
        this.handler = handler;
        this.filterChain = new VmPipeFilterChain( this );
        this.pendingDataQueue = new Queue();

        remoteSession = new VmPipeSessionImpl( manager, this, remoteEntry );
        
        // initialize remote session
        try
        {
            remoteEntry.getAcceptor().getFilterChainBuilder().buildFilterChain( remoteSession.getFilterChain() );
            remoteEntry.getFilterChainBuilder().buildFilterChain( remoteSession.getFilterChain() );
            ( ( VmPipeFilterChain ) remoteSession.getFilterChain() ).sessionCreated( remoteSession );
        }
        catch( Throwable t )
        {
            ExceptionMonitor.getInstance().exceptionCaught( t );
            IOException e = new IOException( "Failed to initialize remote session." );
            e.initCause( t );
            throw e;
        }
        
        // initialize client session
        try
        {
            manager.getFilterChainBuilder().buildFilterChain( remoteSession.getFilterChain() );
            filterChainBuilder.buildFilterChain( remoteSession.getFilterChain() );
            handler.sessionCreated( this );
        }
        catch( Throwable t )
        {
            ExceptionUtil.throwException( t );
        }

        VmPipeIdleStatusChecker.getInstance().addSession( remoteSession );
        VmPipeIdleStatusChecker.getInstance().addSession( this );
        
        ( ( VmPipeFilterChain ) remoteSession.getFilterChain() ).sessionOpened( remoteSession );
        filterChain.sessionOpened( this );
    }

    /**
     * Constructor for server-side session.
     */
    private VmPipeSessionImpl( IoSessionManager manager, VmPipeSessionImpl remoteSession, VmPipe entry )
    {
        this.manager = manager;
        this.lock = remoteSession.lock;
        this.localAddress = remoteSession.remoteAddress;
        this.remoteAddress = remoteSession.localAddress;
        this.handler = entry.getHandler();
        this.filterChain = new VmPipeFilterChain( this );
        this.remoteSession = remoteSession;
        this.pendingDataQueue = new Queue();
    }
    
    public IoSessionManager getManager()
    {
        return manager;
    }

    public IoFilterChain getFilterChain()
    {
        return filterChain;
    }

    public IoHandler getHandler()
    {
        return handler;
    }

    public ProtocolEncoder getEncoder()
    {
        return null;
    }

    public ProtocolDecoder getDecoder()
    {
        return null;
    }
    
    protected void close0( CloseFuture closeFuture )
    {
        filterChain.filterClose( this, closeFuture );
    }
    
    protected void write0( WriteRequest writeRequest )
    {
        this.filterChain.filterWrite( this, writeRequest );
    }

    public int getScheduledWriteRequests()
    {
        return 0;
    }

    public TransportType getTransportType()
    {
        return TransportType.VM_PIPE;
    }

    public SocketAddress getRemoteAddress()
    {
        return remoteAddress;
    }

    public SocketAddress getLocalAddress()
    {
        return localAddress;
    }

    protected void updateTrafficMask()
    {
        if( getTrafficMask().isReadable() || getTrafficMask().isWritable())
        {
            Object[] data = null;
            synchronized( pendingDataQueue )
            {
                data = pendingDataQueue.toArray();
                pendingDataQueue.clear();
            }
            
            for( int i = 0; i < data.length; i++ )
            {
                if( data[ i ] instanceof WriteRequest )
                {
                    WriteRequest wr = ( WriteRequest ) data[ i ];
                    filterChain.doWrite( this, wr );
                }
                else
                {
                    filterChain.messageReceived( this, data[ i ] );
                }
            }
        }
    }
}