package vitruvianJ.events;

import java.util.ArrayList;
import java.util.List;

public class EventRegistry {

	private List<EventObject> events = new ArrayList<EventObject>();
	
	private static EventRegistry instance = null;
	
	public static EventRegistry getInstance()
	{
		if(instance == null)
		{
			instance = new EventRegistry();			
		}
		
		return instance;
	}
	
	public List<EventObject> getEvents()
	{
		return events;
	}
	
	public void addEvent(String name, Event value)
	{
		
		events.add(new EventObject(name, value));	
	}
	
	public void removeEvent(EventObject value)
	{
		events.remove(value);
	}
	
	public Event getEvent(String name)
	{
		for (EventObject object : events) {
			if(object.getName().equals(name))
				return object.getEvent();
		}
		return null;
	}
	
}
