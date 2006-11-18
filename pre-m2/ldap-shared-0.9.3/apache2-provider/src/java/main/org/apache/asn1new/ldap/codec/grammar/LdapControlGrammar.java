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
import org.apache.asn1new.ber.tlv.TLV;
import org.apache.asn1new.ber.tlv.UniversalTag;
import org.apache.asn1new.ber.tlv.Value;
import org.apache.asn1new.util.BooleanDecoder;
import org.apache.asn1new.util.BooleanDecoderException;
import org.apache.asn1new.util.StringUtils;
import org.apache.asn1new.primitives.OID;
import org.apache.asn1new.primitives.OctetString;
import org.apache.asn1new.ldap.codec.LdapConstants;
import org.apache.asn1new.ldap.codec.LdapMessageContainer;
import org.apache.asn1new.ldap.pojo.Control;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the Control LDAP message. All the actions are declared in this
 * class. As it is a singleton, these declaration are only done once.
 * 
 * If an action is to be added or modified, this is where the work is to be done !
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapControlGrammar extends AbstractGrammar implements IGrammar
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** The logger */
    private static final Logger log = LoggerFactory.getLogger( LdapControlGrammar.class );

    /** The instance of grammar. LdapControlGrammar is a singleton */
    private static IGrammar instance = new LdapControlGrammar();

    /**
     * Get the instance of this grammar
     *
     * @return An instance on the LdapControl Grammar
     */
    public static IGrammar getInstance()
    {
        return instance;
    }
    
    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new LdapControlGrammar object.
     */
    private LdapControlGrammar()
    {
        name = LdapControlGrammar.class.getName();
        statesEnum = LdapStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[LdapStatesEnum.LAST_CONTROL_STATE][256];

        //============================================================================================
        // Controls
        //============================================================================================
        //    ...
        //    controls       [0] Controls OPTIONAL } (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.CONTROLS_TAG][LdapConstants.CONTROLS_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROLS_TAG,
                LdapStatesEnum.CONTROLS_VALUE, null );

        //    ...
        //    controls       [0] Controls OPTIONAL } (Value)
        // Initialize the controls pojo
        super.transitions[LdapStatesEnum.CONTROLS_VALUE][LdapConstants.CONTROLS_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROLS_VALUE, LdapStatesEnum.CONTROL_TAG,
                new GrammarAction( "Init Controls" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {

                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();

                        // We can initialize the controls array
                        ldapMessage.initControl();
                    }
                } );

        //============================================================================================
        // Control 
        //============================================================================================
        // Control ::= SEQUENCE { (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.CONTROL_TAG][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_TAG,
                LdapStatesEnum.CONTROL_VALUE, null );

        // Another control when critical and value are empty 
        super.transitions[LdapStatesEnum.CONTROL_LOOP_OR_CRITICAL_OR_VALUE_TAG][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_LOOP_OR_END_TAG,
                LdapStatesEnum.CONTROL_VALUE, null );
        
        // Another control when value is empty 
        super.transitions[LdapStatesEnum.CONTROL_LOOP_OR_VALUE_TAG][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_LOOP_OR_END_TAG,
                LdapStatesEnum.CONTROL_VALUE, null );
        
        // Another control after a value
        super.transitions[LdapStatesEnum.CONTROL_LOOP_OR_END_TAG][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_LOOP_OR_END_TAG,
                LdapStatesEnum.CONTROL_VALUE, null );
        
        // Control ::= SEQUENCE { (Value)
        // Create a new Control object, and store it in the controls array.
        super.transitions[LdapStatesEnum.CONTROL_VALUE][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_VALUE, LdapStatesEnum.CONTROL_TYPE_TAG,
                new GrammarAction( "Add Control" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {
                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        
                        // Create a new control
                        Control control = new Control();
                        
                        // Add it to the ldap message
                        ldapMessage.addControl( control );
                    }
                });

        //============================================================================================
        // ControlType 
        //============================================================================================
        // Control ::= SEQUENCE { 
        //    controlType  LDAPOID, (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.CONTROL_TYPE_TAG][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_TYPE_TAG,
                LdapStatesEnum.CONTROL_TYPE_VALUE, null );

        // Control ::= SEQUENCE { (Value)
        // Store the value in the control object created before
        super.transitions[LdapStatesEnum.CONTROL_TYPE_VALUE][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_TYPE_VALUE, LdapStatesEnum.CONTROL_LOOP_OR_CRITICAL_OR_VALUE_TAG, 
                new GrammarAction( "Set Control type" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {
                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        
                        TLV tlv = ldapMessageContainer.getCurrentTLV();
                        
                        // Get the current control
                        Control control = ldapMessage.getCurrentControl();
                        
                        // Store the type
                        // We have to handle the special case of a 0 length OID
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            log.error( "The name must not be null" );
                            throw new DecoderException( "The name must not be null" );
                        }
                        else
                        {
                            control.setControlType( new OID( tlv.getValue().getData() ) );
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Control OID : " + control.getControlType() );
                        }
                    }
                });

        //============================================================================================
        // Control Criticality
        //============================================================================================
        // Control ::= SEQUENCE {
        //    ...
        //    criticality             BOOLEAN DEFAULT FALSE, (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.CONTROL_LOOP_OR_CRITICAL_OR_VALUE_TAG][UniversalTag.BOOLEAN_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_LOOP_OR_CRITICAL_OR_VALUE_TAG,
                LdapStatesEnum.CONTROL_CRITICALITY_VALUE, null );

        // Control ::= SEQUENCE { (Value)
        //    ...
        //    criticality             BOOLEAN DEFAULT FALSE, (Value)
        // Store the criticality in the control object created before
        super.transitions[LdapStatesEnum.CONTROL_CRITICALITY_VALUE][UniversalTag.BOOLEAN_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_CRITICALITY_VALUE, LdapStatesEnum.CONTROL_LOOP_OR_VALUE_TAG, 
                new GrammarAction( "Set Control criticality" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {
                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        
                        TLV tlv = ldapMessageContainer.getCurrentTLV();
                        
                        // Get the current control
                        Control control = ldapMessage.getCurrentControl();
                        
                        // Store the criticality
                        // We get the value. If it's a 0, it's a FALSE. If it's
                        // a FF, it's a TRUE. Any other value should be an error,
                        // but we could relax this constraint. So if we have something
                        // which is not 0, it will be interpreted as TRUE, but we
                        // will generate a warning.
                        Value value        = tlv.getValue();

                        try
                        {
                            control.setCriticality( BooleanDecoder.parse( value) );
                        }
                        catch ( BooleanDecoderException bde )
                        {
                            log.error("The control criticality flag " + StringUtils.dumpBytes( value.getData() ) + 
                                    " is invalid : " + bde.getMessage() + ". It should be 0 or 255" );
                        
                            throw new DecoderException( bde.getMessage() );
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Control criticality : " + control.getCriticality() );
                        }
                    }
                });

        //============================================================================================
        // Control Value
        //============================================================================================
        // Control ::= SEQUENCE {
        //    ...
        //    controlValue            OCTET STRING OPTIONAL } (Tag)
        // Nothing to do
        super.transitions[LdapStatesEnum.CONTROL_LOOP_OR_CRITICAL_OR_VALUE_TAG][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_LOOP_OR_CRITICAL_OR_VALUE_TAG,
                LdapStatesEnum.CONTROL_VALUE_VALUE, null );

        super.transitions[LdapStatesEnum.CONTROL_LOOP_OR_VALUE_TAG][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_LOOP_OR_VALUE_TAG,
                LdapStatesEnum.CONTROL_VALUE_VALUE, null );

        // Control ::= SEQUENCE { (Value)
        //    ...
        //    controlValue            OCTET STRING OPTIONAL } (Value)
        // Store the value in the control object created before
        super.transitions[LdapStatesEnum.CONTROL_VALUE_VALUE][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
                LdapStatesEnum.CONTROL_VALUE_VALUE, LdapStatesEnum.CONTROL_LOOP_OR_END_TAG, 
                new GrammarAction( "Set Control value" )
                {
                    public void action( IAsn1Container container ) throws DecoderException
                    {
                        LdapMessageContainer ldapMessageContainer = ( LdapMessageContainer )
                            container;
                        LdapMessage          ldapMessage          =
                            ldapMessageContainer.getLdapMessage();
                        
                        TLV tlv = ldapMessageContainer.getCurrentTLV();
                        
                        // Get the current control
                        Control control = ldapMessage.getCurrentControl();
                        
                        Value value        = tlv.getValue();

                        // Store the value
                        // We have to handle the special case of a 0 length value
                        if ( tlv.getLength().getLength() == 0 )
                        {
                            control.setControlValue( OctetString.EMPTY_STRING );
                        }
                        else
                        {
                            control.setControlValue( new OctetString( value.getData() ) );
                        }
                        
                        if ( log.isDebugEnabled() )
                        {
                            log.debug( "Control value : " + StringUtils.dumpBytes( control.getControlValue() ) );
                        }
                    }
                });
    }
}