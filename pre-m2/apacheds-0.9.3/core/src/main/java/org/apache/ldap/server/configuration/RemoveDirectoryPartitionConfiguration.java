/*
 *   @(#) $Id$
 *
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
package org.apache.ldap.server.configuration;

import javax.naming.Name;
import javax.naming.NamingException;

import org.apache.ldap.common.name.LdapName;
import org.apache.ldap.server.partition.DirectoryPartition;
import org.apache.ldap.server.partition.DirectoryPartitionNexus;

/**
 * A {@link Configuration} that removed the attached {@link DirectoryPartition} in
 * the current {@link DirectoryPartitionNexus}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RemoveDirectoryPartitionConfiguration extends Configuration
{
    private static final long serialVersionUID = -6690435863387769527L;
    
    private final Name suffix;

    public RemoveDirectoryPartitionConfiguration( String suffix ) throws NamingException
    {
        this( new LdapName( suffix.trim() ) );
    }
    
    public RemoveDirectoryPartitionConfiguration( Name suffix )
    {
        if( suffix == null )
        {
            throw new NullPointerException( "suffix" );
        }
        
        this.suffix = suffix;
    }
    
    public Name getSuffix()
    {
        return suffix;
    }
}