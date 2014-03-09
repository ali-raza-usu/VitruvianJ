package vitruvianJ.eventargs;

import java.util.Hashtable;

public interface EventArgs {	
	public void addProperty(String name, Object value);	
	public void removeProperty(String name);
	public Object getPropertyValue(String key);
	public Hashtable<String, Object> getProperties();
}
