package vitruvianJ.collections;

import java.util.ArrayList;
import java.util.Iterator;

import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.eventargs.PropertyEventArgs;
import vitruvianJ.events.Event;
import vitruvianJ.events.IEventSubject;
import vitruvianJ.logging.JGUID;

public class ISmartList extends ArrayList implements IEventSubject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Event ItemsCleared = new Event(this);
    public Event ItemAdded = new Event(this);
    public Event ItemRemoved = new Event(this);
    private EventArgs args = new PropertyEventArgs();
	@Override
	public EventArgs getEventArgs() {
		// TODO Auto-generated method stub
		return args;
	}
	
	public boolean addItem(Object item)
	{
		if(super.add(item)){
			args = new PropertyEventArgs();
			args.addProperty("value", item);
			ItemAdded.RaiseEvent();
			return true;
		}else
			return false;
	}
	
	public boolean removeItem(Object item)
	{
		args = new PropertyEventArgs();
		args.addProperty("value", item);
		
		for (Object element : this) {
			if(item.toString().equals(element.toString())){
				this.remove(element);
				ItemRemoved.RaiseEvent();
				return true;
			}
		}		
			return false;
	}
}
