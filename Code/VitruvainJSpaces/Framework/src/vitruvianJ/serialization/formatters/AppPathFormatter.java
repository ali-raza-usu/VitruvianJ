package vitruvianJ.serialization.formatters;

import vitruvianJ.core.PathUtilities;

public class AppPathFormatter extends Formatter{

	public String Format(Object value)
	{
		return PathUtilities.GetRelativePath(value.toString());
	}
	
	public Object Unformat(String value)
	{
		return PathUtilities.GetAbsolutePath(value); 
	}
}
