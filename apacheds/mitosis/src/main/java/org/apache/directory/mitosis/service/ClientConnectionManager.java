/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.mitosis.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoConnector;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.thread.ThreadPoolFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.directory.mitosis.common.Replica;
import org.apache.directory.mitosis.configuration.ReplicationConfiguration;
import org.apache.directory.mitosis.service.protocol.handler.ReplicationClientProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages all outgoing connections to remote replicas.
 *
 * @author Trustin Lee
 * @version $Rev: 116 $, $Date: 2006-09-18 13:47:53Z $
 */
class ClientConnectionManager
{
    private static final Logger log = LoggerFactory.getLogger( ClientConnectionManager.class );
    
    private final ReplicationService service;
    private final IoConnector connector = new SocketConnector();
    private final Map sessions = new HashMap();
    private ReplicationConfiguration configuration;
    private ConnectionMonitor monitor;
    
    ClientConnectionManager( ReplicationService service )
    {
        this.service = service;
    }

    public void start( ReplicationConfiguration cfg ) throws Exception
    {
        // initialze client connection
        //// initialize thread pool
        ThreadPoolFilter threadPoolFilter = new ThreadPoolFilter();
        connector.getFilterChain().addLast( "threadPool", threadPoolFilter );
        //// add logger
        connector.getFilterChain().addLast( "logger", new LoggingFilter() );
        
        this.configuration = cfg;
        
        monitor = new ConnectionMonitor();
        monitor.start();
    }
    
    public void stop() throws Exception
    {
        // close all connections
        monitor.shutdown();
        
        // remove all filters
        connector.getFilterChain().remove( "threadPool" );
        connector.getFilterChain().remove( "logger" );
    }
    
    private class ConnectionMonitor extends Thread
    {
        private boolean timeToShutdown = false;

        public ConnectionMonitor()
        {
            super( "ClientConnectionManager" );
        }
        
        public void shutdown()
        {
            timeToShutdown = true;
            while( isAlive() )
            {
                try
                {
                    join();
                }
                catch( InterruptedException e )
                {
                    log.warn( "Unexpected exception.", e );
                }
            }
        }
        
        public void run()
        {
            while( !timeToShutdown )
            {
                connectUnconnected();
                try
                {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e )
                {
                    log.warn( "Unexpected exception.", e );
                }
            }
            
            disconnectConnected();
        }

        private void connectUnconnected()
        {
            Iterator i = configuration.getPeerReplicas().iterator();
            while( i.hasNext() )
            {
                Replica replica = ( Replica ) i.next();
                Connection con = ( Connection ) sessions.get( replica.getId() );
                if( con == null )
                {
                    con = new Connection();
                    sessions.put( replica.getId(), con );
                }
                
                synchronized( con )
                {
                    if( con.inProgress )
                    {
                        // connection is in progress
                        continue;
                    }
                        
                    if( con.session != null )
                    {
                        if( con.session.isConnected() )
                        {
                            continue;
                        }
                        con.session = null;
                    }
                    
                    // put to connectingSession with dummy value to mark
                    // that connection is in progress
                    con.inProgress = true;

                    if( con.delay < 0 )
                    {
                        con.delay = 0;
                    }
                    else if( con.delay == 0 )
                    {
                        con.delay = 2;
                    }
                    else
                    {
                        con.delay *= 2;
                        if( con.delay > 60 )
                        {
                            con.delay = 60;
                        }
                    }
                }
                
                Connector connector = new Connector( replica, con );
                synchronized( con ) 
                {
                    con.connector = connector;
                }
                connector.start();
            }
        }
        
        private void disconnectConnected()
        {
            log.info( "Closing all connections..." );
            for( ;; )
            {
                Iterator i = sessions.values().iterator();
                while( i.hasNext() )
                {
                    Connection con = ( Connection ) i.next();
                    synchronized( con )
                    {
                        if( con.inProgress )
                        {
                            if( con.connector != null )
                            {
                                con.connector.interrupt();
                            }
                            continue;
                        }
                        
                        i.remove();

                        if( con.session != null )
                        {
                            con.session.close();
                        }
                    }
                }
                
                if( sessions.isEmpty() )
                {
                    break;
                }
                
                // Sleep 1 second and try again waiting for Connector threads.
                try
                {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e )
                {
                    log.warn( "Unexpected exception.", e );
                }
            }
        }
    }
    
    private class Connector extends Thread
    {
        private final Replica replica;
        private final Connection con;
        
        public Connector( Replica replica, Connection con )
        {
            super( "ClientConnectionManager-" + replica );
            this.replica = replica;
            this.con = con;
        }
        
        public void run()
        {
            if( con.delay > 0 )
            {
                log.info( "[" + replica + "] Waiting for " + con.delay + " seconds to reconnect." );
                try
                {
                    Thread.sleep( con.delay * 1000L );
                }
                catch( InterruptedException e )
                {
                }
            }
            
            log.info( "[" + replica + "] Connecting..." );

            IoSession session;
            try
            {
                connector.setConnectTimeout( configuration.getResponseTimeout() );
                ConnectFuture future = connector.connect(
                        replica.getAddress(),
                        new ReplicationClientProtocolHandler( service ) );
                
                future.join();
                session = future.getSession();

                synchronized( con )
                {
                    con.session = session;
                    con.delay = -1; // reset delay
                    con.inProgress = false;
                }
            }
            catch( IOException e )
            {
                log.warn("[" + replica + "] Failed to connect.", e );
            }
            finally
            {
                synchronized( con )
                {
                    con.inProgress = false;
                    con.connector = null;
                }
            }
        }
    }
    
    private static class Connection
    {
        private IoSession session;
        private int delay = -1;
        private boolean inProgress;
        private Connector connector;
        
        public Connection()
        {
        }
    }
}