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
package org.apache.directory.server.core.schema;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.server.core.schema.bootstrap.BootstrapObjectClassRegistry;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.util.JoinIterator;


/**
 * A plain old java object implementation of an ObjectClassRegistry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class GlobalObjectClassRegistry implements ObjectClassRegistry
{
    /** maps an OID to an ObjectClass */
    private final Map byOid;
    /** maps an OID to a schema name*/
    private final Map oidToSchema;
    /** the registry used to resolve names to OIDs */
    private final OidRegistry oidRegistry;
    /** monitor notified via callback events */
    private ObjectClassRegistryMonitor monitor;
    /** the underlying bootstrap registry to delegate on misses to */
    private BootstrapObjectClassRegistry bootstrap;


    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------

    /**
     * Creates an empty BootstrapObjectClassRegistry.
     */
    public GlobalObjectClassRegistry(BootstrapObjectClassRegistry bootstrap, OidRegistry oidRegistry)
    {
        this.byOid = new HashMap();
        this.oidToSchema = new HashMap();
        this.oidRegistry = oidRegistry;
        this.monitor = new ObjectClassRegistryMonitorAdapter();

        this.bootstrap = bootstrap;
        if ( this.bootstrap == null )
        {
            throw new NullPointerException( "the bootstrap registry cannot be null" );
        }
    }


    /**
     * Sets the monitor that is to be notified via callback events.
     *
     * @param monitor the new monitor to notify of notable events
     */
    public void setMonitor( ObjectClassRegistryMonitor monitor )
    {
        this.monitor = monitor;
    }


    // ------------------------------------------------------------------------
    // Service Methods
    // ------------------------------------------------------------------------

    public void register( String schema, ObjectClass dITContentRule ) throws NamingException
    {
        if ( byOid.containsKey( dITContentRule.getOid() ) || bootstrap.hasObjectClass( dITContentRule.getOid() ) )
        {
            NamingException e = new NamingException( "dITContentRule w/ OID " + dITContentRule.getOid()
                + " has already been registered!" );
            monitor.registerFailed( dITContentRule, e );
            throw e;
        }

        oidRegistry.register( dITContentRule.getName(), dITContentRule.getOid() );
        byOid.put( dITContentRule.getOid(), dITContentRule );
        oidToSchema.put( dITContentRule.getOid(), schema );
        monitor.registered( dITContentRule );
    }


    public ObjectClass lookup( String id ) throws NamingException
    {
        id = oidRegistry.getOid( id );

        if ( byOid.containsKey( id ) )
        {
            ObjectClass dITContentRule = ( ObjectClass ) byOid.get( id );
            monitor.lookedUp( dITContentRule );
            return dITContentRule;
        }

        if ( bootstrap.hasObjectClass( id ) )
        {
            ObjectClass dITContentRule = bootstrap.lookup( id );
            monitor.lookedUp( dITContentRule );
            return dITContentRule;
        }

        NamingException e = new NamingException( "dITContentRule w/ OID " + id + " not registered!" );
        monitor.lookupFailed( id, e );
        throw e;
    }


    public boolean hasObjectClass( String id )
    {
        if ( oidRegistry.hasOid( id ) )
        {
            try
            {
                return byOid.containsKey( oidRegistry.getOid( id ) ) || bootstrap.hasObjectClass( id );
            }
            catch ( NamingException e )
            {
                return false;
            }
        }

        return false;
    }


    public String getSchemaName( String id ) throws NamingException
    {
        id = oidRegistry.getOid( id );

        if ( oidToSchema.containsKey( id ) )
        {
            return ( String ) oidToSchema.get( id );
        }

        if ( bootstrap.hasObjectClass( id ) )
        {
            return bootstrap.getSchemaName( id );
        }

        throw new NamingException( "OID " + id + " not found in oid to " + "schema name map!" );
    }


    public Iterator list()
    {
        return new JoinIterator( new Iterator[]
            { byOid.values().iterator(), bootstrap.list() } );
    }
}