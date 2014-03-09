package vitruvianJ.events;

import vitruvianJ.eventargs.EventArgs;

public interface Delegate {
	
	public void invoke(EventArgs args);	
}
