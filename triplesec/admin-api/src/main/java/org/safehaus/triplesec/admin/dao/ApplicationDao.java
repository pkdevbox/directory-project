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
package org.safehaus.triplesec.admin.dao;


import java.util.Iterator;

import javax.naming.directory.ModificationItem;

import org.safehaus.triplesec.admin.Application;
import org.safehaus.triplesec.admin.DataAccessException;


public interface ApplicationDao
{
    Application add( String name, String description, String userPassword ) throws DataAccessException;
    
    Application modify( String name, ModificationItem[] mods ) throws DataAccessException;

    Application load( String name ) throws DataAccessException;

    Iterator applicationIterator() throws DataAccessException;
    
    void delete( Application application ) throws DataAccessException;

    Application rename( Application application, String newName ) throws DataAccessException;

    boolean has( String name ) throws DataAccessException;
}