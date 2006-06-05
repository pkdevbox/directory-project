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


import org.apache.asn1.ber.TagEnum;
import org.apache.asn1.ber.TypeClass;
import org.apache.asn1.ber.digester.AbstractRule;
import org.apache.ldap.common.berlib.asn1.LdapTag;
import org.apache.ldap.common.message.ReferralImpl;
import org.apache.ldap.common.message.SearchResponseReferenceImpl;


/**
 * A BERDigester Rule that creates and pushes a Referral onto the object Stack
 * to be populated by ReferralUrlRule invokations.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory
 * Project</a>
 * @version $Rev$
 */
public class SearchResponseReferralRule extends AbstractRule
{
    /** the tag to expect */
    private static final TagEnum TAG = LdapTag.SEARCH_RESULT_REFERENCE ;

    /**
     * Creates and sets the Referral for an SearchResponseReference to use.
     * It does so by first accessing the underlying SearchResponseReference
     * object which is expected to be the top object on the objectStack.
     * A peek is performed to get this obj.  It is then used to create the
     * Referral object which is pushed onto the stack.
     *
     * @see org.apache.asn1.ber.digester.Rule#tag(int, boolean,
     * org.apache.asn1.ber.TypeClass)
     */
    public void tag( int id, boolean isPrimitive, TypeClass typeClass )
    {
        super.tag( id, isPrimitive, typeClass ) ;

        if ( id != TAG.getTagId() )
        {
            throw new IllegalArgumentException(
                    "Expecting " + TAG.getName()
                    + " with an id of " + TAG.getTagId()
                    + " but instead got a tag id of " + id ) ;
        }

        SearchResponseReferenceImpl resp = ( SearchResponseReferenceImpl )
                getDigester().getRoot() ;
        ReferralImpl ref = new ReferralImpl( resp ) ;
        resp.setReferral( ref ) ;
        getDigester().push( ref ) ;
    }


    /* (non-Javadoc)
     * @see org.apache.snickers.ber.Rule#finish()
     */
    public void finish()
    {
        super.finish() ;
        getDigester().pop() ;
    }
}