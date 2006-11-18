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

import javax.naming.directory.Attributes;


/**
 * TestCase for the AddRequestImpl class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class AddRequestImplTest extends TestCase
{
    /**
     * Creates and populates a LockableAttributeImpl with a specific id.
     *
     * @param id the id for the attribute
     * @return the LockableAttributeImpl assembled for testing
     */
    private LockableAttributeImpl getAttribute( String id )
    {
        LockableAttributeImpl attr = new LockableAttributeImpl( id );
        attr.add( "value0" );
        attr.add( "value1" );
        attr.add( "value2" );
        return attr;
    }


    /**
     * Creates and populates a LockableAttributes object
     *
     * @return
     */
    private LockableAttributesImpl getAttributes()
    {
        LockableAttributesImpl attrs = new LockableAttributesImpl();
        attrs.put( getAttribute( "attr0" ) );
        attrs.put( getAttribute( "attr1" ) );
        attrs.put( getAttribute( "attr2" ) );
        return attrs;
    }


    /**
     * Tests the same object referrence for equality.
     */
    public void testEqualsSameObj()
    {
        AddRequestImpl req = new AddRequestImpl( 5 );
        assertTrue( req.equals( req ) );
    }


    /**
     * Tests for equality using exact copies.
     */
    public void testEqualsExactCopy()
    {
        AddRequestImpl req0 = new AddRequestImpl( 5 );
        req0.setName( "cn=admin,dc=example,dc=com" );
        req0.setEntry( getAttributes() );

        AddRequestImpl req1 = new AddRequestImpl( 5 );
        req1.setName( "cn=admin,dc=example,dc=com" );
        req1.setEntry( getAttributes() );

        assertTrue( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the IDs are different.
     */
    public void testNotEqualDiffId()
    {
        AddRequestImpl req0 = new AddRequestImpl( 7 );
        req0.setName( "cn=admin,dc=example,dc=com" );
        req0.setEntry( getAttributes() );

        AddRequestImpl req1 = new AddRequestImpl( 5 );
        req1.setName( "cn=admin,dc=example,dc=com" );
        req1.setEntry( getAttributes() );

        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the DN names are different.
     */
    public void testNotEqualDiffName()
    {
        AddRequestImpl req0 = new AddRequestImpl( 5 );
        req0.setName( "cn=admin,dc=example,dc=com" );
        req0.setEntry( getAttributes() );

        AddRequestImpl req1 = new AddRequestImpl( 5 );
        req1.setName( "cn=admin,dc=apache,dc=org" );
        req1.setEntry( getAttributes() );


        assertFalse( req0.equals( req1 ) );
    }


    /**
     * Test for inequality when only the DN names are different.
     */
    public void testNotEqualDiffAttributes()
    {
        AddRequestImpl req0 = new AddRequestImpl( 5 );
        req0.setName( "cn=admin,dc=apache,dc=org" );
        req0.setEntry( getAttributes() );

        AddRequestImpl req1 = new AddRequestImpl( 5 );
        req1.setName( "cn=admin,dc=apache,dc=org" );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );

        req1.setEntry( getAttributes() );

        assertTrue( req0.equals( req1 ) );
        assertTrue( req1.equals( req0 ) );

        req1.getEntry().put( "asdf", "asdf" );

        assertFalse( req0.equals( req1 ) );
        assertFalse( req1.equals( req0 ) );
    }


    /**
     * Tests for equality even when another BindRequest implementation is used.
     */
    public void testEqualsDiffImpl()
    {
        AddRequest req0 = new AddRequest()
        {
            public Attributes getEntry()
            {
                return AddRequestImplTest.this.getAttributes();
            }

            public void setEntry( Attributes a_entry )
            {
            }

            public String getName()
            {
                return null;
            }

            public void setName( String a_name )
            {
            }

            public MessageTypeEnum getResponseType()
            {
                return MessageTypeEnum.ADDRESPONSE;
            }

            public boolean hasResponse()
            {
                return true;
            }

            public MessageTypeEnum getType()
            {
                return MessageTypeEnum.ADDREQUEST;
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
                return 5;
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

        AddRequestImpl req1 = new AddRequestImpl( 5 );
        req1.setEntry( getAttributes() );
        assertTrue( req1.equals( req0 ) );
    }
}