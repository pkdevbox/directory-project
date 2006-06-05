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
 * Lockable UnbindRequest implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">
 * Apache Directory Project</a>
 */
public class UnbindRequestImpl extends AbstractRequest implements UnbindRequest
{
    static final long serialVersionUID = -6217184085100410116L;

    /**
     * Creates an UnbindRequest which takes no parameter other than those in
     * the outer envelope to disconnect and end a client session on the server
     * without producing any response.
     *
     * @param id the sequential message identifier.
     */
    public UnbindRequestImpl( final int id )
    {
        super( id, TYPE, false );
    }
}