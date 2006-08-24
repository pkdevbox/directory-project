/*
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
package org.apache.directory.server.core.authn;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.directory.server.core.DirectoryServiceConfiguration;
import org.apache.directory.server.core.configuration.AuthenticatorConfiguration;
import org.apache.directory.server.core.configuration.InterceptorConfiguration;
import org.apache.directory.server.core.interceptor.BaseInterceptor;
import org.apache.directory.server.core.interceptor.Interceptor;
import org.apache.directory.server.core.interceptor.NextInterceptor;
import org.apache.directory.server.core.invocation.InvocationStack;
import org.apache.directory.server.core.jndi.LdapJndiProperties;
import org.apache.directory.server.core.jndi.ServerContext;
import org.apache.directory.shared.ldap.exception.LdapAuthenticationException;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An {@link Interceptor} that authenticates users.
 *
 * @author Apache Directory Project (dev@directory.apache.org)
 * @author Alex Karasulu (akarasulu@apache.org)
 * @author Trustin Lee (trustin@apache.org)
 * @version $Rev$, $Date$
 */
public class AuthenticationService extends BaseInterceptor
{
    private static final Logger log = LoggerFactory.getLogger( AuthenticationService.class );

    /** authenticators **/
    public Map authenticators = new HashMap();

    private DirectoryServiceConfiguration factoryCfg;


    /**
     * Creates an authentication service interceptor.
     */
    public AuthenticationService()
    {
    }


    /**
     * Registers and initializes all {@link Authenticator}s to this service.
     */
    public void init( DirectoryServiceConfiguration factoryCfg, InterceptorConfiguration cfg ) throws NamingException
    {
        this.factoryCfg = factoryCfg;

        // Register all authenticators
        Iterator i = factoryCfg.getStartupConfiguration().getAuthenticatorConfigurations().iterator();
        while ( i.hasNext() )
        {
            try
            {
                this.register( ( AuthenticatorConfiguration ) i.next() );
            }
            catch ( Exception e )
            {
                destroy();
                throw ( NamingException ) new NamingException( "Failed to register authenticator." ).initCause( e );
            }
        }
    }


    /**
     * Deinitializes and deregisters all {@link Authenticator}s from this service.
     */
    public void destroy()
    {
        Iterator i = new ArrayList( authenticators.values() ).iterator();
        while ( i.hasNext() )
        {
            Iterator j = new ArrayList( ( Collection ) i.next() ).iterator();
            while ( j.hasNext() )
            {
                unregister( ( Authenticator ) j.next() );
            }
        }

        authenticators.clear();
    }


    /**
     * Initializes the specified {@link Authenticator} and registers it to
     * this service.
     */
    private void register( AuthenticatorConfiguration cfg ) throws NamingException
    {
        cfg.getAuthenticator().init( factoryCfg, cfg );

        Collection authenticatorList = getAuthenticators( cfg.getAuthenticator().getAuthenticatorType() );
        if ( authenticatorList == null )
        {
            authenticatorList = new ArrayList();
            authenticators.put( cfg.getAuthenticator().getAuthenticatorType(), authenticatorList );
        }

        authenticatorList.add( cfg.getAuthenticator() );
    }


    /**
     * Deinitializes the specified {@link Authenticator} and deregisters it from
     * this service.
     */
    private void unregister( Authenticator authenticator )
    {
        Collection authenticatorList = getAuthenticators( authenticator.getAuthenticatorType() );

        if ( authenticatorList == null )
        {
            return;
        }

        authenticatorList.remove( authenticator );

        try
        {
            authenticator.destroy();
        }
        catch ( Throwable t )
        {
            log.warn( "Failed to destroy an authenticator.", t );
        }
    }


    /**
     * Returns the list of {@link Authenticator}s with the specified type.
     * 
     * @return <tt>null</tt> if no authenticator is found.
     */
    private Collection getAuthenticators( String type )
    {
        Collection result = ( Collection ) authenticators.get( type );
        if ( result != null && result.size() > 0 )
        {
            return result;
        }
        else
        {
            return null;
        }
    }


    public void add( NextInterceptor next, String upName, Name normName, Attributes entry ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Adding the entry " + AttributeUtils.toString( entry ) + " for DN = '" + upName + "'" );
        }

