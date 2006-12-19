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
package org.apache.directory.shared.ldap.schema.syntax;


/**
 * A SyntaxChecker implementation which accepts all values as valid.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class AcceptAllSyntaxChecker extends AbstractSyntaxChecker
{
    /**
     * Creates a SyntaxChecker which accepts all values.
     * 
     * @param oid the oid of the Syntax this checker is associated with
     */
    public AcceptAllSyntaxChecker(String oid)
    {
        super( oid );
    }


    /**
     * Returns true every time.
     * 
     * @see SyntaxChecker#isValidSyntax(Object)
     */
    public boolean isValidSyntax( Object value )
    {
        return true;
    }
}
