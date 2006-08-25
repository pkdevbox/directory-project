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
package org.apache.directory.shared.ldap.codec.del;


import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.grammar.IGrammar;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.LdapStatesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the DelResponse LDAP message. All the actions are
 * declared in this class. As it is a singleton, these declaration are only done
 * once.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DelResponseGrammar extends AbstractGrammar implements IGrammar
{
    // ~ Static fields/initializers
    // -----------------------------------------------------------------

    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( DelResponseGrammar.class );

    /** The instance of grammar. DelResponseGrammar is a singleton */
    private static IGrammar instance = new DelResponseGrammar();


    // ~ Constructors
    // -------------------------------------------------------------------------------

    /**
     * Creates a new DelResponseGrammar object.
     */
    private DelResponseGrammar()
    {
        name = DelResponseGrammar.class.getName();
        statesEnum = LdapStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[LdapStatesEnum.LAST_DEL_RESPONSE_STATE][256];

        // ============================================================================================
        // DelResponse Message
        // ============================================================================================
        // LdapMessage ::= ... DelResponse ...
        // DelResponse ::= [APPLICATION 11] LDAPResult (Tag)
        // Nothing to do.
        super.transitions[LdapStatesEnum.DEL_RESPONSE_TAG][LdapConstants.DEL_RESPONSE_TAG] = new GrammarTransition(
            LdapStatesEnum.DEL_RESPONSE_TAG, LdapStatesEnum.DEL_RESPONSE_VALUE, null );

        // LdapMessage ::= ... DelResponse ...
        // DelResponse ::= [APPLICATION 11] LDAPResult (Value)
        // The next Tag will be the LDAPResult Tag (0x0A).
        // We will switch the grammar then.
        super.transitions[LdapStatesEnum.DEL_RESPONSE_VALUE][LdapConstants.DEL_RESPONSE_TAG] = new GrammarTransition(
            LdapStatesEnum.DEL_RESPONSE_VALUE, LdapStatesEnum.DEL_RESPONSE_LDAP_RESULT, new GrammarAction(
                "Init DelResponse" )
            {
                public void action( IAsn1Container container )
                {

                    LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer ) container;
                    LdapMessage ldapMessage = ldapMessageContainer.getLdapMessage();

                    // Now, we can allocate the DelResponse Object

                    // And we associate it to the ldapMessage Object
                    ldapMessage.setProtocolOP( new DelResponse() );

                    log.debug( "Del response " );
                }
            } );

        // LdapMessage ::= ... DelResponse ...
        // DelResponse ::= [APPLICATION 11] LDAPResult (Value)
        // Ok, we have a LDAPResult Tag (0x0A). So we have to switch the
        // grammar.
        super.transitions[LdapStatesEnum.DEL_RESPONSE_LDAP_RESULT][UniversalTag.ENUMERATED_TAG] = new GrammarTransition(
            LdapStatesEnum.DEL_RESPONSE_LDAP_RESULT, LdapStatesEnum.LDAP_RESULT_GRAMMAR_SWITCH, new GrammarAction(
                "Pop allowed" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    container.grammarPopAllowed( true );
                }

            } );
    }


    // ~ Methods
    // ------------------------------------------------------------------------------------

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