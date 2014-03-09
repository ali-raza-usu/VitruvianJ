package vitruvianJ.logging;

import junit.framework.TestCase;
import org.apache.log4j.*;


public class JLoggerTest extends TestCase {

	JLogger logger;
	protected void setUp() throws Exception {
		super.setUp();
		logger = new JLogger();
	
	}

	void print(String msg)
	{
		System.out.println(msg);		
	}
	protected void tearDown() throws Exception {
		super.tearDown();
	}
		
	
	
	 
	public void testDebugEnabled()
	{		
		Object message = "Ali Raza";
		logger.TestLogger();
		logger.Debug(message);
		assertEquals(logger.IsDebugEnabled(),true);
		//assertEquals((String)message,"Ali Raza");
	}
	
	public void TestMandatory()
	{
		Object message = "Ali Raza";
		logger.TestLogger();
		logger.Mandatory(logger.getLevel(), message);
		assertEquals(logger.getLevel(), Level.DEBUG);
		//assertEquals(logger.IsInfoEnabled(), true);
	}
	
	public void testBehavior1()
	{
		assertEquals(true,true);
		
	}
}
