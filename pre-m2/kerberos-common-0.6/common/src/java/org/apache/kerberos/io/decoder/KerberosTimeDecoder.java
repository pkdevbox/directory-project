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
package org.apache.kerberos.io.decoder;

import java.text.ParseException;
import java.util.Date;

import org.apache.asn1.der.DERGeneralizedTime;
import org.apache.kerberos.messages.value.KerberosTime;

public class KerberosTimeDecoder
{
    /**
     * KerberosTime ::=   GeneralizedTime
     *             -- Specifying UTC time zone (Z)
     */
    protected static KerberosTime decode( DERGeneralizedTime time )
    {
        Date date = null;

        try
        {
            date = time.getDate();
        }
        catch ( ParseException pe )
        {
            pe.printStackTrace();
        }

        return new KerberosTime( date );
    }
}