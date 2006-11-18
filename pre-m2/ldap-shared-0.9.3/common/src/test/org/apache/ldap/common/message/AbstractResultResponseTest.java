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
import org.apache.ldap.common.Lockable;
import org.apache.ldap.common.LockException;


/**
 * TestCase for the methods of the AbstractResultResponse class.
 *
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory
 *         Project</a>
 * @version $Rev$
 */
public class AbstractResultResponseTest extends TestCase
{
    /**
     * Tests to see the same object returns true.
     */
    public void testEqualsSameObj()
    {
        AbstractResultResponse msg;
        msg = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        assertTrue( msg.equals( msg ) );
    }


    /**
     * Tests to see the same exact copy returns true.
     */
    public void testEqualsExactCopy()
    {
        LdapResultImpl r0 = new LdapResultImpl( null );
        LdapResultImpl r1 = new LdapResultImpl( null );

        r0.setErrorMessage( "blah blah blah" );
        r1.setErrorMessage( "blah blah blah" );

        r0.setMatchedDn( "dc=example,dc=com" );
        r1.setMatchedDn( "dc=example,dc=com" );

        r0.setResultCode( ResultCodeEnum.TIMELIMITEXCEEDED );
        r1.setResultCode( ResultCodeEnum.TIMELIMITEXCEEDED );

        Referral refs0 = new ReferralImpl( r0 );
        refs0.addLdapUrl( "ldap://someserver.com" );
        refs0.addLdapUrl( "ldap://anotherserver.org" );

        Referral refs1 = new ReferralImpl( r1 );
        refs1.addLdapUrl( "ldap://someserver.com" );
        refs1.addLdapUrl( "ldap://anotherserver.org" );

        AbstractResultResponse msg0;
        AbstractResultResponse msg1;
        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        msg0.setLdapResult( r0 );
        msg1 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        msg1.setLdapResult( r1 );
        assertTrue( msg0.equals( msg1 ) );
        assertTrue( msg1.equals( msg0 ) );
    }


    /**
     * Tests to see the same exact copy returns true.
     */
    public void testNotEqualsDiffResult()
    {
        LdapResultImpl r0 = new LdapResultImpl( null );
        LdapResultImpl r1 = new LdapResultImpl( null );

        r0.setErrorMessage( "blah blah blah" );
        r1.setErrorMessage( "blah blah blah" );

        r0.setMatchedDn( "dc=example,dc=com" );
        r1.setMatchedDn( "dc=apache,dc=org" );

        r0.setResultCode( ResultCodeEnum.TIMELIMITEXCEEDED );
        r1.setResultCode( ResultCodeEnum.TIMELIMITEXCEEDED );

        Referral refs0 = new ReferralImpl( r0 );
        refs0.addLdapUrl( "ldap://someserver.com" );
        refs0.addLdapUrl( "ldap://anotherserver.org" );

        Referral refs1 = new ReferralImpl( r1 );
        refs1.addLdapUrl( "ldap://someserver.com" );
        refs1.addLdapUrl( "ldap://anotherserver.org" );

        AbstractResultResponse msg0;
        AbstractResultResponse msg1;
        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        msg0.setLdapResult( r0 );
        msg1 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        msg1.setLdapResult( r1 );
        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }


    /**
     * Tests to make sure changes in the id result in inequality.
     */
    public void testNotEqualsDiffId()
    {
        AbstractResultResponse msg0;
        AbstractResultResponse msg1;
        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        msg1 = new AbstractResultResponse( 6, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }


    /**
     * Tests to make sure changes in the type result in inequality.
     */
    public void testNotEqualsDiffType()
    {
        AbstractResultResponse msg0;
        AbstractResultResponse msg1;
        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        msg1 = new AbstractResultResponse( 5, MessageTypeEnum.UNBINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }


    /**
     * Tests to make sure changes in the controls result in inequality.
     */
    public void testNotEqualsDiffControls()
    {
        AbstractResultResponse msg0;
        AbstractResultResponse msg1;
        msg0 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        msg0.add( new Control() {
            private static final long serialVersionUID = 1L;
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

            public String getType()
            {
                return null;
            }

            public void setType( String a_oid )
            {
            }

            public byte[] getValue()
            {
                return new byte[0];
            }

            public void setValue( byte[] a_value )
            {
            }

            public boolean isCritical()
            {
                return false;
            }

            public void setCritical( boolean a_isCritical )
            {
            }

            public byte[] getEncodedValue()
            {
                return null;
            }

            public String getID()
            {
                return null;
            }
        });
        msg1 = new AbstractResultResponse( 5, MessageTypeEnum.BINDREQUEST ) {
            private static final long serialVersionUID = 1L;};
        assertFalse( msg0.equals( msg1 ) );
        assertFalse( msg1.equals( msg0 ) );
    }
}