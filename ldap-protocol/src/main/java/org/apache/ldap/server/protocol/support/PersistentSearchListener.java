/*
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
package org.apache.ldap.server.protocol.support;


import java.util.Iterator;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;

import org.apache.ldap.common.codec.search.controls.ChangeType;
import org.apache.ldap.common.exception.LdapException;
import org.apache.ldap.common.exception.OperationAbandonedException;
import org.apache.ldap.common.message.AbandonListener;
import org.apache.ldap.common.message.AbandonableRequest;
import org.apache.ldap.common.message.Control;
import org.apache.ldap.common.message.EntryChangeControl;
import org.apache.ldap.common.message.LdapResult;
import org.apache.ldap.common.message.PersistentSearchControl;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.message.SearchRequest;
import org.apache.ldap.common.message.SearchResponseEntry;
import org.apache.ldap.common.message.SearchResponseEntryImpl;
import org.apache.ldap.common.util.ExceptionUtils;
import org.apache.ldap.server.jndi.ServerLdapContext;
import org.apache.ldap.server.protocol.SessionRegistry;
import org.apache.mina.common.IoSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A JNDI NamingListener implementation which sends back added, deleted, modified or 
 * renamed entries to a client that created this listener.  This class is part of the
 * persistent search implementation which uses the event notification scheme built into
 * the server core.  This is exposed by the server side ApacheDS JNDI LDAP provider.
 * 
 * This listener is disabled only when a session closes or when an abandon request 
 * cancels it.  Hence time and size limits in normal search operations do not apply
 * here.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
class PersistentSearchListener implements ObjectChangeListener, NamespaceChangeListener, AbandonListener
{
    private static final Logger log = LoggerFactory.getLogger( SearchHandler.class );
    final ServerLdapContext ctx;
    final IoSession session;
    final SearchRequest req;
    final PersistentSearchControl control;
    
    
    PersistentSearchListener( ServerLdapContext ctx, IoSession session, SearchRequest req ) 
    {
        this.session = session;
        this.req = req;
        req.addAbandonListener( this );
        this.ctx = ctx;
        this.control = getPersistentSearchControl( req );
    }
    
    
    public void abandon() throws NamingException
    {
        // must abandon the operation 
        ctx.removeNamingListener( this );

        /*
         * From RFC 2251 Section 4.11:
         * 
         * In the event that a server receives an Abandon Request on a Search  
         * operation in the midst of transmitting responses to the Search, that
         * server MUST cease transmitting entry responses to the abandoned
         * request immediately, and MUST NOT send the SearchResultDone. Of
         * course, the server MUST ensure that only properly encoded LDAPMessage
         * PDUs are transmitted. 
         * 
         * SO DON'T SEND BACK ANYTHING!!!!!
         */
    }
    
    
    public void namingExceptionThrown( NamingExceptionEvent evt ) 
    {
        // must abandon the operation and send response done with an
        // error message if this occurs because something is wrong

        try
        {
            ctx.removeNamingListener( this );
        }
        catch ( NamingException e )
        {
            log.error( "Attempt to remove listener from context failed", e );
        }
        
        /*
         * From RFC 2251 Section 4.11:
         * 
         * In the event that a server receives an Abandon Request on a Search  
         * operation in the midst of transmitting responses to the Search, that
         * server MUST cease transmitting entry responses to the abandoned
         * request immediately, and MUST NOT send the SearchResultDone. Of
         * course, the server MUST ensure that only properly encoded LDAPMessage
         * PDUs are transmitted. 
         * 
         * SO DON'T SEND BACK ANYTHING!!!!!
         */
        if ( evt.getException() instanceof OperationAbandonedException )
        {
            return;
        }

        SessionRegistry.getSingleton().removeOutstandingRequest( session, new Integer( req.getMessageId() ) );
        String msg = "failed on persistent search operation";

        if ( log.isDebugEnabled() )
        {
            msg += ":\n" + req + ":\n" + ExceptionUtils.getStackTrace( evt.getException() );
        }

        ResultCodeEnum code = null;
        if( evt.getException() instanceof LdapException )
        {
            code = ( ( LdapException ) evt.getException() ).getResultCode();
        }
        else
        {
            code = ResultCodeEnum.getBestEstimate( evt.getException(), req.getType() );
        }

        LdapResult result = req.getResultResponse().getLdapResult();
        result.setResultCode( code );
        result.setErrorMessage( msg );
        if ( ( evt.getException().getResolvedName() != null ) &&
                ( ( code == ResultCodeEnum.NOSUCHOBJECT ) ||
                  ( code == ResultCodeEnum.ALIASPROBLEM ) ||
                  ( code == ResultCodeEnum.INVALIDDNSYNTAX ) ||
                  ( code == ResultCodeEnum.ALIASDEREFERENCINGPROBLEM ) ) )
        {
            result.setMatchedDn( evt.getException().getResolvedName().toString() );
        }
        session.write( req.getResultResponse() );
    }

    
    public void objectChanged( NamingEvent evt )
    {
        // send the entry back
        sendEntry( evt );
    }

    public void objectAdded( NamingEvent evt )
    {
        // send the entry back
        sendEntry( evt );
    }

    public void objectRemoved( NamingEvent evt )
    {
        // send the entry back
        sendEntry( evt );
    }

    public void objectRenamed( NamingEvent evt )
    {
        // send the entry back
        sendEntry( evt );
    }

    private void sendEntry( NamingEvent evt ) 
    {
        /*
         * @todo eventually you'll want to add the changeNumber once we move 
         * the CSN functionality into the server.
         */
        SearchResponseEntry respEntry = new SearchResponseEntryImpl( req.getMessageId() );
        EntryChangeControl ecControl = null;

        if ( control.isReturnECs() )
        {
            ecControl = new EntryChangeControl();
            respEntry.add( ecControl );
        }
        
        switch ( evt.getType() )
        {
            case( NamingEvent.OBJECT_ADDED ):
                if ( ! control.isNotificationEnabled( ChangeType.ADD ) ) return;
                respEntry.setObjectName( evt.getNewBinding().getName() );
                respEntry.setAttributes( ( Attributes ) evt.getChangeInfo() );
                if ( ecControl != null )
                {
                    ecControl.setChangeType( ChangeType.ADD );
                }
                break;
            case( NamingEvent.OBJECT_CHANGED ):
                if ( ! control.isNotificationEnabled( ChangeType.MODIFY ) ) return;
                respEntry.setObjectName( evt.getOldBinding().getName() );
                respEntry.setAttributes( ( Attributes ) evt.getOldBinding().getObject() );
                if ( ecControl != null )
                {
                    ecControl.setChangeType( ChangeType.MODIFY );
                }
                break;
            case( NamingEvent.OBJECT_REMOVED ):
                if ( ! control.isNotificationEnabled( ChangeType.DELETE ) ) return;
                respEntry.setObjectName( evt.getOldBinding().getName() );
                respEntry.setAttributes( ( Attributes ) evt.getOldBinding().getObject() );
                if ( ecControl != null )
                {
                    ecControl.setChangeType( ChangeType.DELETE );
                }
                break;
            case( NamingEvent.OBJECT_RENAMED ):
                if ( ! control.isNotificationEnabled( ChangeType.MODDN ) ) return;
                respEntry.setObjectName( evt.getNewBinding().getName() );
                respEntry.setAttributes( ( Attributes ) evt.getNewBinding().getObject() );
                if ( ecControl != null )
                {
                    ecControl.setChangeType( ChangeType.MODDN );
                    ecControl.setPreviousDn( evt.getOldBinding().getName() );
                }
                break;
            default:
                return;
        }
        
        session.write( respEntry );
    }


    /**
     * Searches for and returns the PersistentSearchControl in the request if present.
     * 
     * @param req the request searched
     * @return the psearch control or null if one does not exist for this req
     */
    static PersistentSearchControl getPersistentSearchControl( SearchRequest req )
    {
        Iterator list = req.getControls().values().iterator();
        while ( list.hasNext() )
        {
            Control control = ( Control ) list.next();
            if ( control.getID().equals( "2.16.840.1.113730.3.4.3" ) )
            {
                return ( PersistentSearchControl ) control;
            }
        }
        
        return null;
    }


    public void requestAbandoned( AbandonableRequest req )
    {
        try
        {
            abandon();
        }
        catch ( NamingException e )
        {
            log.error( "failed to properly abandon this persistent search", e );
        }
    }
}