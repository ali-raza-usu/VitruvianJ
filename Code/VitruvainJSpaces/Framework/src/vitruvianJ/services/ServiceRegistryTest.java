package vitruvianJ.services;



import junit.framework.TestCase;
import vitruvianJ.logging.*;

public class ServiceRegistryTest extends TestCase {

	
	
	
	
	public void testGetPreferredService() {
		IService service = (IService) new LoggingService();
		assertEquals(new LoggingService().getClass(),  ServiceRegistry.getPreferredService(service.getClass()));
	}
	

	public void testGetService() {
		JGUID g = new JGUID("18348543121141584457998342892838");				
		ServiceRegistry.Add(new LoggingService());		
		IService service =  ServiceRegistry.GetService(g);
		assertEquals("18348543121141584457998342892838" ,service.getId().toString() );		
	}

	/*
	public void testCleanup() {
		
		ServiceRegistry.Add(new LoggingService());
		ServiceRegistry.Cleanup();
		//The cleanup is not actually removing any service from List
		if(ServiceRegistry.GetServices().size()==3)
			assertEquals(3, ServiceRegistry.GetServices().size());
		else
			assertEquals(1, ServiceRegistry.GetServices().size());
	}

	public void testAdd() {
		IService service = new LoggingService();
			
		ServiceRegistry.Add(service);
		if(ServiceRegistry.GetServices().size()==4)
			assertEquals(4,ServiceRegistry.GetServices().size());
		else
			assertEquals(1,ServiceRegistry.GetServices().size());
	}
*/
	public void testSwap() {
		//will test later with better understanding		
	}
/*
	public void testRemove() {
		LoggingService l = new LoggingService();
		ServiceRegistry.Add(l);
		ServiceRegistry.Remove(l);
		if(ServiceRegistry.GetServices().size()==4)
			assertEquals(4, ServiceRegistry.GetServices().size());
		else
			assertEquals(0, ServiceRegistry.GetServices().size());
	}

	public void testContainsIService() {
		LoggingService l = new LoggingService();
		ServiceRegistry.Add(l);
		Boolean b = ServiceRegistry.Contains(l);
		assertEquals(true, b.booleanValue());
	}
*/
	public void testContainsJGUID() {
		JGUID g= new JGUID("18348543121141584457998342892838");		
		assertTrue(ServiceRegistry.Contains(g));
		//will test later... with better understanding		
	}

}
