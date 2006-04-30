/*
 *   Copyright 2006 The Apache Software Foundation
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

package org.apache.directory.shared.ldap.trigger;


/**
 * An entity that represents a stored procedure parameter which can be
 * specified in an LDAP Trigger Specification.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev:$, $Date:$
 */
public class StoredProcedureParameter
{

    public static final StoredProcedureParameter OPERATION_TIME = new StoredProcedureParameter( "operationTime" );
    public static final StoredProcedureParameter OPERATION_PRINCIPAL = new StoredProcedureParameter(
        "operationPrincipal" );

    private final String name;


    protected StoredProcedureParameter( String name )
    {
        this.name = name;
    }


    /**
     * Returns the name of this Stored Procedure Parameter.
     */
    public String getName()
    {
        return name;
    }


    public String toString()
    {
        return name;
    }
    
    // Operation Specific Subclasses

    public static class BindStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final BindStoredProcedureParameter VERSION = new BindStoredProcedureParameter( "$version" );
        public static final BindStoredProcedureParameter NAME = new BindStoredProcedureParameter( "$name" );
        public static final BindStoredProcedureParameter AUTHENTICATION = new BindStoredProcedureParameter(
            "$authentication" );


        private BindStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class UnbindStoredProcedureParameter extends StoredProcedureParameter
    {

        private UnbindStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class SearchStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final SearchStoredProcedureParameter BASE_OBJECT = new SearchStoredProcedureParameter(
            "$baseObject" );
        public static final SearchStoredProcedureParameter SCOPE = new SearchStoredProcedureParameter( "$scope" );
        public static final SearchStoredProcedureParameter DEREF_ALIASES = new SearchStoredProcedureParameter(
            "$derefAliases" );
        public static final SearchStoredProcedureParameter SIZE_LIMIT = new SearchStoredProcedureParameter(
            "$sizeLimit" );
        public static final SearchStoredProcedureParameter TIME_LIMIT = new SearchStoredProcedureParameter(
            "$timeLimit" );
        public static final SearchStoredProcedureParameter TYPES_ONLY = new SearchStoredProcedureParameter(
            "$typesOnly" );
        public static final SearchStoredProcedureParameter FILTER = new SearchStoredProcedureParameter( "$filter" );
        public static final SearchStoredProcedureParameter ATTRIBUTES = new SearchStoredProcedureParameter(
            "$attributes" );


        private SearchStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class ModifyStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final ModifyStoredProcedureParameter OBJECT = new ModifyStoredProcedureParameter( "$object" );
        public static final ModifyStoredProcedureParameter MODIFICATION = new ModifyStoredProcedureParameter(
            "$modification" );
        public static final ModifyStoredProcedureParameter OLD_ENTRY = new ModifyStoredProcedureParameter( "$oldEntry" );
        public static final ModifyStoredProcedureParameter NEW_ENTRY = new ModifyStoredProcedureParameter( "$newEntry" );


        private ModifyStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class AddStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final DelStoredProcedureParameter ENTRY = new DelStoredProcedureParameter( "$entry" );
        public static final DelStoredProcedureParameter ATTRIBUTES = new DelStoredProcedureParameter( "$attributes" );


        private AddStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class DelStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final AddStoredProcedureParameter NAME = new AddStoredProcedureParameter( "$name" );
        public static final AddStoredProcedureParameter DELETED_ENTRY = new AddStoredProcedureParameter(
            "$deletedEntry" );


        private DelStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class ModDNStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final ModDNStoredProcedureParameter ENTRY = new ModDNStoredProcedureParameter( "$entry" );
        public static final ModDNStoredProcedureParameter NEW_RDN = new ModDNStoredProcedureParameter( "$newrdn" );
        public static final ModDNStoredProcedureParameter DELETE_OLD_RDN = new ModDNStoredProcedureParameter(
            "$deleteoldrdn" );
        public static final ModDNStoredProcedureParameter NEW_SUPERIOR = new ModDNStoredProcedureParameter(
            "$newSuperior" );


        private ModDNStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class CompareStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final CompareStoredProcedureParameter ENTRY = new CompareStoredProcedureParameter( "$entry" );
        public static final CompareStoredProcedureParameter AVA = new CompareStoredProcedureParameter( "$ava" );


        private CompareStoredProcedureParameter( String name )
        {
            super( name );
        }

    }
    
    public static class AbandonStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final AbandonStoredProcedureParameter MESSAGE_ID = new  AbandonStoredProcedureParameter( "$messageId"
     );


        private AbandonStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

    public static class ExtendedStoredProcedureParameter extends StoredProcedureParameter
    {

        public static final ExtendedStoredProcedureParameter REQUEST_NAME = new ExtendedStoredProcedureParameter(
            "$requestName" );
        public static final ExtendedStoredProcedureParameter REQUEST_VALUE = new ExtendedStoredProcedureParameter(
            "$requestValue" );


        private ExtendedStoredProcedureParameter( String name )
        {
            super( name );
        }

    }

}