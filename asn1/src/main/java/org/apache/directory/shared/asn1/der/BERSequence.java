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

package org.apache.directory.shared.asn1.der;


import java.io.IOException;
import java.util.Enumeration;


public class BERSequence extends DERSequence
{
    public void encode( ASN1OutputStream out ) throws IOException
    {
        out.write( DERObject.SEQUENCE | DERObject.CONSTRUCTED );
        out.write( DERObject.TAGGED );

        Enumeration e = getObjects();
        while ( e.hasMoreElements() )
        {
            out.writeObject( e.nextElement() );
        }

        out.write( DERObject.TERMINATOR );
        out.write( DERObject.TERMINATOR );
    }
}