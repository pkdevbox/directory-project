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
package org.apache.directory.shared.ldap.name;


import javax.naming.InvalidNameException;
import javax.naming.NamingException;

import junit.framework.TestCase;


/**
 * Testcase devised specifically for DIRSERVER-584.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 * @see <a href="https://issues.apache.org/jira/browse/DIRSERVER-584">DIRSERVER-584</a>
 */
public class DnParserDIRSERVER_584_Test extends TestCase
{
    static DnParser parser; 
    
    
    public static DnParser getParser() throws NamingException
    {
        if ( parser == null )
        {
            parser = new DnParser();
        }
        
        return parser;
    }
    
    
    /**
     * Need this testa() to run first to mess up the state of the static parser.
     */
    public void testa() throws Exception
    {
        DnParser parser = getParser();
        
        try
        {
            parser.parse( "ou=test=testing" );
            fail( "should never get here" );
        }
        catch ( InvalidNameException e )
        {
        }
    }
    
    
    /**
     * Need this testb() to run second to use the mess up static parser.  This 
     * test should succeed but fails.
     */
    public void testb() throws Exception
    {
        DnParser parser = getParser();
        parser.parse( "ou=system" );
    }
}