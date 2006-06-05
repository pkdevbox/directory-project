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
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.apache.asn1new.ldap.pojo.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the SearchResultEntry LDAP message. All the actions are declared in this
 * class. As it is a singleton, these declaration are only done once.
 * 
 * We have to deal with empty elements, as stated by rfc 2251 :
 * 
 * -- implementors should note that the PartialAttributeList may
 * -- have zero elements (if none of the attributes of that entry
 * -- were requested, or could be returned), and that the vals set
 * -- may also have zero elements (if types only was requested, or
 * -- all values were excluded from the result.)
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEntryGrammar extends AbstractGrammar implements IGrammar
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( SearchResultEntryGrammar.class );

    /** The instance of grammar. SearchResultEntryGrammar is a singleton */
    private static IGrammar instance = new SearchResultEntryGrammar();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SearchResultEntryGrammar object.
     */
    private SearchResultEntryGrammar()
    {
        name       = SearchResultEntryGrammar.class.getName();
        statesEnum = LdapStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[LdapStatesEnum.LAST_SEARCH_RESULT_ENTRY_STATE][256];

        //============================================================================================
        // SearchResultEntry Message
        //============================================================================================
        // LdapMessage ::= ... SearchResultEntry ...
        // SearchResultEntry ::= [APPLICATION 4] SEQUENCE { (Tag)
        // Nothing to do.
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_TAG][LdapConstants.SEARCH_RESULT_ENTRY_TAG] = new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_TAG, LdapStatesEnum.SEARCH_RESULT_ENTRY_VALUE,
                null );

        // LdapMessage ::= ... SearchResultEntry ...
        // SearchResultEntry ::= [APPLICATION 4] SEQUENCE { (Value)
        // Nothing to do.
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_VALUE][LdapConstants.SEARCH_RESULT_ENTRY_TAG] = new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_VALUE,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_OBJECT_NAME_TAG,
                new GrammarAction( "Init SearchResultEntry" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();

                        // Now, we can allocate the SearchResultEntry Object
                        // And we associate it to the ldapMessage Object
                        ldapMessage.setProtocolOP( new SearchResultEntry() );
                    }
                } );

        // LdapMessage ::= ... SearchResultEntry ...
        // SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
        //    objectName      LDAPDN, (Tag)
        //    ...
        // Nothing to do
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_OBJECT_NAME_TAG][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_OBJECT_NAME_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_OBJECT_NAME_VALUE, null );

        // LdapMessage ::= ... SearchResultEntry ...
        // SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
        //    objectName      LDAPDN, (Value)
        //    ...
        // Store the object name.
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_OBJECT_NAME_VALUE][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_OBJECT_NAME_VALUE,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTES_TAG,
                new GrammarAction( "Store search result entry object name Value" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        SearchResultEntry    searchResultEntry    =
                            ldapMessage.getSearchResultEntry();

                        TLV                  tlv                  =
                            ldapMessageContainer.getCurrentTLV();

                        // Store the value.
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            searchResultEntry.setObjectName( LdapDN.EMPTY_LDAPDN );
                        }
                        else
                        {
                            try
                            {
                                searchResultEntry.setObjectName( new LdapDN( tlv.getValue().getData() ) );
                            }
                            catch ( InvalidNameException ine )
                            {
                                log.error(" The DN " + StringUtils.dumpBytes( tlv.getValue().getData() ) + "is invalid : " + ine.getMessage() );
                                throw new DecoderException( "The Dn is invalid : " + ine.getMessage() );
                            }
                        }

                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Search Result Entry DN found : " + searchResultEntry.getObjectName() );
                        }
                    }
                } );

        // SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
        //    attributes      PartialAttributeList }
        // PartialAttributeList ::= *SEQUENCE* OF SEQUENCE { (Tag)
        //    ...
        // Nothing to do
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTES_TAG][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTES_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTES_VALUE, null );

        // SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
        //    attributes      PartialAttributeList }
        // PartialAttributeList ::= *SEQUENCE* OF SEQUENCE { (Tag)
        //    ...
        // We may have many attributes. Nothing to do
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTES_VALUE][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTES_VALUE,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_PARTIAL_ATTRIBUTE_LIST_TAG, null );

        // PartialAttributeList ::= SEQUENCE OF *SEQUENCE* { (Tag)
        //    ...
        // Nothing to do
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_PARTIAL_ATTRIBUTE_LIST_TAG][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_PARTIAL_ATTRIBUTE_LIST_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_PARTIAL_ATTRIBUTE_LIST_VALUE, null );

        // SearchResultEntry ::= [APPLICATION 4] SEQUENCE {
        //    attributes      PartialAttributeList }
        // PartialAttributeList ::= SEQUENCE OF *SEQUENCE* { (Tag)
        //    ...
        // Loop.
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_OR_LIST_TAG][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_OR_LIST_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_PARTIAL_ATTRIBUTE_LIST_VALUE, null );

        // PartialAttributeList ::= SEQUENCE OF *SEQUENCE* { (Value)
        //    ...
        // We may have many attributes. We also have to store a previously decoded AttributeValue
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_PARTIAL_ATTRIBUTE_LIST_VALUE][UniversalTag.SEQUENCE_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_PARTIAL_ATTRIBUTE_LIST_VALUE,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_TYPE_TAG,
                new GrammarAction( "Store attributeValue" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        SearchResultEntry    searchResultEntry    =
                            ldapMessage.getSearchResultEntry();

                        searchResultEntry.addPartialAttributeList();
                    }
                } );

        // PartialAttributeList ::= SEQUENCE OF SEQUENCE {
        //    type    AttributeDescription, (Tag)
        //    ...
        // Nothing to do.
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_TYPE_TAG][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_TYPE_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_TYPE_VALUE, null );

        // PartialAttributeList ::= SEQUENCE OF SEQUENCE {
        //    type    AttributeDescription, (Value)
        //    ...
        // Store the attribute's name.
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_TYPE_VALUE][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_TYPE_VALUE,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_VALS_TAG,
                new GrammarAction( "Store search result entry object name Value" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        SearchResultEntry    searchResultEntry    =
                            ldapMessage.getSearchResultEntry();

                        TLV                  tlv                  =
                            ldapMessageContainer.getCurrentTLV();

                        LdapString type = LdapString.EMPTY_STRING;

                        // Store the name
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            searchResultEntry.addAttributeValues( type );
                        }
                        else
                        {
                            try
                            {
                                type = new LdapString( tlv.getValue().getData() );
                                searchResultEntry.addAttributeValues( type );
                            }
                            catch ( LdapStringEncodingException lsee )
                            {
                                log.error( "Invalid attribute type : " + StringUtils.dumpBytes( tlv.getValue().getData() ) );
                                throw new DecoderException( "The attribute type is invalid : " + lsee.getMessage() );
                            }
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Attribute type : " + type );
                        }
                    }
                } );

        // PartialAttributeList ::= SEQUENCE OF SEQUENCE {
        //    ...
        //    vals    *SET OF* AttributeValue} (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_VALS_TAG][UniversalTag.SET_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_VALS_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_VALS_VALUE, null );

        // PartialAttributeList ::= SEQUENCE OF SEQUENCE {
        //    ...
        //    vals    *SET OF* AttributeValue} (Value)
        // We may have many values. Nothing to do
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_VALS_VALUE][UniversalTag.SET_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_VALS_VALUE,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_TAG, null );

        // PartialAttributeList ::= SEQUENCE OF SEQUENCE {
        //    ...
        //    vals    SET OF *AttributeValue*} (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_TAG][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_VALUE, null );

        // PartialAttributeList ::= SEQUENCE OF SEQUENCE {
        //    ...
        //    vals    SET OF *AttributeValue*} (Tag)
        // The loop.
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_OR_LIST_TAG][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_OR_LIST_TAG,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_VALUE, null );

        // PartialAttributeList ::= SEQUENCE OF SEQUENCE {
        //    ...
        //    vals    SET OF *AttributeValue*} (Value)
        // We may have many values. Store the current one in the current Attribute Value
        super.transitions[LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_VALUE][UniversalTag.OCTET_STRING_TAG] =
            new GrammarTransition(
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_VALUE,
                LdapStatesEnum.SEARCH_RESULT_ENTRY_ATTRIBUTE_VALUE_OR_LIST_TAG,
                new GrammarAction( "Store Attribute Value value" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        SearchResultEntry    searchResultEntry    =
                            ldapMessage.getSearchResultEntry();

                        TLV                  tlv                  =
                            ldapMessageContainer.getCurrentTLV();

                        // Store the value
                        OctetString value = OctetString.EMPTY_STRING;
                        
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            searchResultEntry.addAttributeValue( value );
                        }
                        else
                        {
                            value = new OctetString( tlv.getValue().getData() );
                            searchResultEntry.addAttributeValue( value );
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Attribute value : " + value );
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