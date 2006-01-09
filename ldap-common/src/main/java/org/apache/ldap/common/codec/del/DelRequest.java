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
package org.apache.ldap.common.codec.del;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import javax.naming.Name;

import org.apache.asn1.codec.EncoderException;
import org.apache.asn1.ber.tlv.Length;
import org.apache.ldap.common.codec.LdapConstants;
import org.apache.ldap.common.codec.LdapMessage;
import org.apache.ldap.common.name.LdapDN;


/**
 * A DelRequest Message. Its syntax is :
 *   DelRequest ::= [APPLICATION 10] LDAPDN
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DelRequest extends LdapMessage
{
    //~ Instance fields ----------------------------------------------------------------------------

    /** The entry to be deleted */
    private Name entry;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new DelRequest object.
     */
    public DelRequest()
    {
        super( );
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the message type
     *
     * @return Returns the type.
     */
    public int getMessageType()
    {
        return LdapConstants.DEL_REQUEST;
    }

    /**
     * Get the entry to be deleted
     *
     * @return Returns the entry.
     */
    public String getEntry()
    {
        return ( ( entry == null ) ? "" : entry.toString() );
    }

    /**
     * Set the entry to be deleted
     *
     * @param entry The entry to set.
     */
    public void setEntry( Name entry )
    {
        this.entry = entry;
    }

    /**
     * Compute the DelRequest length
     * 
     * DelRequest :
     * 
     * 0x4A L1 entry
     * 
     * L1 = Length(entry)
     * 
     * Length(DelRequest) = Length(0x4A) + Length(L1) + L1
     */
    public int computeLength()
    {
        // The entry
        return 1 + Length.getNbBytes( LdapDN.getNbBytes( entry ) ) + LdapDN.getNbBytes( entry );
    }

    /**
     * Encode the DelRequest message to a PDU.
     * 
     * DelRequest :
     * 
     * 0x4A LL entry
     * 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        if (buffer == null)
        {
            throw new EncoderException("Cannot put a PDU in a null buffer !");
        }

        try
        {
            // The DelRequest Tag
            buffer.put( LdapConstants.DEL_REQUEST_TAG );

            // The entry
            buffer.put( Length.getBytes( LdapDN.getNbBytes( entry ) ) );
            buffer.put( LdapDN.getBytes( entry ) );
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException("The PDU buffer size is too small !");
        }

        return buffer;
    }

    /**
     * Return a String representing a DelRequest
     * 
     * @return A DelRequest String
     */
    public String toString()
    {

        StringBuffer sb = new StringBuffer();

        sb.append( "    Del request\n" );
        sb.append( "        Entry : '" ).append( entry.toString() ).append( "'\n" );

        return sb.toString();
    }
}
