package vitruvianJ.events;

public class EventObject {

	private String eventName  = "";
	private Event event;
	
	
	public EventObject(String name, Event event)
	{
		eventName = name;
		this.event = event; 
	}
	
	public String getName()
	{
		return eventName;
	}
	
	public Event getEvent()
	{
		return event;
	}
}
