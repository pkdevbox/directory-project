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
package org.apache.directory.shared.ldap.schema.syntax.parser;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.directory.shared.ldap.schema.syntax.AbstractSchemaDescription;


/**
 * Utils for schema parser test. Contains tests that are common
 * for many schema parsers like OID, name, desc, extension.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SchemaParserTestUtils 
{

    /**
     * Test numericoid
     * 
     * @throws ParseException
     */
    public static void testNumericOid( AbstractSchemaParser parser, String required ) throws ParseException
    {
        String value = null;
        AbstractSchemaDescription asd = null;

        // null test
        value = null;
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, null" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // no oid
        value = "( )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, no NUMERICOID" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // simple
        value = "( 0.1.2.3.4.5.6.7.8.9 "+required+" )";
        asd = parser.parse( value );
        Assert.assertEquals( "0.1.2.3.4.5.6.7.8.9", asd.getNumericOid() );

        // simple
        value = "( 123.4567.890 "+required+")";
        asd = parser.parse( value );
        Assert.assertEquals( "123.4567.890", asd.getNumericOid() );
        
        // simple with spaces
        value = "(          0.1.2.3.4.5.6.7.8.9         "+required+" )";
        asd = parser.parse( value );
        Assert.assertEquals( "0.1.2.3.4.5.6.7.8.9", asd.getNumericOid() );

        // non-numeric not allowed
        value = "( test "+required+" )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NUMERICOID test" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // to short
        value = "( 1 "+required+" )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NUMERICOID 1" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // dot only
        value = "( . "+required+" )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NUMERICOID ." );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // ends with dot
        value = "( 1.1. "+required+" )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NUMERICOID 1.1." );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // quotes not allowed
        value = "( '1.1' "+required+" )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NUMERICOID '1.1' (quoted)" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // leading 0 not allowed
        value = "( 01.1 "+required+" )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NUMERICOID 01.1 (leading zero)" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // alpha not allowed
        value = "( 1.2.a.4 "+required+" )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NUMERICOID 1.2.a.4 (alpha not allowed)" );
        }
        catch ( ParseException pe )
        {
            Assert.assertTrue( true );
        }
        
    }
    
    /**
     * Tests NAME and its values
     * 
     * @throws ParseException
     */
    public static void testNames( AbstractSchemaParser parser, String oid, String required ) throws ParseException
    {
        String value = null;
        AbstractSchemaDescription asd = null;

        // alpha
        value = "( "+oid+" "+required+" NAME 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ' )";
        asd = parser.parse( value );
        Assert.assertEquals( 1, asd.getNames().size() );
        Assert.assertEquals( "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", asd.getNames().get( 0 ) );

        // alpha-num-hypen
        value = "( "+oid+" "+required+" NAME 'abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789' )";
        asd = parser.parse( value );
        Assert.assertEquals( 1, asd.getNames().size() );
        Assert.assertEquals( "abcdefghijklmnopqrstuvwxyz-ABCDEFGHIJKLMNOPQRSTUVWXYZ-0123456789", asd.getNames().get( 0 ) );

        // with parentheses
        value = "( "+oid+" "+required+" NAME ( 'a-z-0-9' ) )";
        asd = parser.parse( value );
        Assert.assertEquals( 1, asd.getNames().size() );
        Assert.assertEquals( "a-z-0-9", asd.getNames().get( 0 ) );

        // with parentheses, without space
        value = "( "+oid+" "+required+" NAME ('a-z-0-9') )";
        asd = parser.parse( value );
        Assert.assertEquals( 1, asd.getNames().size() );
        Assert.assertEquals( "a-z-0-9", asd.getNames().get( 0 ) );

        // multi with space
        value = "( "+oid+" "+required+" NAME ( 'test1' 'test2' ) )";
        asd = parser.parse( value );
        Assert.assertEquals( 2, asd.getNames().size() );
        Assert.assertEquals( "test1", asd.getNames().get( 0 ) );
        Assert.assertEquals( "test2", asd.getNames().get( 1 ) );

        // multi without space
        value = "( "+oid+" "+required+" NAME ('test1' 'test2' 'test3') )";
        asd = parser.parse( value );
        Assert.assertEquals( 3, asd.getNames().size() );
        Assert.assertEquals( "test1", asd.getNames().get( 0 ) );
        Assert.assertEquals( "test2", asd.getNames().get( 1 ) );
        Assert.assertEquals( "test3", asd.getNames().get( 2 ) );

        // multi with many spaces
        value = "(          "+oid+" "+required+"          NAME          (          'test1'          'test2'          'test3'          )          )";
        asd = parser.parse( value );
        Assert.assertEquals( 3, asd.getNames().size() );
        Assert.assertEquals( "test1", asd.getNames().get( 0 ) );
        Assert.assertEquals( "test2", asd.getNames().get( 1 ) );
        Assert.assertEquals( "test3", asd.getNames().get( 2 ) );

        // lowercase
        value = "( "+oid+" "+required+" name 'test' )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, NAME is lowercase" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // unquoted
        value = "( "+oid+" "+required+" NAME test )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NAME test (unquoted)" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // start with number
        value = "( "+oid+" "+required+" NAME '1test' )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NAME 1test (starts with number)" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // start with hypen
        value = "( "+oid+" "+required+" NAME '-test' )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NAME -test (starts with hypen)" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // invalid character
        value = "( "+oid+" "+required+" NAME 'te_st' )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NAME te_st (contains invalid character)" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // NAM unknown
        value = "( "+oid+" "+required+" NAM 'test' )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid token NAM" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // one valid, one invalid
        value = "( "+oid+" "+required+" NAME ( 'test' 'te_st' ) )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, invalid NAME te_st (contains invalid character)" );
        }
        catch ( ParseException pe )
        {
            // expected
        }

        // no space between values
        value = "( "+oid+" "+required+" NAME ( 'test1''test2' ) )";
        try
        {
            asd = parser.parse( value );
            Assert.fail( "Exception expected, invalid NAME values (no space between values)" );
        }
        catch ( ParseException pe )
        {
            Assert.assertTrue( true );
        }
    }
    
    
    /**
     * Tests DESC
     * 
     * @throws ParseException
     */
    public static void testDescription( AbstractSchemaParser parser, String oid, String required ) throws ParseException
    {
        String value = null;
        AbstractSchemaDescription asd = null;

        // simple
        value = "("+oid+" "+required+" DESC 'Descripton')";
        asd = parser.parse( value );
        Assert.assertEquals( "Descripton", asd.getDescription() );

        // unicode
        value = "( "+oid+" "+required+" DESC 'Descripton \u00E4\u00F6\u00FC\u00DF \u90E8\u9577' )";
        asd = parser.parse( value );
        Assert.assertEquals( "Descripton \u00E4\u00F6\u00FC\u00DF \u90E8\u9577", asd.getDescription() );
        
        // escaped characters
        value = "( "+oid+" "+required+" DESC 'test\\5Ctest' )";
        asd = parser.parse( value );
        Assert.assertEquals( "test\\test", asd.getDescription() );
        value = "( "+oid+" "+required+" DESC 'test\\5ctest' )";
        asd = parser.parse( value );
        Assert.assertEquals( "test\\test", asd.getDescription() );
        value = "( "+oid+" "+required+" DESC 'test\\27test' )";
        asd = parser.parse( value );
        Assert.assertEquals( "test'test", asd.getDescription() );
        value = "( "+oid+" "+required+" DESC '\\5C\\27\\5c' )";
        asd = parser.parse( value );
        Assert.assertEquals( "\\'\\", asd.getDescription() );
        
        // lowercase
        value = "( "+oid+" "+required+" desc 'Descripton' )";
        try
        {
            parser.parse( value );
            Assert.fail( "Exception expected, DESC is lowercase" );
        }
        catch ( ParseException pe )
        {
            Assert.assertTrue( true );
        }
        
    }
    
    
    /**
     * Test extensions.
     * 
     * @throws ParseException
     */
    public static void testExtensions( AbstractSchemaParser parser, String oid, String required ) throws ParseException
    {
        String value = null;
        AbstractSchemaDescription asd = null;

        // no extension
        value = "( "+oid+" "+required+" )";
        asd = parser.parse( value );
        Assert.assertEquals( 0, asd.getExtensions().size() );

        // single extension with one value
        value = "( "+oid+" "+required+" X-TEST 'test' )";
        asd = parser.parse( value );
        Assert.assertEquals( 1, asd.getExtensions().size() );
        Assert.assertNotNull( asd.getExtensions().get( "X-TEST" ) );
        Assert.assertEquals( 1, asd.getExtensions().get( "X-TEST" ).size() );
        Assert.assertEquals( "test", asd.getExtensions().get( "X-TEST" ).get( 0 ) );

        // single extension with multiple values
        value = "( "+oid+" "+required+" X-TEST-ABC ('test1' 'test \u00E4\u00F6\u00FC\u00DF'       'test \u90E8\u9577' ) )";
        asd = parser.parse( value );
        Assert.assertEquals( 1, asd.getExtensions().size() );
        Assert.assertNotNull( asd.getExtensions().get( "X-TEST-ABC" ) );
        Assert.assertEquals( 3, asd.getExtensions().get( "X-TEST-ABC" ).size() );
        Assert.assertEquals( "test1", asd.getExtensions().get( "X-TEST-ABC" ).get( 0 ) );
        Assert.assertEquals( "test \u00E4\u00F6\u00FC\u00DF", asd.getExtensions().get( "X-TEST-ABC" ).get( 1 ) );
        Assert.assertEquals( "test \u90E8\u9577", asd.getExtensions().get( "X-TEST-ABC" ).get( 2 ) );

        // multiple extensions
        value = "("+oid+" "+required+" X-TEST-a ('test1-1' 'test1-2') X-TEST-b ('test2-1' 'test2-2'))";
        asd = parser.parse( value );
        Assert.assertEquals( 2, asd.getExtensions().size() );
        Assert.assertNotNull( asd.getExtensions().get( "X-TEST-a" ) );
        Assert.assertEquals( 2, asd.getExtensions().get( "X-TEST-a" ).size() );
        Assert.assertEquals( "test1-1", asd.getExtensions().get( "X-TEST-a" ).get( 0 ) );
        Assert.assertEquals( "test1-2", asd.getExtensions().get( "X-TEST-a" ).get( 1 ) );
        Assert.assertNotNull( asd.getExtensions().get( "X-TEST-b" ) );
        Assert.assertEquals( 2, asd.getExtensions().get( "X-TEST-b" ).size() );
        Assert.assertEquals( "test2-1", asd.getExtensions().get( "X-TEST-b" ).get( 0 ) );
        Assert.assertEquals( "test2-2", asd.getExtensions().get( "X-TEST-b" ).get( 1 ) );

        // some more complicated
        value = "("+oid+" "+required+" X-_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ('\\5C\\27\\5c'))";
        asd = parser.parse( value );
        Assert.assertEquals( 1, asd.getExtensions().size() );
        Assert.assertNotNull( asd.getExtensions().get( "X-_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" ) );
        Assert.assertEquals( 1, asd.getExtensions().get( "X-_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" ).size() );
        Assert.assertEquals( "\\'\\", asd.getExtensions().get( "X-_-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" ).get( 0 ) );
        
        // invalid extension, no number allowed
        value = "( "+oid+" "+required+" X-TEST1 'test' )";
        try
        {
            asd = parser.parse( value );
            Assert.fail( "Exception expected, invalid extension X-TEST1 (no number allowed)" );
        }
        catch ( ParseException pe )
        {
            Assert.assertTrue( true );
        }

    }

    
    /**
     * Tests OBSOLETE
     * 
     * @throws ParseException
     */
    public static void testObsolete( AbstractSchemaParser parser, String oid, String required ) throws ParseException
    {
        String value = null;
        AbstractSchemaDescription asd = null;

        // not obsolete
        value = "( "+oid+" "+required+" )";
        asd = parser.parse( value );
        Assert.assertFalse( asd.isObsolete() );

        // not obsolete
        value = "( "+oid+" "+required+" NAME 'test' DESC 'Descripton' )";
        asd = parser.parse( value );
        Assert.assertFalse( asd.isObsolete() );
        
        // obsolete
        value = "("+oid+" "+required+" NAME 'test' DESC 'Descripton' OBSOLETE)";
        asd = parser.parse( value );
        Assert.assertTrue( asd.isObsolete() );

        // obsolete 
        value = "("+oid+" "+required+" OBSOLETE)";
        asd = parser.parse( value );
        Assert.assertTrue( asd.isObsolete() );

        // ivalid
        value = "("+oid+" "+required+" NAME 'test' DESC 'Descripton' OBSOLET )";
        try
        {
            asd = parser.parse( value );
            Assert.fail( "Exception expected, invalid OBSOLETE value" );
        }
        catch ( ParseException pe )
        {
            // expected
        }
        
        // trailing value not allowed
        value = "("+oid+" "+required+" NAME 'test' DESC 'Descripton' OBSOLETE 'true' )";
        try
        {
            asd = parser.parse( value );
            Assert.fail( "Exception expected, trailing value ('true') now allowed" );
        }
        catch ( ParseException pe )
        {
            Assert.assertTrue( true );
        }
        
    }
    
    
    
    /**
     * Tests for unique elements.
     * 
     * @throws ParseException
     */
    public static void testUnique( AbstractSchemaParser parser, String[] testValues )
    {
        for ( int i = 0; i < testValues.length; i++ )
        {
            String testValue = testValues[i];
            try
            {
                parser.parse( testValue );
                Assert.fail( "Exception expected, element appears twice in "+testValue );
            }
            catch ( ParseException pe )
            {
                Assert.assertTrue( true );
            }
        }
        
    }
    
    
    /**
     * Tests the multithreaded use of a single parser.
     */
    public static void testMultiThreaded( AbstractSchemaParser parser, String[] testValues )
    {
        final boolean[] isSuccessMultithreaded = new boolean[1];
        isSuccessMultithreaded[0] = true;
        
        // start up and track all threads (40 threads)
        List<Thread> threads = new ArrayList<Thread>();
        for ( int ii = 0; ii < 10; ii++ )
        {
            for ( int i = 0; i < testValues.length; i++ )
            {
                Thread t = new Thread( new ParseSpecification( parser, testValues[i], isSuccessMultithreaded ) );
                threads.add( t );
                t.start();
            }
        }

        // wait until all threads have died
        boolean hasLiveThreads = false;
        do
        {
            hasLiveThreads = false;

            for ( int ii = 0; ii < threads.size(); ii++ )
            {
                Thread t = threads.get( ii );
                hasLiveThreads = hasLiveThreads || t.isAlive();
            }
        }
        while ( hasLiveThreads );

        // check that no one thread failed to parse and generate a SS object
        Assert.assertTrue( isSuccessMultithreaded[0] );
        
    }

    static class ParseSpecification implements Runnable
    {
        private final AbstractSchemaParser parser;
        private final String value;
        private final boolean[] isSuccessMultithreaded;
        
        private AbstractSchemaDescription result;
        
        public ParseSpecification( AbstractSchemaParser parser, String value, boolean[] isSuccessMultithreaded )
        {
            this.parser = parser;
            this.value = value;
            this.isSuccessMultithreaded = isSuccessMultithreaded;
        }
        
        
        public void run()
        {
            try
            {
                result = parser.parse( value );
            }
            catch ( ParseException e )
            {
                e.printStackTrace();
            }
            
            isSuccessMultithreaded[0] = isSuccessMultithreaded[0] && ( result != null );
        }
    }
    
    
}