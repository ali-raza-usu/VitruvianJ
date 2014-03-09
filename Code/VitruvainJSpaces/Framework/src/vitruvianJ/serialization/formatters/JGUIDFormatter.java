package vitruvianJ.serialization.formatters;

import vitruvianJ.logging.JGUID;

public class JGUIDFormatter extends Formatter{
	
	public String Format(Object value)
	{
		return value.toString();
	}
	
	public Object Unformat(String value)
	{
		return new JGUID(value);
	}

}
