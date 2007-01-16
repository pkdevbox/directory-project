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
package org.safehaus.triplesec.configuration;


/**
 * An SMS provider configuration.
 * 
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 * @version $Rev$
 */
public class SmsConfiguration
{
    private String username;
    private String password;
    private String accountName;
    private String transportUrl;
    
    
    public void setSmsUsername( String username )
    {
        this.username = username;
    }
    
    
    public String getSmsUsername()
    {
        return username;
    }


    public void setSmsPassword( String password )
    {
        this.password = password;
    }


    public String getSmsPassword()
    {
        return password;
    }


    public void setSmsAccountName( String accountName )
    {
        this.accountName = accountName;
    }


    public String getSmsAccountName()
    {
        return accountName;
    }


    public void setSmsTransportUrl( String transportUrl )
    {
        this.transportUrl = transportUrl;
    }


    public String getSmsTransportUrl()
    {
        return transportUrl;
    }
}