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
package org.apache.directory.shared.ldap.util;


import java.io.IOException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.directory.shared.ldap.message.AttributeImpl;
import org.apache.directory.shared.ldap.message.AttributesImpl;

/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributesSerializerUtils
{
    private static final long serialVersionUID = -3756830073760754086L;
    private static final byte SEPARATOR = -1;


    /**
     * @see jdbm.helper.Serializer#deserialize(byte[])
     */
    public static final Object deserialize( byte[] buf ) throws IOException
    {
        if ( buf.length == 0 )
        {
            return new AttributesImpl();
        }

        int pos = 0;
        AttributesImpl attrs = new AttributesImpl();
        while ( pos < buf.length )
        {
            String id = AttributeSerializerUtils.readString( buf, pos );
            AttributeImpl attr = new AttributeImpl( id );
            pos += ( id.length() << 1 ) + 4;

            // read the type of the objects stored in this attribute
            if ( buf[pos] == AttributeSerializerUtils.STRING_TYPE )
            {
                pos++;
                while ( pos < buf.length && buf[pos] != SEPARATOR )
                {
                    String value = AttributeSerializerUtils.readString( buf, pos );
                    pos += ( value.length() << 1 ) + 4;
                    attr.add( value );
                }
            }
            else
            {
                pos++;
                while ( pos < buf.length && buf[pos] != SEPARATOR )
                {
                    byte[] value = AttributeSerializerUtils.readBytes( buf, pos );
                    pos += value.length + 4;
                    attr.add( value );
                }
            }

            pos++; // skip the separator
            attrs.put( attr );
        }

        return attrs;
    }


    /**
     * @see jdbm.helper.Serializer#serialize(java.lang.Object)
     */
    public static final byte[] serialize( Object attrsObj ) throws IOException
    {
        Attributes attrs = ( Attributes ) attrsObj;

        // calculate the size of the entire byte[] and allocate
        byte[] buf = new byte[calculateSize( attrs )];
        int pos = 0;
        try
        {
            for ( NamingEnumeration ii = attrs.getAll(); ii.hasMore(); /**/)
            {
                // get an attribute at a time
                Attribute attr = ( Attribute ) ii.next();

                // write the length of the id and it's value
                pos = AttributeSerializerUtils.write( buf, attr.getID(), pos );

                // write the type or is-binary field
                Object first = attr.get();
                if ( first instanceof String )
                {
                    buf[pos] = AttributeSerializerUtils.STRING_TYPE;
                    pos++;

                    // write out each value to the buffer whatever type it may be
                    for ( NamingEnumeration jj = attr.getAll(); jj.hasMore(); /**/)
                    {
                        String value = ( String ) jj.next();
                        pos = AttributeSerializerUtils.write( buf, value, pos );
                    }
                }
                else
                {
                    buf[pos] = AttributeSerializerUtils.BYTE_ARRAY_TYPE;
                    pos++;

                    // write out each value to the buffer whatever type it may be
                    for ( NamingEnumeration jj = attr.getAll(); jj.hasMore(); /**/)
                    {
                        byte[] value = ( byte[] ) jj.next();
                        pos = AttributeSerializerUtils.write( buf, value, pos );
                    }
                }
                
                if ( ii.hasMore() )
                {
                    buf[pos] = SEPARATOR;
                    pos++;
                }
            }
        }
        catch ( NamingException e )
        {
            IOException ioe = new IOException( "Failed while accesssing attributes and/or their values." );
            ioe.initCause( e );
            throw ioe;
        }

        return buf;
    }


    private static final int calculateSize( Attributes attrs ) throws IOException
    {
        int size = 0;

        try
        {
            for ( NamingEnumeration ii = attrs.getAll(); ii.hasMore(); /**/)
            {
                Attribute attr = ( Attribute ) ii.next();

                if ( ii.hasMore() )
                {
                    // augment by attribute size and 1 for the separator
                    size += AttributeSerializerUtils.calculateSize( attr ) + 1;
                }
                else
                {
                    // augment by attribute size only since there are no more attributes left
                    size += AttributeSerializerUtils.calculateSize( attr );
                }
            }
        }
        catch ( NamingException e )
        {
            IOException ioe = new IOException( "Failed while accesssing attributes." );
            ioe.initCause( e );
            throw ioe;
        }

        return size;
    }
}
