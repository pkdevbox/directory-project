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
package org.apache.ldap.common.message;


import junit.framework.TestCase;

import java.util.Collection;
import java.util.Collections;

import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.LockException;


/**
 * TestCases for the SearchResponseImpl class methods.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a> $Rev$
 */
public class SearchResponseDoneImplTest extends TestCase
{
    /**
     * Creates and populates a SearchResponseDoneImpl stub for testing purposes.
     *
     * @return a populated SearchResponseDoneImpl stub
     */
    private SearchResponseDoneImpl createStub()
    {
        // Construct the Search response to test with results and referrals
        SearchResponseDoneImpl response = new SearchResponseDoneImpl( 45 );
        LdapResultImpl result = new LdapResultImpl( response );
        response.setLdapResult( result );
        result.setMatchedDn( "dc=example,dc=com" );
        result.setResultCode( ResultCodeEnum.SUCCESS );
        ReferralImpl refs = new ReferralImpl( result );
        refs.addLdapUrl( "ldap://someserver.com" );
        refs.addLdapUrl( "ldap://apache.org" );
        refs.addLdapUrl( "ldap://another.net" );
        result.setReferral( refs );
        return response;
    }


    /**
     * Tests for equality using the same object.
     */
    public void testEqualsSameObj()
    {
        SearchResponseDoneImpl resp = createStub();
        assertTrue( resp.equals( resp ) );
    }


    /**
     * Tests for equality using an exact copy.
     */
    public void testEqualsExactCopy()
    {
        SearchResponseDoneImpl resp0 = createStub();
        SearchResponseDoneImpl resp1 = createStub();
        assertTrue( resp0.equals( resp1 ) );
        assertTrue( resp1.equals( resp0 ) );
    }


    /**
     * Tests for equality using different stub implementations.
     */
    public void testEqualsDiffImpl()
    {
        SearchResponseDoneImpl resp0 = createStub();
        SearchResponseDone resp1 = new SearchResponseDone()
        {
            public LdapResult getLdapResult()
            {
                LdapResultImpl result = new LdapResultImpl( this );
                result.setMatchedDn( "dc=example,dc=com" );
                result.setResultCode( ResultCodeEnum.SUCCESS );
                ReferralImpl refs = new ReferralImpl( result );
                refs.addLdapUrl( "ldap://someserver.com" );
                refs.addLdapUrl( "ldap://apache.org" );
                refs.addLdapUrl( "ldap://another.net" );
                result.setReferral( refs );

                return result;
            }

            public void setLdapResult( LdapResult a_result )
            {
            }

            public MessageTypeEnum getType()
            {
                return MessageTypeEnum.SEARCHRESDONE;
            }

            public Collection getControls()
            {
                return Collections.EMPTY_LIST;
            }

            public void add( Control a_control ) throws MessageException
            {
            }

            public void remove( Control a_control ) throws MessageException
            {
            }

            public int getMessageId()
            {
                return 45;
            }

            public Object get( Object a_key )
            {
                return null;
            }

            public Object put( Object a_key, Object a_value )
            {
                return null;
            }

            public Lockable getParent()
            {
                return null;
            }

            public boolean isLocked()
            {
                return false;
            }

            public boolean getLocked()
            {
                return false;
            }

            public void setLocked( boolean a_isLocked ) throws LockException
            {
            }

            public boolean isUnlockable()
            {
                return false;
            }
        };

        assertTrue( resp0.equals( resp1 ) );
        assertFalse( resp1.equals( resp0 ) );
    }


    /**
     * Tests inequality when messageIds are different.
     */
    public void testNotEqualsDiffIds()
    {
        SearchResponseDoneImpl resp0 = new SearchResponseDoneImpl( 3 );
        SearchResponseDoneImpl resp1 = new SearchResponseDoneImpl( 4 );

        assertFalse( resp0.equals( resp1 ) );
        assertFalse( resp1.equals( resp0 ) );
    }
}