        checkAuthenticated();
        next.add( upName, normName, entry );
    }


    public void delete( NextInterceptor next, Name name ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Deleting name = '" + name.toString() + "'" );
        }

        checkAuthenticated();
        next.delete( name );
    }


    public Name getMatchedName( NextInterceptor next, Name dn, boolean normalized ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Matching name = '" + dn.toString() + "'" );
        }

        checkAuthenticated();
        return next.getMatchedName( dn, normalized );
    }


    public Attributes getRootDSE( NextInterceptor next ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Getting root DSE" );
        }

        checkAuthenticated();
        return next.getRootDSE();
    }


    public Name getSuffix( NextInterceptor next, Name dn, boolean normalized ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Getting suffix for name = '" + dn.toString() + "'" );
        }

        checkAuthenticated();
        return next.getSuffix( dn, normalized );
    }


    public boolean hasEntry( NextInterceptor next, Name name ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Testing if entry name = '" + name.toString() + "' exists" );
        }

        checkAuthenticated();
        return next.hasEntry( name );
    }


    public boolean isSuffix( NextInterceptor next, Name name ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Testing suffix for name = '" + name.toString() + "'" );
        }

        checkAuthenticated();
        return next.isSuffix( name );
    }


    public NamingEnumeration list( NextInterceptor next, Name base ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Listing base = '" + base.toString() + "'" );
        }

        checkAuthenticated();
        return next.list( base );
    }


    public Iterator listSuffixes( NextInterceptor next, boolean normalized ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Listing suffixes" );
        }

        checkAuthenticated();
        return next.listSuffixes( normalized );
    }


    public Attributes lookup( NextInterceptor next, Name dn, String[] attrIds ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Lookup name = '" + dn.toString() + "', attributes = " + attrIds );
        }

        checkAuthenticated();
        return next.lookup( dn, attrIds );
    }


    public Attributes lookup( NextInterceptor next, Name name ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Lookup name = '" + name.toString() + "'" );
        }

        checkAuthenticated();
        return next.lookup( name );
    }


    public void modify( NextInterceptor next, Name name, int modOp, Attributes mods ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Modifying name = '" + name.toString() + "', modifs = " + AttributeUtils.toString( mods ) );
        }

        checkAuthenticated();
        next.modify( name, modOp, mods );
    }


    public void modify( NextInterceptor next, Name name, ModificationItem[] mods ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Modifying name = '" + name.toString() + "'" );
        }

        checkAuthenticated();
        next.modify( name, mods );
    }


    public void modifyRn( NextInterceptor next, Name name, String newRn, boolean deleteOldRn ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Modifying name = '" + name.toString() + "', new RDN = '" + newRn + "', oldRDN = '"
                + deleteOldRn + "'" );
        }

        checkAuthenticated();
        next.modifyRn( name, newRn, deleteOldRn );
    }


    public void move( NextInterceptor next, Name oriChildName, Name newParentName, String newRn, boolean deleteOldRn )
        throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Moving name = '" + oriChildName.toString() + "' to name = '" + newParentName + "', new RDN = '"
                + newRn + "', oldRDN = '" + deleteOldRn + "'" );
        }

        checkAuthenticated();
        next.move( oriChildName, newParentName, newRn, deleteOldRn );
    }


    public void move( NextInterceptor next, Name oriChildName, Name newParentName ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Moving name = '" + oriChildName.toString() + " to name = '" + newParentName + "'" );
        }

        checkAuthenticated();
        next.move( oriChildName, newParentName );
    }


    public NamingEnumeration search( NextInterceptor next, Name base, Map env, ExprNode filter,
        SearchControls searchCtls ) throws NamingException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "Search for base = '" + base.toString() + "'" );
        }

        checkAuthenticated();
        return next.search( base, env, filter, searchCtls );
    }


    private void checkAuthenticated() throws NamingException
    {
        ServerContext ctx = ( ServerContext ) InvocationStack.getInstance().peek().getCaller();

        if ( ctx.getPrincipal() != null )
        {
            if ( ctx.getEnvironment().containsKey( Context.SECURITY_CREDENTIALS ) )
            {
                ctx.removeFromEnvironment( Context.SECURITY_CREDENTIALS );
            }
            return;
        }

        throw new IllegalStateException( "Attempted operation by unauthenticated caller." );
    }


    public void bind( NextInterceptor next, Name bindDn, byte[] credentials, List mechanisms, String saslAuthId )
        throws NamingException
    {
        // check if we are already authenticated and if so we return making
        // sure first that the credentials are not exposed within context
        ServerContext ctx = ( ServerContext ) InvocationStack.getInstance().peek().getCaller();

        if ( ctx.getPrincipal() != null )
        {
            if ( ctx.getEnvironment().containsKey( Context.SECURITY_CREDENTIALS ) )
            {
                ctx.removeFromEnvironment( Context.SECURITY_CREDENTIALS );
            }
            return;
        }

        // pick the first matching authenticator type
        Collection authenticators = null;
        for ( int ii = 0; ii < mechanisms.size(); ii++ )
        {
            authenticators = getAuthenticators( ( String ) mechanisms.get( ii ) );

            if ( authenticators != null )
            {
                break;
            }
        }

        if ( authenticators == null )
        {
            log.debug( "No authenticators found, delegating bind to the nexus." );
            // as a last resort try binding via the nexus
            next.bind( bindDn, credentials, mechanisms, saslAuthId );
            log.debug( "Nexus succeeded on bind operation." );
            // bind succeeded if we got this far 
            ctx.setPrincipal( new TrustedPrincipalWrapper( new LdapPrincipal( bindDn, LdapJndiProperties
                .getAuthenticationLevel( ctx.getEnvironment() ) ) ) );
            // remove creds so there is no security risk
            ctx.removeFromEnvironment( Context.SECURITY_CREDENTIALS );
            return;
        }

        // try each authenticators
        for ( Iterator i = authenticators.iterator(); i.hasNext(); )
        {
            Authenticator authenticator = ( Authenticator ) i.next();
            try
            {
                // perform the authentication
                LdapPrincipal authorizationId = authenticator.authenticate( ctx );
                // authentication was successful
                ctx.setPrincipal( new TrustedPrincipalWrapper( authorizationId ) );
                // remove creds so there is no security risk
                ctx.removeFromEnvironment( Context.SECURITY_CREDENTIALS );
                return;
            }
            catch ( LdapAuthenticationException e )
            {
                // authentication failed, try the next authenticator
                if ( log.isInfoEnabled() )
                {
                    log.info( "Authenticator " + authenticator.getClass() + " failed to authenticate " + bindDn );
                }
            }
            catch ( Exception e )
            {
                // Log other exceptions than LdapAuthenticationException
                if ( log.isWarnEnabled() )
                {
                    log.warn( "Unexpected exception from " + authenticator.getClass() + " for principal " + bindDn, e );
                }
            }
        }

        throw new LdapAuthenticationException();
    }

    /**
     * FIXME This doesn't secure anything actually.
     * 
     * Created this wrapper to pass to ctx.setPrincipal() which is public for added
     * security.  This adds more security because an instance of this class is not
     * easily accessible whereas LdapPrincipals can be accessed easily from a context
     * althought they cannot be instantiated outside of the authn package.  Malicious
     * code may not be able to set the principal to what they would like but they
     * could switch existing principals using the now public ServerContext.setPrincipal()
     * method.  To avoid this we make sure that this metho takes a TrustedPrincipalWrapper
     * as opposed to the LdapPrincipal.  Only this service can create and call setPrincipal
     * with a TrustedPrincipalWrapper.
     */
    public final class TrustedPrincipalWrapper
    {
        /** the wrapped ldap principal */
        private final LdapPrincipal principal;


        /**
         * Creates a TrustedPrincipalWrapper around an LdapPrincipal.
         *
         * @param principal the LdapPrincipal to wrap
         */
        private TrustedPrincipalWrapper(LdapPrincipal principal)
        {
            this.principal = principal;
        }


        /**
         * Gets the LdapPrincipal this TrustedPrincipalWrapper wraps.
         *
         * @return the wrapped LdapPrincipal
         */
        public LdapPrincipal getPrincipal()
        {
            return principal;
        }
    }
}