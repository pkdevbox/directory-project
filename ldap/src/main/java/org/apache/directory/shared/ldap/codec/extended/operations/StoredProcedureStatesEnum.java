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

package org.apache.directory.shared.ldap.codec.extended.operations;


import org.apache.directory.shared.asn1.ber.grammar.IGrammar;
import org.apache.directory.shared.asn1.ber.grammar.IStates;


/**
 * Constants for StoredProcedureGrammar
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StoredProcedureStatesEnum implements IStates
{
    //~ Static fields/initializers -----------------------------------------------------------------

    //=========================================================================
    // StoredProcedure
    //=========================================================================
    /** starting state */
    public static int START_STATE = 0;

    /** StoredProcedure */
    public static int STORED_PROCEDURE_STATE = 1;

    // Language ---------------------------------------------------------------
    /** Language */
    public static int LANGUAGE_STATE = 2;

    // Procedure --------------------------------------------------------------
    /** Procedure */
    public static int PROCEDURE_STATE = 3;

    // Parameters -------------------------------------------------------------
    /** Parameters */
    public static int PARAMETERS_STATE = 4;

    // Parameter --------------------------------------------------------------
    /** Parameter */
    public static int PARAMETER_STATE = 5;

    // Parameter type ---------------------------------------------------------
    /** Parameter type */
    public static int PARAMETER_TYPE_STATE = 6;

    // Parameters value -------------------------------------------------------
    /** Parameter value */
    public static int PARAMETER_VALUE_STATE = 7;

    public static int LAST_STORED_PROCEDURE_STATE = 8;

    //=========================================================================
    // States debug strings 
    //=========================================================================
    /** A string representation of all the states */
    private static String[] StoredProcedureString = new String[]
        { 
        "START_STATE", 
        "STORED_PROCEDURE_STATE", 
        "LANGUAGE_STATE", 
        "PROCEDURE_STATE", 
        "PARAMETERS_STATE", 
        "PARAMETER_TYPE_STATE",
        "PARAMETER_VALUE_STATE" 
        };

    /** The instance */
    private static StoredProcedureStatesEnum instance = new StoredProcedureStatesEnum();


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * This is a private constructor. This class is a singleton
     *
     */
    private StoredProcedureStatesEnum()
    {
    }


    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get an instance of this class
     * @return An instance on this class
     */
    public static IStates getInstance()
    {
        return instance;
    }


    /**
     * Get the grammar name
     * @param The grammar code
     * @return The grammar name
     */
    public String getGrammarName( int grammar )
    {
        return "STORED_PROCEDURE_GRAMMAR";
    }


    /**
     * Get the grammar name
     * @param The grammar class
     * @return The grammar name
     */
    public String getGrammarName( IGrammar grammar )
    {
        if ( grammar instanceof StoredProcedureGrammar )
        {
            return "STORED_PROCEDURE_GRAMMAR";
        }
        else
        {
            return "UNKNOWN GRAMMAR";
        }
    }


    /**
     * Get the string representing the state
     * 
     * @param grammar The current grammar being used
     * @param state The state number
     * @return The String representing the state
     */
    public String getState( int state )
    {
        return ( ( state == GRAMMAR_END ) ? "STORED_PROCEDURE_END_STATE" : StoredProcedureString[state] );
    }
}