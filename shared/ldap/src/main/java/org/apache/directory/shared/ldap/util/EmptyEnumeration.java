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
 * $Id: EmptyEnumeration.java,v 1.1 2003/09/16 05:29:44 akarasulu Exp $
 *
 * -- (c) LDAPd Group                                                    --
 * -- Please refer to the LICENSE.txt file in the root directory of      --
 * -- any LDAPd project for copyright and distribution information.      --
 *
 * Created on Aug 11, 2003
 */
package org.apache.directory.shared.ldap.util;


import java.util.NoSuchElementException;

import javax.naming.NamingException;
import javax.naming.NamingEnumeration;


/**
 * An empty NamingEnumeration without any values: meaning
 * hasMore/hasMoreElements() always returns false, and next/nextElement() always
 * throws a NoSuchElementException.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: akarasulu $
 * @version $Revision$
 */
public class EmptyEnumeration implements NamingEnumeration
{

    /**
     * @see javax.naming.NamingEnumeration#close()
     */
    public void close()
    {
    }


    /**
     * Always returns false.
     * 
     * @see javax.naming.NamingEnumeration#hasMore()
     */
    public boolean hasMore() throws NamingException
    {
        return false;
    }


    /**
     * Always throws NoSuchElementException.
     * 
     * @see javax.naming.NamingEnumeration#next()
     */
    public Object next() throws NamingException
    {
        throw new NoSuchElementException();
    }


    /**
     * Always return false.
     * 
     * @see java.util.Enumeration#hasMoreElements()
     */
    public boolean hasMoreElements()
    {
        return false;
    }


    /**
     * Always throws NoSuchElementException.
     * 
     * @see java.util.Enumeration#nextElement()
     */
    public Object nextElement()
    {
        throw new NoSuchElementException();
    }

}
