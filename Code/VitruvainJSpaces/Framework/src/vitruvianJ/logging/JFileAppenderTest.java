package vitruvianJ.logging;



import junit.framework.TestCase;

public class JFileAppenderTest extends TestCase {

	
	public void testGetLogPathName()
	{					
		assertEquals(true, true);
		System.out.println(JFileAppender.GetLogPathName());
	}
	
	public void testGetFile()
	{
		
		
		assertEquals(PathUtilities.ApplicationName+"\\Logs\\"+JFileAppender.GetLogPathName(), JFileAppender.getFileName());
			System.out.println(JFileAppender.getFileName());
		
	}
	
	public void testGetLayout() {
		
	}

	public void testGetLayoutLayout() {
		
	}

}
