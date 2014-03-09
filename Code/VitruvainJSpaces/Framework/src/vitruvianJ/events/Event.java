package vitruvianJ.events;

import java.util.ArrayList;

import vitruvianJ.eventargs.EventArgs;

public class Event{

	protected ArrayList<Delegate> observers = new ArrayList<Delegate>();
	protected IEventSubject owner;
	
	public Event(IEventSubject owner)
	{
		this.owner = owner;
	}
	
	public ArrayList<Delegate> getInvocationList()
	{
		return observers;
	}
	public void addObservers(Delegate value)
	{
		observers.add(value);
	}
	
	public void removeObservers(Delegate value)
	{
		observers.remove(value);
	}
	
	public void RaiseEvent()
	{
		EventArgs args = owner.getEventArgs();
				
		for(int i = 0; i<observers.size(); i++)
		{
			Delegate observer = observers.get(i); 
			observer.invoke(args);
			//System.out.println(observer.invoke());
		}
	}
	
	public void RaiseReturnTypeEvent()
	{
		EventArgs args = owner.getEventArgs();
				
		for(int i = 0; i<observers.size(); i++)
		{
			ReturnDelegate observer = (ReturnDelegate)observers.get(i); 
			observer.invoke();
		}
	}
	
	public boolean CallEvent(ReturnDelegate observer)
	{	 
			return observer.invoke();	
	}
}