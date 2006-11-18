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
package org.apache.ldap.common.message;


/**
 * Lockable AddResponse implementation.
 * 
 * @version $Rev$
 */
public class AddResponseImpl
    extends AbstractResultResponse implements AddResponse
{
    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------


    static final long serialVersionUID = 4027132942339551383L;

    /**
     * Creates a Lockable AddResponse as a reply to an AddRequest.
     *
     * @param id the session unique message id
     */
    public AddResponseImpl( final int id )
    {
        super( id, TYPE ) ;
    }
}