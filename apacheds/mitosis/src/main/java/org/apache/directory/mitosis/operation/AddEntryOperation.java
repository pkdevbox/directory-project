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
package org.apache.directory.mitosis.operation;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.apache.directory.server.core.partition.PartitionNexus;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.NamespaceTools;
import org.apache.directory.mitosis.common.CSN;
import org.apache.directory.mitosis.operation.support.EntryUtil;
import org.apache.directory.mitosis.store.ReplicationStore;

/**
 * An {@link Operation} that adds a new entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddEntryOperation extends Operation
{
    private static final long serialVersionUID = 2294492811671880570L;

    private final Name normalizedName;
    private final String userProvidedName;
    private final Attributes entry;

    /**
     * Creates a new instance.
     * 
     * @param entry an entry
     */
    public AddEntryOperation( CSN csn, Name normalizedName, String userProvidedName, Attributes entry )
    {
        super( csn );

        assert normalizedName != null;
        assert userProvidedName != null;
        assert entry != null;
        
        this.normalizedName = normalizedName;
        this.userProvidedName = userProvidedName;
        this.entry = ( Attributes ) entry.clone();
    }
    
    public String toString()
    {
        return super.toString() + ": [" + normalizedName + "].new( " + entry + " )";
    }

    protected void execute0( PartitionNexus nexus, ReplicationStore store ) throws NamingException
    {
        if( !EntryUtil.isEntryUpdatable( nexus, (LdapDN)normalizedName, getCSN() ) )
        {
            return;
        }
        EntryUtil.createGlueEntries( nexus, (LdapDN)normalizedName, false );
        
        // Replace the entry if an entry with the same name exists.
        Attributes oldEntry = nexus.lookup( (LdapDN)normalizedName );
        if( oldEntry != null )
        {
            // Find the attributes that new entry doesn't have.
            Attributes attrsToRemove = ( Attributes ) oldEntry.clone();
            NamingEnumeration e = oldEntry.getAll();
            while( e.hasMore() )
            {
                Attribute attr = ( Attribute ) e.next();
                String attrID = attr.getID();
                if( entry.get( attrID ) != null )
                {
                    attrsToRemove.remove( attrID );
                }
            }
            
            // Don't let RN attribute be removed
            String rnAttrID = NamespaceTools.getRdnAttribute(
                    normalizedName.get( normalizedName.size() - 1 ) );
            attrsToRemove.remove( rnAttrID );
            
            // Delete the attributes.
            nexus.modify( (LdapDN)normalizedName, DirContext.REMOVE_ATTRIBUTE, entry );

            // Remove RN attribute from new entry because it should be the same
            // with the old one.
            entry.remove( rnAttrID );

            // Now replace old entries with the new attributes
            nexus.modify( (LdapDN)normalizedName, DirContext.REPLACE_ATTRIBUTE, entry );
        }
        else
        {
            String rdn = normalizedName.get( normalizedName.size() - 1 );
            // Remove the attribute first in case we're using a buggy 
            // LockableAttributesImpl which doesn't replace old attributes
            // when we put a new one.
            entry.remove( NamespaceTools.getRdnAttribute( rdn ) );
            entry.put(
                    NamespaceTools.getRdnAttribute( rdn ),
                    NamespaceTools.getRdnValue( rdn ) );
            nexus.add( (LdapDN)normalizedName, entry );
        }
    }
}