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

package org.apache.directory.ldapstudio.browser.core.events;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;


public class ConnectionUpdateEvent
{

    public static final int CONNECTION_OPENED = 1;

    public static final int CONNECTION_CLOSED = 2;

    public static final int CONNECTION_SCHEMA_LOADED = 3;

    public static final int CONNECTION_ADDED = 4;

    public static final int CONNECTION_REMOVED = 5;

    public static final int CONNECTION_PARAMETER_UPDATED = 6;

    public static final int CONNECTION_RENAMED = 7;

    private int detail;

    private IConnection connection;


    public ConnectionUpdateEvent( IConnection connection, int detail )
    {
        this.connection = connection;
        this.detail = detail;
    }


    public IConnection getConnection()
    {
        return connection;
    }


    public int getDetail()
    {
        return detail;
    }

}