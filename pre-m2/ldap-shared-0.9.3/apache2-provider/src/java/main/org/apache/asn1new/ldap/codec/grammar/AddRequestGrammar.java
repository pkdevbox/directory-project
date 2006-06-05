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

import javax.naming.InvalidNameException;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.ber.containers.IAsn1Container;
import org.apache.asn1new.ber.grammar.AbstractGrammar;
import org.apache.asn1new.ber.grammar.GrammarAction;
import org.apache.asn1new.ber.grammar.GrammarTransition;
import org.apache.asn1new.ber.grammar.IGrammar;
import org.apache.asn1new.ber.tlv.TLV;
import org.apache.asn1new.ber.tlv.UniversalTag;
import org.apache.asn1new.primitives.OctetString;
import org.apache.asn1new.util.StringUtils;
import org.apache.asn1new.ldap.codec.LdapConstants;
import org.apache.asn1new.ldap.codec.LdapMessageContainer;
import org.apache.asn1new.ldap.codec.primitives.LdapDN;
import org.apache.asn1new.ldap.codec.primitives.LdapString;
import org.apache.asn1new.ldap.codec.primitives.LdapStringEncodingException;
import org.apache.asn1new.ldap.pojo.AddRequest;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the AddRequest LDAP message. All the actions are declared in this
 * class. As it is a singleton, these declaration are only done once.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddRequestGrammar extends AbstractGrammar implements IGrammar
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( AddRequestGrammar.class );

    /** The instance of grammar. AddRequestGrammar is a singleton */
    private static IGrammar instance = new AddRequestGrammar();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new AddRequestGrammar object.
     */
    private AddRequestGrammar()
    {
        name       = AddRequestGrammar.class.getName();
        statesEnum = LdapStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[LdapStatesEnum.LAST_ADD_REQUEST_STATE][256];

        //============================================================================================
        // AddRequest Message
        //============================================================================================
        // LdapMessage ::= ... AddRequest ...
        // AddRequest ::= [APPLICATION 8] SEQUENCE { (Tag)
        // Nothing to do.
        super.transitions[LdapStatesEnum.ADD_REQUEST_TAG][LdapConstants.ADD_REQUEST_TAG] = new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_TAG, LdapStatesEnum.ADD_REQUEST_VALUE, null );

        // LdapMessage ::= ... AddRequest ...
        // AddRequest ::= [APPLICATION 8] SEQUENCE { (Value)
        // Create the structure
        super.transitions[LdapStatesEnum.ADD_REQUEST_VALUE][LdapConstants.ADD_REQUEST_TAG] = new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_VALUE, LdapStatesEnum.ADD_REQUEST_ENTRY_TAG,
                new GrammarAction( "Init addRequest" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();

                        // Now, we can allocate the ModifyRequest Object
                        // And we associate it to the ldapMessage Object
                        ldapMessage.setProtocolOP( new AddRequest() );
                    }
                } );

        // AddRequest ::= [APPLICATION 8] SEQUENCE {
        //    entry      LDAPDN, (Tag)
        //    ...
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ENTRY_TAG][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ENTRY_TAG, LdapStatesEnum.ADD_REQUEST_ENTRY_VALUE,
                null );

        // AddRequest ::= [APPLICATION 8] SEQUENCE {
        //    entry      LDAPDN, (Value)
        //    ...
        // Store the object name.
        super.transitions[LdapStatesEnum.ADD_REQUEST_ENTRY_VALUE][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ENTRY_VALUE, LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TAG,
                new GrammarAction( "Store add request object Value" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        AddRequest           addRequest           = ldapMessage.getAddRequest();

                        TLV                  tlv                  =
                            ldapMessageContainer.getCurrentTLV();

                        // Store the entry. It can't be null
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            throw new DecoderException( "The DN can't be null" );
                        }
                        else
                        {
                            try
                            {
                                addRequest.setEntry( new LdapDN(
                                        tlv.getValue().getData() ) );
                            }
                            catch ( InvalidNameException ine )
                            {
                                log.error( "The DN is invalid : " + StringUtils.dumpBytes(tlv.getValue().getData()) + " : " + ine.getMessage() );
                                throw new DecoderException( "Incorrect DN given : " + ine.getMessage() );
                            }
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Adding an entry with DN : " + addRequest.getEntry() );
                        }
                    }
                } );

        // AddRequest ::= [APPLICATION 8] SEQUENCE {
        //    ...
        //    attributes      AttributeList }
        // AttributeList ::= *SEQUENCE* OF SEQUENCE { (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TAG][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TAG,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALUE, null );

        // AddRequest ::= [APPLICATION 8] SEQUENCE {
        //    ...
        //    attributes      AttributeList }
        // AttributeList ::= *SEQUENCE* OF SEQUENCE { (Value)
        // Allocate the attributes ArrayList
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALUE][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALUE,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTE_TAG,
                new GrammarAction( "Init attributes array list" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        AddRequest           addRequest           = ldapMessage.getAddRequest();

                        addRequest.initAttributes();
                    }
                } );

        // AttributeList ::= SEQUENCE OF *SEQUENCE* { (Tag)
        //     ...
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTE_TAG][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTE_TAG,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTE_VALUE, null );

        // AttributeList ::= SEQUENCE OF *SEQUENCE* { (Tag)
        //     ...
        // Nothing to do
        // This is a loop, when dealing with more than one attribute
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_OR_ATTRIBUTE_OR_END][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_OR_ATTRIBUTE_OR_END,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTE_VALUE, null );

        // AttributeList ::= SEQUENCE OF *SEQUENCE* { (Value)
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTE_VALUE][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTE_VALUE,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TYPE_TAG, null );

        // AttributeList ::= SEQUENCE OF *SEQUENCE* {
        //    type    AttributeDescription, (Tag)
        //    ...
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TYPE_TAG][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TYPE_TAG,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TYPE_VALUE, null );

        // AttributeList ::= SEQUENCE OF *SEQUENCE* {
        //    type    AttributeDescription, (Value)
        //    ...
        // Store the attribute type.
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TYPE_VALUE][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_TYPE_VALUE,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALS_TAG,
                new GrammarAction( "Store attribute type" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        TLV                  tlv                  =
                            ldapMessageContainer.getCurrentTLV();

                        AddRequest           addRequest           = ldapMessage.getAddRequest();

                        // Store the type. It can't be null.
                        LdapString type = null;
                        
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            log.error( "Null types are not allowed" );
                            throw new DecoderException( "The type can't be null" );
                        }
                        else
                        {
                            try
                            {
                                type = new LdapString( tlv.getValue().getData() );
                                
                                addRequest.addAttributeType( type );
                            }
                            catch ( LdapStringEncodingException lsee )
                            {
                                log.error( "The type is invalid : " + StringUtils.dumpBytes( tlv.getValue().getData() ) + " : " + lsee.getMessage() );
                                throw new DecoderException( "Invalid attribute type : " + lsee.getMessage() );
                            }
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Adding type " + type );
                        }
                    }
                } );

        // AttributeTypeAndValues ::= SEQUENCE {
        //     ...
        //     vals    *SET OF* AttributeValue } (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALS_TAG][UniversalTag.SET_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALS_TAG,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALS_VALUE, null );

        // AttributeTypeAndValues ::= SEQUENCE {
        //     ...
        //     vals    *SET OF* AttributeValue } (Value)
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALS_VALUE][UniversalTag.SET_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VALS_VALUE,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_TAG, null );

        // AttributeTypeAndValues ::= SEQUENCE {
        //     ...
        //     vals    SET OF *AttributeValue* } (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_TAG][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_TAG,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_VALUE, null );

        // AttributeTypeAndValues ::= SEQUENCE {
        //     ...
        //     vals    SET OF *AttributeValue* } (Loop)
        // This is a loop, when dealing with multi-valued attributes
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_OR_ATTRIBUTE_OR_END][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_OR_ATTRIBUTE_OR_END,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_VALUE, null );

        // AttributeTypeAndValues ::= SEQUENCE {
        //     ...
        //     vals    SET OF AttributeValue }
        // AttributeValue ::= OCTET STRING (Value)
        // Store a new attribute value.
        super.transitions[LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_VALUE][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_VALUE,
                LdapStatesEnum.ADD_REQUEST_ATTRIBUTES_VAL_OR_ATTRIBUTE_OR_END,
                new GrammarAction( "Store attribute value" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        AddRequest           addRequest           = ldapMessage.getAddRequest();

                        TLV                  tlv                  =
                            ldapMessageContainer.getCurrentTLV();

                        // Store the value. It can't be null
                        OctetString value = OctetString.EMPTY_STRING;
                        
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            addRequest.addAttributeValue( value );
                        }
                        else
                        {
                            value = new OctetString( tlv.getValue().getData() );
                            addRequest.addAttributeValue( value );
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Adding value " + value );
                        }
                    }
                } );

    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the instance of this grammar
     *
     * @return An instance on the SearchResultEntry Grammar
     */
    public static IGrammar getInstance()
    {
        return instance;
    }
}