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
package org.apache.directory.server.schema.registries;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.MatchingRuleUse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A plain old java object implementation of an MatchingRuleUseRegistry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class DefaultMatchingRuleUseRegistry implements MatchingRuleUseRegistry
{
    /** static class logger */
    private final static Logger log = LoggerFactory.getLogger( DefaultMatchingRuleUseRegistry.class );
    /** maps a name to an MatchingRuleUse */
    private final Map<String,MatchingRuleUse> byName;
    /** maps a MatchingRuleUse name to a schema name*/
    private final Map<String,String> nameToSchema;


    // ------------------------------------------------------------------------
    // C O N S T R U C T O R S
    // ------------------------------------------------------------------------

    
    /**
     * Creates an empty BootstrapMatchingRuleUseRegistry.
     */
    public DefaultMatchingRuleUseRegistry()
    {
        this.byName = new HashMap<String,MatchingRuleUse>();
        this.nameToSchema = new HashMap<String,String>();
    }


    // ------------------------------------------------------------------------
    // Service Methods
    // ------------------------------------------------------------------------

    
    public void register( String schema, MatchingRuleUse matchingRuleUse ) throws NamingException
    {
        if ( byName.containsKey( matchingRuleUse.getName() ) )
        {
            NamingException e = new NamingException( "matchingRuleUse w/ name " + matchingRuleUse.getName()
                + " has already been registered!" );
            throw e;
        }

        nameToSchema.put( matchingRuleUse.getName(), schema );
        byName.put( matchingRuleUse.getName(), matchingRuleUse );
        if ( log.isDebugEnabled() )
        {
            log.debug( "registed matchingRuleUse: " + matchingRuleUse );
        }
    }


    public MatchingRuleUse lookup( String name ) throws NamingException
    {
        if ( !byName.containsKey( name ) )
        {
            NamingException e = new NamingException( "matchingRuleUse w/ name " + name + " not registered!" );
            throw e;
        }

        MatchingRuleUse matchingRuleUse = byName.get( name );
        if ( log.isDebugEnabled() )
        {
            log.debug( "lookup with name '"+ name + "' of matchingRuleUse: " + matchingRuleUse );
        }
        return matchingRuleUse;
    }


    public boolean hasMatchingRuleUse( String name )
    {
        return byName.containsKey( name );
    }


    public String getSchemaName( String id ) throws NamingException
    {
        if ( nameToSchema.containsKey( id ) )
        {
            return ( String ) nameToSchema.get( id );
        }

        throw new NamingException( "Name " + id + " not found in name to " + "schema name map!" );
    }


    public Iterator list()
    {
        return byName.values().iterator();
    }
}