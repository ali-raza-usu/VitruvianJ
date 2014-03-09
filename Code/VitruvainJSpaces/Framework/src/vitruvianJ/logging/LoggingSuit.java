package vitruvianJ.logging;


import junit.framework.Test;
import junit.framework.TestSuite;

public class LoggingSuit {

	public static void Main(String args[])
	{
		LoggingSuit.suite();
	}
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for logging");
		//$JUnit-BEGIN$
		suite.addTestSuite(JFileAppenderTest.class);
		suite.addTestSuite(PathUtilitiesTest.class);
		suite.addTestSuite(JGUIDTest.class);		
		suite.addTestSuite(JLoggerTest.class);
		//$JUnit-END$
		return suite;
	}

}
