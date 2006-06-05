/*
 *   Copyright 2005 The Apache Software Foundation
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
package org.apache.kerberos.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.kerberos.exceptions.ErrorType;
import org.apache.kerberos.kdc.KdcConfiguration;
import org.apache.kerberos.kdc.authentication.AuthenticationContext;
import org.apache.kerberos.kdc.authentication.AuthenticationServiceChain;
import org.apache.kerberos.kdc.ticketgrant.TicketGrantingContext;
import org.apache.kerberos.kdc.ticketgrant.TicketGrantingServiceChain;
import org.apache.kerberos.messages.KdcRequest;
import org.apache.kerberos.store.PrincipalStore;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.protocol.ProtocolHandler;
import org.apache.mina.protocol.ProtocolSession;
import org.apache.protocol.common.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Kerberos protocol handler for MINA which handles requests for the authentication
 * service and the ticket granting service of the KDC.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class KerberosProtocolHandler implements ProtocolHandler
{
    private static final Logger log = LoggerFactory.getLogger( KerberosProtocolHandler.class );

    private KdcConfiguration config;
    private PrincipalStore store;

    private Command authService;
    private Command tgsService;

    public KerberosProtocolHandler( KdcConfiguration config, PrincipalStore store )
    {
        this.config = config;
        this.store = store;

        authService = new AuthenticationServiceChain();
        tgsService = new TicketGrantingServiceChain();
    }

    public void sessionCreated( ProtocolSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " CREATED" );
        }
    }

    public void sessionOpened( ProtocolSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " OPENED" );
        }
    }

    public void sessionClosed( ProtocolSession session )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " CLOSED" );
        }
    }

    public void sessionIdle( ProtocolSession session, IdleStatus status )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " IDLE(" + status + ")" );
        }
    }

    public void exceptionCaught( ProtocolSession session, Throwable cause )
    {
        log.error( session.getRemoteAddress() + " EXCEPTION", cause );
        session.close();
    }

    public void messageReceived( ProtocolSession session, Object message )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " RCVD: " + message );
        }

        InetAddress clientAddress = ( (InetSocketAddress) session.getRemoteAddress() ).getAddress();
        KdcRequest request = (KdcRequest) message;

        int messageType = request.getMessageType().getOrdinal();

        try
        {
            switch ( messageType )
            {
                case 10:
                    AuthenticationContext authContext = new AuthenticationContext();
                    authContext.setConfig( config );
                    authContext.setStore( store );
                    authContext.setClientAddress( clientAddress );
                    authContext.setRequest( request );

                    authService.execute( authContext );

                    session.write( authContext.getReply() );
                    break;

                case 12:
                    TicketGrantingContext tgsContext = new TicketGrantingContext();
                    tgsContext.setConfig( config );
                    tgsContext.setStore( store );
                    tgsContext.setClientAddress( clientAddress );
                    tgsContext.setRequest( request );

                    tgsService.execute( tgsContext );

                    session.write( tgsContext.getReply() );
                    break;

                case 11:
                case 13:
                    log.error( "Kerberos error:  " + ErrorType.KRB_AP_ERR_BADDIRECTION.getMessage() );

                default:
                    log.error( "Kerberos error:  " + ErrorType.KRB_AP_ERR_MSG_TYPE.getMessage() );
            }
        }
        catch ( Exception e )
        {
            log.error( e.getMessage() );
        }
    }

    public void messageSent( ProtocolSession session, Object message )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( session.getRemoteAddress() + " SENT: " + message );
        }
    }
}