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
package org.apache.ldap.common.codec.unbind;

import java.nio.ByteBuffer;

import org.apache.asn1.codec.DecoderException;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1.ber.Asn1Decoder;
import org.apache.asn1.ber.IAsn1Container;
import org.apache.ldap.common.codec.LdapDecoder;
import org.apache.ldap.common.codec.LdapMessage;
import org.apache.ldap.common.codec.LdapMessageContainer;
import org.apache.ldap.common.util.StringTools;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UnBindRequestTest extends TestCase {
    /**
     * Test the decoding of a UnBindRequest with no controls
     */
    public void testDecodeUnBindRequestNoControls()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x07 );
        stream.put(
            new byte[]
            {
                0x30, 0x05, 		// LDAPMessage ::=SEQUENCE {
				0x02, 0x01, 0x01, 	//         messageID MessageID
				0x42, 0x00, 		//        CHOICE { ..., unbindRequest UnbindRequest,...
									// UnbindRequest ::= [APPLICATION 2] NULL
            } );

        String decodedPdu = StringTools.dumpBytes( stream.array() );
        stream.flip();

        // Allocate a BindRequest Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            de.printStackTrace();
            fail( de.getMessage() );
        }
    	
        LdapMessage message = ( ( LdapMessageContainer ) ldapMessageContainer ).getLdapMessage();

        assertEquals( 1, message.getMessageId() );
        
        // Check the length
        assertEquals(7, message.computeLength());

        try
        {
            ByteBuffer bb = message.encode( null );
            
            String encodedPdu = StringTools.dumpBytes( bb.array() ); 
            
            assertEquals(encodedPdu, decodedPdu );
        }
        catch ( EncoderException ee )
        {
            ee.printStackTrace();
            fail( ee.getMessage() );
        }
    }

    /**
     * Test the decoding of a UnBindRequest with a not null body
     */
    public void testDecodeUnBindRequestNotNull()
    {
        Asn1Decoder ldapDecoder = new LdapDecoder();

        ByteBuffer  stream      = ByteBuffer.allocate( 0x09 );
        stream.put(
            new byte[]
            {
                0x30, 0x07,         // LDAPMessage ::=SEQUENCE {
                0x02, 0x01, 0x01,   //         messageID MessageID
                0x42, 0x02,         //        CHOICE { ..., unbindRequest UnbindRequest,...
                  0x04, 0x00        // UnbindRequest ::= [APPLICATION 2] NULL
                                    
            } );

        stream.flip();

        // Allocate a LdapMessage Container
        IAsn1Container ldapMessageContainer = new LdapMessageContainer();

        // Decode a UnbindRequest message
        try
        {
            ldapDecoder.decode( stream, ldapMessageContainer );
        }
        catch ( DecoderException de )
        {
            System.out.println( de.getMessage() );
            assertTrue( true );
            return;
        }
        
        fail( "We should not reach this point" );
    }
}
