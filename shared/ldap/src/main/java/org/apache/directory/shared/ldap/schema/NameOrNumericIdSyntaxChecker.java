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
package org.apache.directory.shared.ldap.schema;


import javax.naming.NamingException;


import org.apache.directory.shared.ldap.exception.LdapInvalidAttributeValueException;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * A SyntaxChecker which verifies that a value is either a name or a numeric id 
 * according to RFC 4512.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class NameOrNumericIdSyntaxChecker implements SyntaxChecker
{
    public static final String DEFAULT_OID = "1.3.6.1.4.1.18060.0.4.0.0.0";
    private final String oid;
    
    
    public NameOrNumericIdSyntaxChecker( String oid )
    {
        this.oid = oid;
    }
    
    
    public NameOrNumericIdSyntaxChecker()
    {
        this.oid = DEFAULT_OID;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.SyntaxChecker#assertSyntax(java.lang.Object)
     */
    public void assertSyntax( Object value ) throws NamingException
    {
        if ( ! isValidSyntax( value ) )
        {
            throw new LdapInvalidAttributeValueException( ResultCodeEnum.INVALID_ATTRIBUTE_SYNTAX );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.SyntaxChecker#getSyntaxOid()
     */
    public String getSyntaxOid()
    {
        return oid;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.SyntaxChecker#isValidSyntax(java.lang.Object)
     */
    public boolean isValidSyntax( Object value )
    {
        String strValue;

        if ( value == null )
        {
            return false;
        }
        
        if ( value instanceof String )
        {
            strValue = ( String ) value;
        }
        else if ( value instanceof byte[] )
        {
            strValue = StringTools.utf8ToString( ( byte[] ) value ); 
        }
        else
        {
            strValue = value.toString();
        }

        if ( strValue.length() == 0 )
        {
            return false;
        }
        
        // if the first character is a digit it's an attempt at an OID and must be
        // checked to make sure there are no other chars except '.' and digits.
        if ( Character.isDigit( strValue.charAt( 0 ) ) )
        {
            if ( ! org.apache.directory.shared.asn1.primitives.OID.isOID( strValue ) )
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        // here we just need to make sure that we have the right characters in the 
        // string and that it starts with a letter.
        if ( Character.isLetter( strValue.charAt( 0 ) ) )
        {
            final int limit = strValue.length();
            for ( int ii = 0; ii < limit; ii++ )
            {
                char ch = strValue.charAt( ii );
                
                if ( ! Character.isLetter( ch ) && ! Character.isDigit( ch ) && ! ( '-' == ch ) )
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
        
        return true;
    }
}
