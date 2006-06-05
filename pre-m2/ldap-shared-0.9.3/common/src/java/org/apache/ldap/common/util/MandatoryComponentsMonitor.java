/*
 *   Copyright 2005 The Apache Software Foundation
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


package org.apache.ldap.common.util;


/**
 * A monitoring set where all elements are mandatory.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class MandatoryComponentsMonitor extends AbstractSimpleComponentsMonitor
{
    public MandatoryComponentsMonitor( String [] components )
    {
        super( components );
    }

    /**
     * Returns true if all components registered are used.
     */
    public boolean finalStateValid()
    {
        return allComponentsUsed();
    }
}