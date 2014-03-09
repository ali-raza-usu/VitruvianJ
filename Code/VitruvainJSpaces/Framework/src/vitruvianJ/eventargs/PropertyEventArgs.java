package vitruvianJ.eventargs;

import java.util.Hashtable;

public class PropertyEventArgs implements EventArgs{

	private Hashtable<String,Object> properties = new Hashtable<String, Object>(); 
	@Override
	public void addProperty(String name, Object value) {		
		properties.put(name, value);		
	}

	@Override
	public void removeProperty(String name) {
		properties.remove(name);
	}


	@Override
	public Hashtable<String, Object> getProperties() {		
		return properties;
	}

	@Override
	public Object getPropertyValue(String key) {
		return properties.get(key);

	}

	
	
	

}
