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
package org.apache.asn1new.ldap.codec;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1new.ber.Asn1Decoder;
import org.apache.asn1new.ber.containers.IAsn1Container;
import org.apache.asn1new.ber.tlv.TLVStateEnum;
import org.apache.asn1new.ldap.pojo.BindRequest;
import org.apache.asn1new.ldap.pojo.LdapMessage;
import org.apache.asn1new.ldap.pojo.SimpleAuthentication;

import java.nio.ByteBuffer;


/**
 * A global Ldap Decoder test
 * 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapDecoderTest extends TestCase
{
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Test the decoding of a full PDU
     */
    public void testDecodeFull()
    {

        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x35 );
        stream.put(
            new byte[]
            {
                0x30, 0x33, 		// LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, 	//         messageID MessageID
                0x60, 0x2E, 		//        CHOICE { ..., bindRequest BindRequest, ...
                        			// BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, 	//        version INTEGER (1..127),
                0x04, 0x1F, 		//        name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
                ( byte ) 0x80, 0x08, //        authentication AuthenticationChoice
                                     // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING, ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd'
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }

        // Check the decoded PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br      = message.getBindRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( 3, br.getVersion() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", br.getName() );
        Assert.assertEquals( true, ( br.getAuthentication() instanceof SimpleAuthentication ) );
        Assert.assertEquals( "password",
            ( ( SimpleAuthentication ) br.getAuthentication() ).getSimple().toString() );
    }

    /**
     * Test the decoding of a partial PDU
     */
    public void testDecodePartial()
    {

        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 16 );
        stream.put(
            new byte[]
            {
                0x30, 0x33, 		// LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, 	//         messageID MessageID
                0x60, 0x2E, 		//        CHOICE { ..., bindRequest BindRequest, ...
                        			// BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, 	//        version INTEGER (1..127),
                0x04, 0x1F, 		//        name LDAPDN,
                'u', 'i', 'd', '='
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }

        Assert.assertEquals( TLVStateEnum.VALUE_STATE_PENDING, ldapMessageContainer.getState() );
        
        // Check the decoded PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br      = message.getBindRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( 3, br.getVersion() );
        Assert.assertEquals( null, br.getName() );
        Assert.assertEquals( false, ( br.getAuthentication() instanceof SimpleAuthentication ) );
    }

    /**
     * Test the decoding of a splitted PDU
     */
    public void testDecodeSplittedPDU()
    {

        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 16 );
        stream.put(
            new byte[]
            {
                0x30, 0x33, 		// LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, 	//         messageID MessageID
                0x60, 0x2E, 		//        CHOICE { ..., bindRequest BindRequest, ...
                        			// BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, 	//        version INTEGER (1..127),
                0x04, 0x1F, 		//        name LDAPDN,
                'u', 'i', 'd', '='
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU first block of data
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }

        Assert.assertEquals( TLVStateEnum.VALUE_STATE_PENDING, ldapMessageContainer.getState() );

        // Second block of data
        stream = ByteBuffer.allocate( 37 );
        stream.put(
            new byte[]
            {
                'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=', 'e', 'x', 'a',
                'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm', ( byte ) 0x80, 0x08, //        authentication AuthenticationChoice
                                                                                            // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING, ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd'
            } );

        stream.flip();

        // Decode a BindRequest PDU second block of data
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            Assert.fail( de.getMessage() );
        }

        Assert.assertEquals( ldapMessageContainer.getState(), TLVStateEnum.PDU_DECODED );
        
        // Check the decoded PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        BindRequest br      = message.getBindRequest();

        Assert.assertEquals( 1, message.getMessageId() );
        Assert.assertEquals( 3, br.getVersion() );
        Assert.assertEquals( "uid=akarasulu,dc=example,dc=com", br.getName() );
        Assert.assertEquals( true, ( br.getAuthentication() instanceof SimpleAuthentication ) );
        Assert.assertEquals( "password",
            ( ( SimpleAuthentication ) br.getAuthentication() ).getSimple().toString() );
    }

    /**
     * Test the decoding of a PDU with a bad Length.
     * The first TLV has a length of 0x32 when the PDU is 0x33 bytes long.
     */
    public void testDecodeBadLengthTooSmall()
    {

        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x35 );
        stream.put(
            new byte[]
            {
                    				// Length should be 0x33...
                0x30, 0x32, 		// LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01, 	//         messageID MessageID
                0x60, 0x2E, 		//        CHOICE { ..., bindRequest BindRequest, ...
                        			// BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, 	//        version INTEGER (1..127),
                0x04, 0x1F, 		//        name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
                ( byte ) 0x80, 0x08, //        authentication AuthenticationChoice
                                     // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING, ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd'
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            Assert.assertEquals( "The current Value length is above the expected length",
                de.getMessage() );
            return;
        }

        Assert.fail( "Should never reach this point.." );
    }

    /**
     * Test the decoding of a PDU with a bad primitive Length.
     * The second TLV has a length of 0x02 when the PDU is 0x01 bytes long.
     */
    public void testDecodeBadPrimitiveLengthTooBig()
    {

        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x35 );
        stream.put(
            new byte[]
            {
                0x30, 0x33, 		// LDAPMessage ::=SEQUENCE {
                					// Length should be 0x01...
                0x02, 0x02, 0x01, 	//         messageID MessageID
                0x60, 0x2E, 		//        CHOICE { ..., bindRequest BindRequest, ...
                        			// BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, 	//        version INTEGER (1..127),
                0x04, 0x1F, 		//        name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
                ( byte ) 0x80, 0x08, //        authentication AuthenticationChoice
                                     // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING, ...
                'p', 'a', 's', 's', 'w', 'o', 'r'
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            Assert.assertEquals( "Universal tag 14 is reserved", de.getMessage() );
            return;
        }

        Assert.fail( "Should never reach this point." );
    }

    /**
     * Test the decoding of a PDU with a bad primitive Length.
     * The second TLV has a length of 0x02 when the PDU is 0x01 bytes long.
     */
    public void testDecodeBadTagTransition()
    {

        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x35 );
        stream.put(
            new byte[]
            {
                0x30, 0x33, 		// LDAPMessage ::=SEQUENCE {
                					// Length should be 0x01...
                0x02, 0x01, 0x01, 	//         messageID MessageID
                0x2D, 0x2D, 		//        CHOICE { ..., bindRequest BindRequest, ...
                        			// BindRequest ::= APPLICATION[0] SEQUENCE {
                0x02, 0x01, 0x03, 	//        version INTEGER (1..127),
                0x04, 0x1F, 		//        name LDAPDN,
                'u', 'i', 'd', '=', 'a', 'k', 'a', 'r', 'a', 's', 'u', 'l', 'u', ',', 'd', 'c', '=',
                'e', 'x', 'a', 'm', 'p', 'l', 'e', ',', 'd', 'c', '=', 'c', 'o', 'm',
                ( byte ) 0x80, 0x08, //        authentication AuthenticationChoice
                                     // AuthenticationChoice ::= CHOICE { simple [0] OCTET STRING, ...
                'p', 'a', 's', 's', 'w', 'o', 'r', 'd'
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a BindRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            Assert.assertEquals( "Bad transition !",
                de.getMessage() );
            return;
        }

        Assert.fail( "Should never reach this point." );
    }
}