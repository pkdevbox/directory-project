/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.apache.naming;


/**
 * Static constants for this package.
 */

public final class Constants {

    public static final String Package = "org.apache.naming";
    
     public static final String FactoryPackage = "org.apache.naming.factory";

    public static final String DEFAULT_RESOURCE_FACTORY = 
        FactoryPackage + ".ResourceFactory";

    public static final String DEFAULT_RESOURCE_LINK_FACTORY = 
        FactoryPackage + ".ResourceLinkFactory";

    public static final String DEFAULT_TRANSACTION_FACTORY = 
        FactoryPackage + ".TransactionFactory";

    public static final String DEFAULT_RESOURCE_ENV_FACTORY = 
        FactoryPackage + ".ResourceEnvFactory";

    public static final String DEFAULT_EJB_FACTORY = 
        FactoryPackage + ".EjbFactory";

    public static final String DBCP_DATASOURCE_FACTORY = 
        "org.apache.commons.dbcp.BasicDataSourceFactory";

}