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
package org.apache.ldap.server.partition.impl.btree;


import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.ldap.common.filter.ExprNode;
import org.apache.ldap.common.filter.SubstringNode;
import org.apache.ldap.common.schema.AttributeType;
import org.apache.ldap.common.schema.Normalizer;
import org.apache.ldap.server.schema.AttributeTypeRegistry;
import org.apache.ldap.server.schema.OidRegistry;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * Evaluates substring filter assertions on an entry.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SubstringEvaluator implements Evaluator
{
    /** Database used while evaluating candidates */
    private BTreeDirectoryPartition db;
    /** Oid Registry used to translate attributeIds to OIDs */
    private OidRegistry oidRegistry;
    /** AttributeType registry needed for normalizing and comparing values */
    private AttributeTypeRegistry attributeTypeRegistry;


    /**
     * Creates a new SubstringEvaluator for substring expressions.
     *
     * @param db the database this evaluator uses
     * @param oidRegistry the OID registry for name to OID mapping
     * @param attributeTypeRegistry the attributeType registry
     */
    public SubstringEvaluator( BTreeDirectoryPartition db, OidRegistry oidRegistry,
                               AttributeTypeRegistry attributeTypeRegistry )
    {
        this.db = db;
        this.oidRegistry = oidRegistry;
        this.attributeTypeRegistry = attributeTypeRegistry;
    }


    /**
     * @see org.apache.ldap.server.partition.impl.btree.Evaluator#evaluate(ExprNode, IndexRecord)
     */
    public boolean evaluate( ExprNode node, IndexRecord record )
        throws NamingException
    {
        RE regex = null; 
        SubstringNode snode = ( SubstringNode ) node;
        String oid = oidRegistry.getOid( snode.getAttribute() );
        AttributeType type = attributeTypeRegistry.lookup( oid );
        Normalizer normalizer = type.getSubstr().getNormalizer();

        if ( db.hasUserIndexOn( snode.getAttribute() ) )
        {
            Index idx = db.getUserIndex( snode.getAttribute() );
        
            /*
             * Note that this is using the reverse half of the index giving a 
             * considerable performance improvement on this kind of operation.
             * Otherwise we would have to scan the entire index if there were
             * no reverse lookups.
             */
        
            NamingEnumeration list = idx.listReverseIndices( record.getEntryId() );

            // compile the regular expression to search for a matching attribute
            try 
            {
                regex = snode.getRegex( normalizer );
            } 
            catch ( RESyntaxException e ) 
            {
                NamingException ne = new NamingException( "SubstringNode '" 
                    + node + "' had " + "incorrect syntax" );
                ne.setRootCause( e );
                throw ne;
            }

            // cycle through the attribute values testing for a match
            while ( list.hasMore() ) 
            {
                IndexRecord rec = ( IndexRecord ) list.next();
            
                // once match is found cleanup and return true
                if ( regex.match( ( String ) rec.getIndexKey() ) ) 
                {
                    list.close();
                    return true;
                }
            }

            // we fell through so a match was not found - assertion was false.
            return false;
        }

        // --------------------------------------------------------------------
        // Index not defined beyond this point
        // --------------------------------------------------------------------
        
        // resusitate the entry if it has not been and set entry in IndexRecord
        if ( null == record.getAttributes() )
        {
            Attributes attrs = db.lookup( record.getEntryId() );
            record.setAttributes( attrs );
        }
        
        // get the attribute
        Attribute attr = record.getAttributes().get( snode.getAttribute() );
        
        // if the attribute does not exist just return false
        if ( null == attr )
        {
            return false;
        }

        // compile the regular expression to search for a matching attribute
        try 
        {
            regex = snode.getRegex( normalizer );
        } 
        catch ( RESyntaxException e ) 
        {
            NamingException ne = new NamingException( "SubstringNode '" 
                + node + "' had " + "incorrect syntax" );
            ne.setRootCause( e );
            throw ne;
        }
        
        /*
         * Cycle through the attribute values testing normalized version 
         * obtained from using the substring matching rule's normalizer.
         * The test uses the comparator obtained from the appropriate 
         * substring matching rule.
         */ 
        NamingEnumeration list = attr.getAll(); 
        while ( list.hasMore() ) 
        {
            String value = ( String ) 
                normalizer.normalize( list.next() );
            
            // Once match is found cleanup and return true
            if ( regex.match( value ) ) 
            {
                list.close();
                return true;
            }
        }

        // we fell through so a match was not found - assertion was false.
        return false;
    }
}