package vitruvianJ.distribution.syncpatterns.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import vitruvianJ.eventargs.EventArgs;

public class PropertyChangedEventArgs implements EventArgs{

	private HashMap<String, Object> names = new HashMap<String, Object>();
	@Override
	public void addProperty(String name, Object value) {
		// TODO Auto-generated method stub
		names.put(name, value);
		
	}

	@Override
	public void removeProperty(String name) {
		// TODO Auto-generated method stub
		names.remove(name);
		
	}

	@Override
	public Object getPropertyValue(String key) {
		// TODO Auto-generated method stub
		return names.get(key);
	}

	@Override
	public Hashtable<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
