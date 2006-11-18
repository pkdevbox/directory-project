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
package org.apache.ldap.common.berlib.snacc ;


import org.apache.ldap.common.message.Message ;
import org.apache.ldap.common.message.spi.Provider ;
import org.apache.ldap.common.message.MessageTypeEnum ;
import org.apache.ldap.common.message.spi.TransformerSpi ;
import org.apache.ldap.common.message.spi.ProviderException ;

import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessage ;
import org.apache.ldap.common.berlib.snacc.ldap_v3.LDAPMessageChoice ;


/**
 * Snacc4J Provider's TransformerSpi implementation
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class SnaccTransformer
    implements TransformerSpi
{
    /** Provider owning this Transformer SPI implementation */
	private final Provider m_provider ;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    /**
     * Creates an instance of a Snacc4J TransformerSpi implementation.
     *
     * @param a_provider the owning provider.
     */
	SnaccTransformer( final Provider a_provider )
    {
        m_provider = a_provider ;
    }


    // ------------------------------------------------------------------------
    // ProviderObject Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Gets the Provider that this TransformerSpi implementation is part of.
     *
     * @return the owning provider.
     */
    public Provider getProvider()
    {
        return m_provider ;
    }


    // ------------------------------------------------------------------------
    // TransformerSpi Interface Method Implementations
    // ------------------------------------------------------------------------


    /**
     * Transforms the agnostic Message representation of an LDAPv3 ASN.1 message
     * envelope into a compiler generated and BER lib specific stub containment
     * tree.  Transformer method used before marshaling a PDU onto the wire -
     * the resultant object generated by this transform overload is the object
     * marshaled.
     *
     * @param a_msg The Message object used to generate the compiler stub based
     * containment tree.
     * @return the compiler stub based containment tree representing the Message
     * transformed into the provider's format.  For example for the Snacc
     * provider this object would be an instance of the LDAPMessage stub class.
     * @throws ProviderException to indicate an error while attempting to
     * transform library/compiler specific message envelope into agnostic
     * message.  Provider specific exceptions encountered while transforming
     * can be held within this subclass of MultiException.
     */
    public Object transform(Message a_msg)
        throws ProviderException
    {
        LDAPMessage l_snaccMsg = null ;

        switch( a_msg.getType().getValue() )
        {
        case( MessageTypeEnum.ABANDONREQUEST_VAL ):
            l_snaccMsg = AbandonRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.AbandonRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.ADDREQUEST_VAL ):
            l_snaccMsg = AddRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.AddRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.ADDRESPONSE_VAL ):
            l_snaccMsg = AddResponseTransform.transformToSnacc(
                ( org.apache.ldap.common.message.AddResponse ) a_msg ) ;
            break ;
        case( MessageTypeEnum.BINDREQUEST_VAL ):
            l_snaccMsg = BindRequestTransform.transform(
            	( org.apache.ldap.common.message.BindRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.BINDRESPONSE_VAL ):
            l_snaccMsg = BindResponseTransform.transform(
            	( org.apache.ldap.common.message.BindResponse ) a_msg ) ;
            break ;
        case( MessageTypeEnum.COMPAREREQUEST_VAL ):
            l_snaccMsg = CompareRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.CompareRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.COMPARERESPONSE_VAL ):
            l_snaccMsg = CompareResponseTransform.transformToSnacc(
                ( org.apache.ldap.common.message.CompareResponse ) a_msg ) ;
            break ;
        case( MessageTypeEnum.DELREQUEST_VAL ):
            l_snaccMsg = DelRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.DeleteRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.DELRESPONSE_VAL ):
            l_snaccMsg = DelResponseTransform.transformToSnacc(
                ( org.apache.ldap.common.message.DeleteResponse ) a_msg ) ;
            break ;
        case( MessageTypeEnum.EXTENDEDREQ_VAL ):
            l_snaccMsg = ExtendedRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.ExtendedRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.EXTENDEDRESP_VAL ):
            l_snaccMsg = ExtendedResponseTransform.transformToSnacc(
                ( org.apache.ldap.common.message.ExtendedResponse ) a_msg ) ;
            break ;
        case( MessageTypeEnum.MODDNREQUEST_VAL ):
            l_snaccMsg = ModifyDnRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.ModifyDnRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.MODDNRESPONSE_VAL ):
            l_snaccMsg = ModifyDnResponseTransform.transformToSnacc(
                ( org.apache.ldap.common.message.ModifyDnResponse ) a_msg ) ;
            break ;
        case( MessageTypeEnum.MODIFYREQUEST_VAL ):
            l_snaccMsg = ModifyRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.ModifyRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.MODIFYRESPONSE_VAL ):
            l_snaccMsg = ModifyResponseTransform.transformToSnacc(
                ( org.apache.ldap.common.message.ModifyResponse ) a_msg ) ;
            break ;
        case( MessageTypeEnum.SEARCHREQUEST_VAL ):
            l_snaccMsg = SearchRequestTransform.transformToSnacc(
                ( org.apache.ldap.common.message.SearchRequest ) a_msg ) ;
            break ;
        case( MessageTypeEnum.SEARCHRESDONE_VAL ):
            l_snaccMsg = SearchResponseDoneTransform.transformToSnacc(
                ( org.apache.ldap.common.message.SearchResponseDone ) a_msg ) ;
            break ;
        case( MessageTypeEnum.SEARCHRESENTRY_VAL ):
            l_snaccMsg = SearchResponseEntryTransform.transformToSnacc(
                ( org.apache.ldap.common.message.SearchResponseEntry ) a_msg ) ;
            break ;
        case( MessageTypeEnum.SEARCHRESREF_VAL ):
            l_snaccMsg = SearchResponseReferenceTransform.transformToSnacc(
                ( org.apache.ldap.common.message.SearchResponseReference ) a_msg ) ;
            break ;
        case( MessageTypeEnum.UNBINDREQUEST_VAL ):
            l_snaccMsg = UnbindRequestTransform.transformToSnacc(
             ( org.apache.ldap.common.message.UnbindRequest) a_msg ) ;
            break ;
        default:
            throw new ProviderException( m_provider,
            	"Unknown message type: " + a_msg.getType().getName() ) ;
        }

        return l_snaccMsg ;
    }


    /**
     * Transforms the containment tree rooted at some compiler stub object into
     * a Message implementing object instance.  Transformer method used after
     * demarshaling a PDU off the wire - which consequently generated the
     * argument.
     *
     * @param a_obj the compiler specific root object holding the containment
     * tree for the LDAPv3 message envelope.  For example for the Snacc
     * provider this object would be an instance of the LDAPMessage stub class.
     * @return the compiler agnostic Message implemenating object representing
     * the containment tree held by the message envelope argument.
     * @throws ProviderException to indicate an error while attempting to
     * transform library/compiler specific message envelope into agnostic
     * message.  Provider specific exceptions encountered while transforming
     * can be held within this subclass of MultiException.
     */
    public Message transform(Object a_obj)
        throws ProviderException
    {
        Message l_message = null ;
        LDAPMessage l_snaccMsg = null ;
        LDAPMessageChoice l_op = null ;

        try
        {
            l_snaccMsg = (LDAPMessage) a_obj ;
        }
        catch( ClassCastException e )
        {
            ProviderException pe = new ProviderException( m_provider,
                "Object to be transformed was not recognized as a Snacc4J "
                + "generated stub base LDAPMessage envelope!") ;
            pe.addThrowable( e ) ;
            throw pe ;
        }

        l_op = l_snaccMsg.protocolOp ;
        switch( l_op.choiceId )
        {
        case( LDAPMessageChoice.ABANDONREQUEST_CID ):
            l_message =
                AbandonRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.ADDREQUEST_CID ):
            l_message = AddRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.ADDRESPONSE_CID ):
            l_message = AddResponseTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.BINDREQUEST_CID ):
            l_message = BindRequestTransform.transform( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.BINDRESPONSE_CID ):
            l_message = BindResponseTransform.transform( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.COMPAREREQUEST_CID ):
            l_message =
                CompareRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.COMPARERESPONSE_CID ):
            l_message =
                CompareResponseTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.DELREQUEST_CID ):
            l_message = DelRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.DELRESPONSE_CID ):
            l_message = DelResponseTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.EXTENDEDREQ_CID ):
            l_message =
                ExtendedRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.EXTENDEDRESP_CID ):
            l_message =
                ExtendedResponseTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.MODDNREQUEST_CID ):
            l_message =
                ModifyDnRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.MODDNRESPONSE_CID ):
            l_message =
                ModifyDnResponseTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.MODIFYREQUEST_CID ):
            l_message =
                ModifyRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.MODIFYRESPONSE_CID ):
            l_message =
                ModifyResponseTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.SEARCHREQUEST_CID ):
            l_message =
                SearchRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.SEARCHRESDONE_CID ):
            l_message =
                SearchResponseDoneTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.SEARCHRESENTRY_CID ):
            l_message =
                SearchResponseEntryTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.SEARCHRESREF_CID ):
            l_message = SearchResponseReferenceTransform.transformFromSnacc(
                l_snaccMsg ) ;
            break ;
        case( LDAPMessageChoice.UNBINDREQUEST_CID ):
            l_message =
                UnbindRequestTransform.transformFromSnacc( l_snaccMsg ) ;
            break ;
		default:
            throw new ProviderException( m_provider,
            	"Unrecognized message choice id value: " + l_op.choiceId ) ;
        }

        return l_message ;
    }
}