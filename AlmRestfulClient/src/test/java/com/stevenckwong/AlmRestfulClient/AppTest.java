package com.stevenckwong.AlmRestfulClient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	App app = App.getInstance();
    	
    	// Testing the default constructor that doesn't provide any values
    	assertEquals("http://localhost:8080/qcbin/api", app.getAlmAPIURL());
    	app = null;
    	
    	// Testing the url read through the System properties
    	System.setProperty("almProtocol", "https");
    	System.setProperty("almHost", "testhost.com");
    	System.setProperty("almPort", "9090");
    	app = App.getInstance();
    	assertEquals("https://testhost.com:9090/qcbin/api", app.getAlmAPIURL());
    	app = null;
    	
    	// Test the constructor with parameters.
    	
    	app = App.getInstance("testhost2","1010","https");
    	assertEquals("https://testhost2:1010/qcbin/api", app.getAlmAPIURL());
    	app = null;
    	
    	// Test the signin capabilities
    	// app = new App("localhost","8080","http") ;
    	// app.setAlmParameters("DEFAULT", "MyDemo", "steven", "HPS0ftw@re!");
    	// app.signIn();
    	// app.getEntityById("test", "5");
    	// app.attachFileToEntity("test", "5", "myfile.zip", "");
    	// app.signOut();
    }
    
}
