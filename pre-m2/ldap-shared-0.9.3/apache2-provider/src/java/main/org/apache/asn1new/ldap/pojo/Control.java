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
package org.apache.asn1new.ldap.pojo;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.asn1new.Asn1Object;
import org.apache.asn1.codec.EncoderException;
import org.apache.asn1new.ber.tlv.Length;
import org.apache.asn1new.ber.tlv.UniversalTag;
import org.apache.asn1new.ber.tlv.Value;
import org.apache.asn1new.primitives.OID;
import org.apache.asn1new.primitives.OctetString;


/**
 * A Asn1Object to store a Control.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Control extends Asn1Object
{
    //~ Instance fields ----------------------------------------------------------------------------

    /** The control type */
    private OID controlType;

    /** The criticality (default value is false) */
    private boolean criticality = false;

    /** Optionnal control value */
    private OctetString controlValue;
    
    /** The control length */
    private transient int controlLength;
    
    private static final byte[] EMPTY_BYTES = new byte[]{};

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the control type
     *
     * @return A string which represent the control type
     */
    public String getControlType()
    {
        return (controlType == null ? "" : controlType.toString() );
    }

    /**
     * Set the control type
     *
     * @param controlType An OID to store
     */
    public void setControlType( OID controlType )
    {
        this.controlType = controlType;
    }

    /**
     * Get the control value
     *
     * @return The control value
     */
    public byte[] getControlValue()
    {
        return controlValue == null ? EMPTY_BYTES : controlValue.getValue();
    }

    /**
     * Set the control value
     *
     * @param controlValue The control value to store
     */
    public void setControlValue( OctetString controlValue )
    {
        this.controlValue = controlValue;
    }

    /**
     * Get the criticality
     *
     * @return <code>true</code> if the criticality flag is true.
     */
    public boolean getCriticality()
    {
        return criticality;
    }

    /**
     * Set the criticality
     *
     * @param criticality The criticality value
     */
    public void setCriticality( boolean criticality )
    {
        this.criticality = criticality;
    }

    /**
     * Compute the Control length
     * 
     * Control :
     * 
     * 0x30 L1
     *  |
     *  +--> 0x04 L2 controlType
     * [+--> 0x01 0x01 criticality]
     * [+--> 0x04 L3 controlValue] 
     * 
     * Control length = Length(0x30) + length(L1) 
     *                  + Length(0x04) + Length(L2) + L2
     *                  [+ Length(0x01) + 1 + 1]
     *                  [+ Length(0x04) + Length(L3) + L3]
     */
    public int computeLength()
    {
        // The controlType
        int controlTypeLengh = controlType.getOIDLength();
        controlLength = 1 + Length.getNbBytes( controlTypeLengh ) + controlTypeLengh;  
        
        // The criticality, only if true
        if (criticality == true)
        {
            controlLength += 1 + 1 + 1; // Always 3 for a boolean
        }
        
        // The control value, if any
        if (controlValue != null)
        {
            controlLength += 1 + Length.getNbBytes( controlValue.getNbBytes() ) + controlValue.getNbBytes();
        }
        
        return 1 + Length.getNbBytes( controlLength ) + controlLength;
    }
    
    /**
     * Generate the PDU which contains the Control.
     * 
     * Control :
     * 0x30 LL
     *   0x04 LL type 
     *   [0x01 0x01 criticality]
     *   [0x04 LL value]
     *   
     * @param object The encoded PDU
     * @return A ByteBuffer that contaons the PDU
     * @throws EncoderException If anything goes wrong.
     */
    public ByteBuffer encode( ByteBuffer buffer ) throws EncoderException
    {
        try 
        {
            // The LdapMessage Sequence
            buffer.put( UniversalTag.SEQUENCE_TAG );
            
            // The length has been calculated by the computeLength method
            buffer.put( Length.getBytes( controlLength ) );
        }
        catch ( BufferOverflowException boe )
        {
            throw new EncoderException("The PDU buffer size is too small !"); 
        }
            
        // The control type
        Value.encode( buffer, controlType );

        // The control criticality, if true
        if ( criticality == true )
        {
            Value.encode( buffer, criticality );
        }
        
        // The control value, if any
        if ( controlValue != null )
        {
            Value.encode( buffer, controlValue );
        }

        return buffer;
    }

    /**
     * Return a String representing a Control
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("    Control\n");
        sb.append("        Control type : '").append( controlType.toString() ).append("'\n");
        sb.append("        Criticality : '").append( criticality ).append( "'\n") ;
        
        if ( controlValue != null ) 
        {
            sb.append("        Control value : '").append(controlValue.toString()).append("'\n");
        }
        
        return sb.toString();
    }
}