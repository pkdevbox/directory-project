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

package org.apache.asn1.der;

/**
 * DER NumericString - a string of ASCII numeric characters { 0,1,2,3,4,5,6,7,8,9 }.
 */
public class DERNumericString extends DERString
{
    /**
     * Basic DERObject constructor.
     */
    DERNumericString( byte[] value )
    {
    	super( NUMERIC_STRING, value );
    }
    
    /**
     * Static factory method, type-conversion operator.
     */
    public static DERNumericString valueOf( String string )
    {
    	return new DERNumericString( stringToByteArray( string ));
    }
}

