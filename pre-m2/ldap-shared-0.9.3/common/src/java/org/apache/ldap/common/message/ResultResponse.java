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

/*
 * $Id: ResultResponse.java,v 1.1 2003/05/02 00:49:07 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 */

package org.apache.ldap.common.message ;

/**
 * An LDAP Response that contains an LDAPResult structure within it.
 *
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public interface ResultResponse
    extends Response
{
    /**
     * Gets the LdapResult components of this Response.
     *
     * @return the LdapResult for this Response.
     */
    LdapResult getLdapResult() ;

    /**
     * Sets the LdapResult components of this Response.
     *
     * @param a_result the LdapResult for this Response.
     */
    void setLdapResult( LdapResult a_result ) ;
}