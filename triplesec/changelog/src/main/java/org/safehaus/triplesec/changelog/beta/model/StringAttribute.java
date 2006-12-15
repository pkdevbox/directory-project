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
package org.safehaus.triplesec.changelog.beta.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:ersiner@safehaus.org">Ersin Er</a>
 * @author <a href="mailto:akarasulu@safehaus.org">Alex Karasulu</a>
 */
public class StringAttribute
{
    private String id;
    private List values = new ArrayList();
    
    public StringAttribute( String id )
    {
        this.id = id;
    }
    
    public StringAttribute( String id, String value )
    {
        this( id );
        addValue( value );
    }
    
    public StringAttribute( String id, List values )
    {
        this( id );
        addValues( values );
    }
    

    public String getId()
    {
        return id;
    }


    public List getValues()
    {
        return values;
    }


    public void addValue( String value )
    {
        values.add( value );
    }


    public void addValues( List values )
    {
        // TODO: Need some safity check here
        values.addAll( values );
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        Iterator it = values.iterator();
        
        while ( it.hasNext() )
        {
            buffer.append( id );
            buffer.append( ": " );
            buffer.append( it.next() );
            buffer.append( ", " );
        }
        
        return buffer.toString();
    }

}