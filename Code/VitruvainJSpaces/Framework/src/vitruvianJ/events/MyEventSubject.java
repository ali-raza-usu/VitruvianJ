package vitruvianJ.events;

import java.util.Hashtable;
import java.util.Iterator;

import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.eventargs.PropertyEventArgs;



public class MyEventSubject implements IEventSubject
{
	private EventArgs args = new PropertyEventArgs();
	
	public MyEventSubject()
	{	
		EventRegistry.getInstance().addEvent("event1",new Event(this));
		EventRegistry.getInstance().addEvent("event2",new Event(this));
	}
	
	public EventArgs getEventArgs()
	{
		//EventArgs arg = new PropertyEventArgs();
		args.addProperty("fname","Ali");
		args.addProperty("lname","Raza");
		return args;
	}
		
		
}
