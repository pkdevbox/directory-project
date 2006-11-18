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
package org.apache.ldap.common.berlib.asn1.decoder.search ;


import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.SearchResponseEntryImpl;


/**
 * A digester rule which fires to build SearchResponseEntry containment trees.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class SearchResponseEntryRule extends AbstractRule
{
    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass ) ;

        LdapTag tag = LdapTag.getLdapTagById( id ) ;

        if ( LdapTag.SEARCH_RESULT_ENTRY != tag )
        {
            throw new IllegalArgumentException(
                    "Expected a SEARCH_RESULT_ENTRY tag id but got a " + tag ) ;
        }

        Object req = new SearchResponseEntryImpl( getDigester().popInt() ) ;
        getDigester().push( req ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.asn1.ber.digester.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;
        getDigester().pop() ;
    }
}