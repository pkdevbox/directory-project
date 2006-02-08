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
package org.apache.directory.shared.ldap.codec.del;


import java.nio.ByteBuffer;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.shared.asn1.ber.Asn1Decoder;
import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.codec.EncoderException;
import org.apache.directory.shared.ldap.codec.Control;
import org.apache.directory.shared.ldap.codec.LdapDecoder;
import org.apache.directory.shared.ldap.codec.LdapMessage;
import org.apache.directory.shared.ldap.codec.LdapMessageContainer;
import org.apache.directory.shared.ldap.codec.del.DelRequest;
import org.apache.directory.shared.ldap.util.StringTools;

import junit.framework.TestCase;


/**
 * Test the DelRequest codec
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DelRequestTest extends TestCase
{
    /**
     * Test the decoding of a full DelRequest
     */
    public void testDecodeDelRequestSuccess() throws NamingException
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x27 );

        stream.put( new byte[]
            {

            0x30, 0x25, // LDAPMessage ::= SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                // CHOICE { ..., delRequest DelRequest, ...
                // DelRequest ::= [APPLICATION 10] LDAPDN;
                0x4A, 0x20, 'c', 'n', '=', 't', 'e', 's', 't', 'M', 'o', 'd', 'i', 'f', 'y', ',', 'o', 'u', '=', 'u',
                's', 'e', 'r', 's', ',', 'o', 'u', '=', 's', 'y', 's', 't', 'e', 'm' } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a DelRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded DelRequest PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        DelRequest delRequest = message.getDelRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( "cn=testModify,ou=users,ou=system", delRequest.getEntry() );

        // Check the length
        assertEquals( 0x27, message.computeLength() );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }


    /**
     * Test the decoding of an empty DelRequest
     */
    public void testDecodeDelRequestEmpty() throws NamingException
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x07 );

        stream.put( new byte[]
            {

            0x30, 0x05, // LDAPMessage ::= SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                // CHOICE { ..., delRequest DelRequest, ...
                // DelRequest ::= [APPLICATION 10] LDAPDN;
                0x4A, 0x00 // Empty DN
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a DelRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
            fail( "We should never reach this point !!!" );
        }
        catch ( DecoderException de )
        {
            assertTrue( true );
        }
    }


    /**
     * Test the decoding of a full DelRequest with controls
     */
    public void testDecodeDelRequestSuccessWithControls() throws NamingException
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer stream = ByteBuffer.allocate( 0x44 );

        stream.put( new byte[]
            {

            0x30, 0x42, // LDAPMessage ::= SEQUENCE {
                0x02, 0x01, 0x01, // messageID MessageID
                // CHOICE { ..., delRequest DelRequest, ...
                // DelRequest ::= [APPLICATION 10] LDAPDN;
                0x4A, 0x20, 'c', 'n', '=', 't', 'e', 's', 't', 'M', 'o', 'd', 'i', 'f', 'y', ',', 'o', 'u', '=', 'u',
                's', 'e', 'r', 's', ',', 'o', 'u', '=', 's', 'y', 's', 't', 'e', 'm', ( byte ) 0xA0, 0x1B, // A
                                                                                                            // control
                0x30, 0x19, 0x04, 0x17, 0x32, 0x2E, 0x31, 0x36, 0x2E, 0x38, 0x34, 0x30, 0x2E, 0x31, 0x2E, 0x31, 0x31,
                0x33, 0x37, 0x33, 0x30, 0x2E, 0x33, 0x2E, 0x34, 0x2E, 0x32 } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a DelRequest PDU
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }

        // Check the decoded DelRequest PDU
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();
        DelRequest delRequest = message.getDelRequest();

        assertEquals( 1, message.getMessageId() );
        assertEquals( "cn=testModify,ou=users,ou=system", delRequest.getEntry() );

        // Check the length
        assertEquals( 0x44, message.computeLength() );

        // Check the Control
        List controls = message.getControls();

        assertEquals( 1, controls.size() );

        Control control = message.getControls( 0 );
        assertEquals( "2.16.840.1.113730.3.4.2", control.getControlType() );
        assertEquals( "", StringTools.dumpBytes( ( byte[] ) control.getControlValue() ) );

        // Check the encoding
        try
        {
            ByteBuffer bb = message.encode( null );

            String encodedPdu = StringTools.dumpBytes( bb.array() );

            assertEquals( encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }

}
