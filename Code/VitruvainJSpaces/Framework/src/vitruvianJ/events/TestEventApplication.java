package vitruvianJ.events;

public class TestEventApplication {
	
	public static void main(String args[])
	{
		System.out.println("Ali Raza");
		MyEventSubject class1 = new MyEventSubject();		
		MyObserver1 observer1 = new MyObserver1();
		Event event = EventRegistry.getInstance().getEvent("event1");
		
		event.RaiseEvent();
		event.RaiseReturnTypeEvent();
		System.out.println("==========================");
		
	
		
		event = EventRegistry.getInstance().getEvent("event2");
		event.RaiseEvent();		
	}

	
}
