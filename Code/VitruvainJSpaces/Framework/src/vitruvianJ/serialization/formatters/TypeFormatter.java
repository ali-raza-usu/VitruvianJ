package vitruvianJ.serialization.formatters;

import vitruvianJ.core.ClassFactory;

public class TypeFormatter extends Formatter {

	private boolean _strongTyped = false;
	
	public TypeFormatter()
	{
		
	}
	
	public TypeFormatter(boolean strongTyped)
	{
		_strongTyped = strongTyped;
	}
	
	public boolean getStrongTyped()
	{
		return _strongTyped;
	}
	
	public void setStrongTyped(boolean value)
	{
		_strongTyped = value;
	}
	
	
	@Override
	public String Format(Object value) {
		if(value == null)
			return "";
		String name = value.getClass().getName(); 
		return name;		
	}

	@Override
	public Object Unformat(String value) {
		try{
		if(value == null)
			return null;
		return ClassFactory.FindType(value);
		}catch(Exception e)
		{
			return null;
		}
	}

}
