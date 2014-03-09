package vitruvianJ.events;

import java.util.Hashtable;

import javax.imageio.spi.ServiceRegistry;

import vitruvianJ.eventargs.EventArgs;


public class MyObserver1
{

	private m1Delegate m1;
	private m2Delegate m2;
	private m3Delegate m3;
	
	public MyObserver1()
	{
		// get a reference to the event of interest
		//  Choices:
		//	1.	Have the event or events pass to this class by an outside object
		//	2.	Have a registry of events and this instance can look them up
		// here we choice approach #1

		m1 = new m1Delegate();
		EventRegistry.getInstance().getEvent("event1").addObservers(m1);
		
		
		m2 = new m2Delegate();
		EventRegistry.getInstance().getEvent("event1").addObservers(m2);
		
		m3 = new m3Delegate(); 
		EventRegistry.getInstance().getEvent("event1").addObservers(m3);
	}
	
	
	
	class m3Delegate implements ReturnDelegate
	{
		public boolean method3()
		{
			System.out.println(" method 3 - return type");
			return true;
		}

		@Override
		public boolean invoke() {			
			return method3();
		}

		@Override
		public void invoke(EventArgs args) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class m1Delegate implements ReturnDelegate
	{
		public void method1(EventArgs args)
		{
			System.out.println("MyObserver1(Observer) : method1");
		}
		
		@Override
		public void invoke(EventArgs args) {
			method1(args);			
		}

		@Override
		public boolean invoke() {
			// TODO Auto-generated method stub
			return false;
		}		
	}
	
	class m2Delegate implements ReturnDelegate
	{
		public void method2(EventArgs args)
		{
			Hashtable<String, Object> properties = args.getProperties();
			String fname = (String)properties.get("fname");
			String lname = (String)properties.get("lname");
			System.out.println("MyObserver1 : method2 name = " + fname + " , " + lname );
		}
		@Override
		public void invoke(EventArgs args) {
			method2(args);			
		}
		@Override
		public boolean invoke() {
			// TODO Auto-generated method stub
			return false;
		}		
	}
	
	
}
