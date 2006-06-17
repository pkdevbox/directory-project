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

package org.apache.dns;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.spi.InitialContextFactory;

import org.apache.directory.server.dns.DnsConfiguration;
import org.apache.directory.server.dns.DnsServer;
import org.apache.directory.server.dns.store.JndiRecordStoreImpl;
import org.apache.directory.server.dns.store.RecordStore;
import org.apache.directory.server.protocol.shared.MapAdapter;
import org.apache.felix.servicebinder.Lifecycle;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.transport.socket.nio.DatagramAcceptor;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DnsServerFactory implements ManagedServiceFactory, Lifecycle
{
    /** the log for this class */
    private static final Logger log = LoggerFactory.getLogger( DnsServerFactory.class );

    private static final String DEFAULT_PID = "org.apache.dns.default";

    private ConfigurationAdmin cm;
    private IoAcceptor acceptor = new DatagramAcceptor();
    private InitialContextFactory factory;

    private Map servers = Collections.synchronizedMap( new HashMap() );

    public void updated( String pid, Dictionary config ) throws ConfigurationException
    {
        log.debug( getName() + " (" + pid + ") updating with " + config );

        DnsConfiguration dnsConfig = new DnsConfiguration( new MapAdapter( config ) );

        synchronized ( servers )
        {
            if ( DEFAULT_PID.equals( pid ) && servers.size() > 0 )
            {
                return;
            }

            // As soon as we get a "non-default"-config, delete default
            if ( !pid.equals( DEFAULT_PID ) )
            {
                deleted( DEFAULT_PID );
            }

            // For a given pid, do we have the service?
            DnsServer dnsServer = (DnsServer) servers.get( pid );

            // If we don't have the service, create it with the config.
            // Or, if we do have the service, re-create it if the config is different.
            if ( dnsServer == null || dnsServer.isDifferent( config ) )
            {
                deleted( pid );
                RecordStore store = new JndiRecordStoreImpl( dnsConfig, factory );
                dnsServer = new DnsServer( dnsConfig, acceptor, store );
                servers.put( pid, dnsServer );
            }
        }
    }

    public void deleted( String pid )
    {
        synchronized ( servers )
        {
            DnsServer dnsServer = (DnsServer) servers.remove( pid );

            if ( dnsServer != null )
            {
                dnsServer.destroy();
            }
        }
    }

    public String getName()
    {
        return "Apache DNS Service Factory";
    }

    /**
     * All required services have been bound, but our service(s) are not yet
     * registered.  If there is no Config Admin we start a server with default properties.
     */
    public void activate()
    {
        try
        {
            if ( cm == null )
            {
                updated( DEFAULT_PID, new Hashtable( DnsConfiguration.getDefaultConfig() ) );
            }
        }
        catch ( Exception e )
        {
            log.error( e.getMessage(), e );
        }
        finally
        {
            cm = null;
        }
    }

    /**
     * Invalidation has started and our services have been unregistered, but
     * any required services have not been unbound yet.
     */
    public void deactivate()
    {
        synchronized ( servers )
        {
            Iterator it = servers.values().iterator();

            while ( it.hasNext() )
            {
                DnsServer dnsServer = (DnsServer) it.next();
                dnsServer.destroy();
            }

            servers.clear();
        }
    }

    public void setInitialContextFactory( InitialContextFactory factory )
    {
        this.factory = factory;
        log.debug( getName() + " has bound to " + factory );
    }

    public void unsetInitialContextFactory( InitialContextFactory factory )
    {
        this.factory = null;
        log.debug( getName() + " has unbound from " + factory );
    }

    public void setConfigurationAdmin( ConfigurationAdmin cm )
    {
        this.cm = cm;
        log.debug( getName() + " has bound to " + cm );
    }

    public void unsetConfigurationAdmin( ConfigurationAdmin cm )
    {
        this.cm = null;
        log.debug( getName() + " has unbound from " + cm );
    }
}