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
package org.apache.directory.shared.ldap.message.extended;


import org.apache.directory.shared.ldap.message.ExtendedResponseImpl;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;


/**
 * The response sent back from the server with a LaunchDiagnosticUiRequest
 * extended operation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class LaunchDiagnosticUiResponse extends ExtendedResponseImpl
{
    private static final long serialVersionUID = -3824715470944544189L;

    public static final String EXTENSION_OID = "1.2.6.1.4.1.18060.1.1.1.100.2";

    private static final byte[] EMPTY_RESPONSE = new byte[0];


    public LaunchDiagnosticUiResponse(int messageId, ResultCodeEnum rcode)
    {
        super( messageId, EXTENSION_OID );

        switch ( rcode.getValue() )
        {
            case ( ResultCodeEnum.SUCCESS_VAL  ):
                break;
            case ( ResultCodeEnum.OPERATIONSERROR_VAL  ):
                break;
            case ( ResultCodeEnum.INSUFFICIENTACCESSRIGHTS_VAL  ):
                break;
            default:
                throw new IllegalArgumentException( "The result code can only be one of: " + ResultCodeEnum.SUCCESS
                    + ", " + ResultCodeEnum.OPERATIONSERROR + ", " + ResultCodeEnum.INSUFFICIENTACCESSRIGHTS );
        }
        super.getLdapResult().setMatchedDn( null );
        super.getLdapResult().setResultCode( rcode );
    }


    public LaunchDiagnosticUiResponse(int messageId)
    {
        super( messageId, EXTENSION_OID );
        super.getLdapResult().setMatchedDn( null);
        super.getLdapResult().setResultCode( ResultCodeEnum.SUCCESS );
    }


    // ------------------------------------------------------------------------
    // ExtendedResponse Interface Method Implementations
    // ------------------------------------------------------------------------

    /**
     * Gets the reponse OID specific encoded response values.
     * 
     * @return the response specific encoded response values.
     */
    public byte[] getResponse()
    {
        return EMPTY_RESPONSE;
    }


    /**
     * Sets the reponse OID specific encoded response values.
     * 
     * @param value
     *            the response specific encoded response values.
     */
    public void setResponse( byte[] value )
    {
        throw new UnsupportedOperationException( "the response is hardcoded as zero length array" );
    }


    /**
     * Gets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @return the OID of the extended response type.
     */
    public String getResponseName()
    {
        return EXTENSION_OID;
    }


    /**
     * Sets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @param oid
     *            the OID of the extended response type.
     */
    public void setResponseName( String oid )
    {
        throw new UnsupportedOperationException( "the OID is fixed: " + EXTENSION_OID );
    }


    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( obj instanceof LaunchDiagnosticUiResponse )
        {
            return true;
        }

        return false;
    }
}