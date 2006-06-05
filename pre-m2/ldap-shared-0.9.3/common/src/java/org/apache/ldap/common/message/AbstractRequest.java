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
package org.apache.ldap.common.message ;


/**
 * The base request message class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 * @version $Rev$
 */
public class AbstractRequest
    extends AbstractMessage implements Request
{
    static final long serialVersionUID = -4511116249089399040L;
    /** Flag indicating whether or not this request returns a response. */
    private final boolean m_hasResponse ;


    /**
     * Subclasses must provide these parameters via a super constructor call.
     *
     * @param a_id the sequential message identifier
     * @param a_type the request type enum
     * @param a_hasResponse flag indicating if this request generates a response
     */
    protected AbstractRequest( final int a_id,
        final MessageTypeEnum a_type, boolean a_hasResponse )
    {
        super( a_id, a_type ) ;

        m_hasResponse = a_hasResponse ;
    }


    /**
     * Indicator flag used to determine whether or not this type of request
     * produces a reply.
     *
     * @return true if any reply is generated, false if no response is generated
     */
    public boolean hasResponse()
    {
        return m_hasResponse ;
    }
}