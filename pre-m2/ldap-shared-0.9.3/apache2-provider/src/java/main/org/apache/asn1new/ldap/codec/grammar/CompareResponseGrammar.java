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
package org.apache.asn1new.ldap.codec.grammar;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.ber.containers.IAsn1Container;
import org.apache.asn1new.ber.grammar.AbstractGrammar;
import org.apache.asn1new.ber.grammar.GrammarAction;
import org.apache.asn1new.ber.grammar.GrammarTransition;
import org.apache.asn1new.ber.grammar.IGrammar;
import org.apache.asn1new.ber.tlv.UniversalTag;
import org.apache.asn1new.ldap.codec.LdapConstants;
import org.apache.asn1new.ldap.codec.LdapMessageContainer;
import org.apache.asn1new.ldap.pojo.CompareResponse;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the CompareResponse LDAP message. All the actions are declared in this
 * class. As it is a singleton, these declaration are only done once.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompareResponseGrammar extends AbstractGrammar implements IGrammar
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( CompareResponseGrammar.class );

    /** The instance of grammar. CompareResponseGrammar is a singleton */
    private static IGrammar instance = new CompareResponseGrammar();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new CompareResponseGrammar object.
     */
    private CompareResponseGrammar()
    {
        name = CompareResponseGrammar.class.getName();
        statesEnum = LdapStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[LdapStatesEnum.LAST_COMPARE_RESPONSE_STATE][256];

        //============================================================================================
        // CompareResponse Message
        //============================================================================================
        // LdapMessage ::= ... CompareResponse ...
        // CompareResponse ::= [APPLICATION 15] LDAPResult (Tag)
        // Nothing to do.
        super.transitions[LdapStatesEnum.COMPARE_RESPONSE_TAG][LdapConstants.COMPARE_RESPONSE_TAG] = new GrammarTransition(
                LdapStatesEnum.COMPARE_RESPONSE_TAG, LdapStatesEnum.COMPARE_RESPONSE_VALUE, null );

        // LdapMessage ::= ... CompareResponse ...
        // CompareResponse ::= [APPLICATION 15] LDAPResult (Value)
        // The next Tag will be the LDAPResult Tag (0x0A).
        // We will switch the grammar then.
        super.transitions[LdapStatesEnum.COMPARE_RESPONSE_VALUE][LdapConstants.COMPARE_RESPONSE_TAG] = new GrammarTransition(
                LdapStatesEnum.COMPARE_RESPONSE_VALUE, LdapStatesEnum.COMPARE_RESPONSE_LDAP_RESULT, 
                new GrammarAction( "Init CompareResponse" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage      ldapMessage          =
                            ldapMessageContainer.getLdapMessage();

                        // Now, we can allocate the CompareResponse Object
                        // And we associate it to the ldapMessage Object
                        ldapMessage.setProtocolOP( new CompareResponse() );

                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Compare response " );
                        }
                    }
                } );

        // LdapMessage ::= ... CompareResponse ...
        // CompareResponse ::= [APPLICATION 15] LDAPResult (Value)
        // Ok, we have a LDAPResult Tag (0x0A). So we have to switch the grammar.
        super.transitions[LdapStatesEnum.COMPARE_RESPONSE_LDAP_RESULT][UniversalTag.ENUMERATED_TAG] = new GrammarTransition(
                LdapStatesEnum.COMPARE_RESPONSE_LDAP_RESULT, LdapStatesEnum.LDAP_RESULT_GRAMMAR_SWITCH, null );
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the instance of this grammar
     *
     * @return An instance on the LdapMessage Grammar
     */
    public static IGrammar getInstance()
    {
        return instance;
    }
}