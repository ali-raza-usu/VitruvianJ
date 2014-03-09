package vitruvianJ.serialization.formatters;

import vitruvianJ.logging.GUID;

public class GuidFormatter extends Formatter{
	
	public String Format(Object value)
	{
		return value.toString();
	}
	
	public Object Unformat(String value)
	{
		return new GUID(value);
	}

}
