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
package org.apache.ldap.server.authn;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.apache.ldap.common.exception.LdapAuthenticationException;
import org.apache.ldap.common.exception.LdapAuthenticationNotSupportedException;
import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.message.ResultCodeEnum;
import org.apache.ldap.common.util.StringTools;
import org.apache.ldap.server.DirectoryServiceConfiguration;
import org.apache.ldap.server.configuration.AuthenticatorConfiguration;
import org.apache.ldap.server.configuration.InterceptorConfiguration;
import org.apache.ldap.server.interceptor.BaseInterceptor;
import org.apache.ldap.server.interceptor.Interceptor;
import org.apache.ldap.server.interceptor.NextInterceptor;
import org.apache.ldap.server.invocation.InvocationStack;
import org.apache.ldap.server.jndi.ServerContext;
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
        while( i.hasNext() )
        {
            try
            {
                this.register( ( AuthenticatorConfiguration ) i.next() );
            }
            catch ( Exception e )
            {
                destroy();
                throw ( NamingException ) new NamingException(
                        "Failed to register authenticator." ).initCause( e );
            }
        }
    }
    
    /**
     * Deinitializes and deregisters all {@link Authenticator}s from this service.
     */
    public void destroy()
    {
        Iterator i = new ArrayList( authenticators.values() ).iterator();
        while( i.hasNext() )
        {
            Iterator j = new ArrayList( ( Collection ) i.next() ).iterator();
            while( j.hasNext() )
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
        catch( Throwable t )
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
        if( result != null && result.size() > 0 )
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
        authenticate();
        next.add( upName, normName, entry );
    }


    public void delete( NextInterceptor next, Name name ) throws NamingException
    {
        authenticate();
        next.delete( name );
    }


    public Name getMatchedName( NextInterceptor next, Name dn, boolean normalized ) throws NamingException
    {
        authenticate();
        return next.getMatchedName( dn, normalized );
    }


    public Attributes getRootDSE( NextInterceptor next ) throws NamingException
    {
        authenticate();
        return next.getRootDSE();
    }


    public Name getSuffix( NextInterceptor next, Name dn, boolean normalized ) throws NamingException
    {
        authenticate();
        return next.getSuffix( dn, normalized );
    }


    public boolean hasEntry( NextInterceptor next, Name name ) throws NamingException
    {
        authenticate();
        return next.hasEntry( name );
    }


    public boolean isSuffix( NextInterceptor next, Name name ) throws NamingException
    {
        authenticate();
        return next.isSuffix( name );
    }


    public NamingEnumeration list( NextInterceptor next, Name base ) throws NamingException
    {
        authenticate();
        return next.list( base );
    }


    public Iterator listSuffixes( NextInterceptor next, boolean normalized ) throws NamingException
    {
        authenticate();
        return next.listSuffixes( normalized );
    }


    public Attributes lookup( NextInterceptor next, Name dn, String[] attrIds ) throws NamingException
    {
        authenticate();
        return next.lookup( dn, attrIds );
    }


    public Attributes lookup( NextInterceptor next, Name name ) throws NamingException
    {
        authenticate();
        return next.lookup( name );
    }


    public void modify( NextInterceptor next, Name name, int modOp, Attributes mods ) throws NamingException
    {
        authenticate();
        next.modify( name, modOp, mods );
    }


    public void modify( NextInterceptor next, Name name, ModificationItem[] mods ) throws NamingException
    {
        authenticate();
        next.modify( name, mods );
    }


    public void modifyRn( NextInterceptor next, Name name, String newRn, boolean deleteOldRn ) throws NamingException
    {
        authenticate();
        next.modifyRn( name, newRn, deleteOldRn );
    }


    public void move( NextInterceptor next, Name oriChildName, Name newParentName, String newRn, boolean deleteOldRn ) throws NamingException
    {
        authenticate();
        next.move( oriChildName, newParentName, newRn, deleteOldRn );
    }


    public void move( NextInterceptor next, Name oriChildName, Name newParentName ) throws NamingException
    {
        authenticate();
        next.move( oriChildName, newParentName );
    }


    public NamingEnumeration search( NextInterceptor next, Name base, Map env, ExprNode filter, SearchControls searchCtls ) throws NamingException
    {
        authenticate();
        return next.search( base, env, filter, searchCtls );
    }


    private void authenticate() throws NamingException
    {
        // check if we are already authenticated and if so we return making
        // sure first that the credentials are not exposed within context
        ServerContext ctx =
            ( ServerContext ) InvocationStack.getInstance().peek().getCaller();

        if ( ctx.getPrincipal() != null )
        {
            if ( ctx.getEnvironment().containsKey( Context.SECURITY_CREDENTIALS ) )
            {
                ctx.removeFromEnvironment( Context.SECURITY_CREDENTIALS );
            }
            return;
        }

        String authList = ( String ) ctx.getEnvironment().get( Context.SECURITY_AUTHENTICATION );

        if ( authList == null )
        {
            if ( ctx.getEnvironment().containsKey( Context.SECURITY_CREDENTIALS ) )
            {
                // authentication type is simple here

                authList = "simple";
            }
            else
            {
                // authentication type is anonymous

                authList = "none";
            }

        }

        authList = StringTools.deepTrim( authList );

        String[] auth = authList.split( " " );

        Collection authenticators = null;

        // pick the first matching authenticator type

        for ( int i=0; i<auth.length; i++)
        {
            authenticators = getAuthenticators( auth[i] );

            if ( authenticators != null )
            {
                break;
            }
        }

        if ( authenticators == null )
        {
            ctx.getEnvironment(); // shut's up idea's yellow light

            ResultCodeEnum rc = ResultCodeEnum.AUTHMETHODNOTSUPPORTED;

            throw new LdapAuthenticationNotSupportedException( rc );
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
            catch( LdapAuthenticationException e )
            {
                // authentication failed, try the next authenticator
            }
            catch( Exception e )
            {
                // Log other exceptions than LdapAuthenticationException
                log.warn( "Unexpected exception from " + authenticator.getClass(), e );
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
        private TrustedPrincipalWrapper( LdapPrincipal principal )
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