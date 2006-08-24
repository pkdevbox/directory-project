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
package org.apache.directory.shared.ldap.schema;


import javax.naming.NamingException;


/**
 * A normalizer for the objectIdentifierMatch matching rule.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ObjectIdentifierNormalizer implements Normalizer
{
    public Object normalize( Object value ) throws NamingException
    {
        if ( value == null )
        {
            return null;
        }

        if ( !( value instanceof String ) )
        {
            return value;
        }

        String str = ( ( String ) value ).trim();

        if ( str.length() == 0 )
        {
            return "";
        }
        else if ( Character.isDigit( str.charAt( 0 ) ) )
        {
            // We do this test to avoid a lowerCasing which cost time
            return str;
        }
        else
        {
            return str.toLowerCase();
        }
    }
}