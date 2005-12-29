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
package org.apache.ldap.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.ldap.server.configuration.Configuration;
import org.apache.ldap.server.jndi.AbstractContextFactory;
import org.apache.ldap.server.partition.DirectoryPartition;

/**
 * Provides JNDI service to {@link AbstractContextFactory}.
 *
 * @author The Apache Directory Project
 * @version $Rev$, $Date$
 */
public abstract class DirectoryService
{
    private static final Map instances = new HashMap();

    /**
     * Returns the default instance.  This method is identical with calling
     * <tt>getInstance( Configuration.DEFAULT_INSTANCE_ID )</tt>.
     */
    public static DirectoryService getInstance()
    {
        return getInstance( Configuration.DEFAULT_INSTANCE_ID );
    }
    
    /**
     * Returns {@link DirectoryService} with the specified instance ID.
     */
    public synchronized static DirectoryService getInstance( String instanceId )
    {
        instanceId = instanceId.trim();
        DirectoryService service = ( DirectoryService ) instances.get( instanceId );
        if( service == null )
        {
            service = new DefaultDirectoryService( instanceId );
            instances.put( instanceId, service );
        }
        
        return service;
    }
    
    /**
     * Returns all instances of instantiated {@link DirectoryService}.
     */
    public synchronized static Set getAllInstances()
    {
        return new HashSet( instances.values() );
    }

    /**
     * Starts up this service.
     * 
     * @param listener a listener that listens to the lifecycle of this service
     * @param environment JNDI {@link InitialContext} environment
     * 
     * @throws NamingException if failed to start up
     */
    public abstract void startup( DirectoryServiceListener listener, Hashtable environment ) throws NamingException;
    
    /**
     * Shuts down this service.
     * 
     * @throws NamingException if failed to shut down
     */
    public abstract void shutdown() throws NamingException;
    
    /**
     * Calls {@link DirectoryPartition#sync()} for all registered {@link DirectoryPartition}s.
     * @throws NamingException if synchronization failed
     */
    public abstract void sync() throws NamingException;
    
    /**
     * Returns <tt>true</tt> if this service is started.
     */
    public abstract boolean isStarted();
    
    /**
     * Returns the configuration of this service.
     */
    public abstract DirectoryServiceConfiguration getConfiguration();

    /**
     * Returns an anonymous JNDI {@link Context} with the specified <tt>baseName</tt>
     * @throws NamingException if failed to create a context
     */
    public abstract Context getJndiContext( String baseName ) throws NamingException;
    
    /**
     * Returns a JNDI {@link Context} with the specified authentication information
     * (<tt>principal</tt>, <tt>credential</tt>, and <tt>authentication</tt>) and
     * <tt>baseName</tt>.
     * 
     * @param principal {@link Context#SECURITY_PRINCIPAL} value
     * @param credential {@link Context#SECURITY_CREDENTIALS} value
     * @param authentication {@link Context#SECURITY_AUTHENTICATION} value
     * @throws NamingException if failed to create a context
     */
    public abstract Context getJndiContext( String principal, byte[] credential, String authentication, String baseName ) throws NamingException;
}
