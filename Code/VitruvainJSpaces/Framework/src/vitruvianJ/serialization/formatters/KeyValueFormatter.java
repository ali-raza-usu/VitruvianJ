package vitruvianJ.serialization.formatters;

import java.lang.reflect.Type;

import vitruvianJ.core.ClassFactory;

public class KeyValueFormatter extends Formatter {

	private Formatter _keyFormatter = null;
	private Formatter _valueFormatter = null;
	
	public KeyValueFormatter(Type keyFormatterType, Type valueFormatterType)
	{
		try{
			_keyFormatter = (Formatter)ClassFactory.CreateObject(keyFormatterType);
			_valueFormatter = (Formatter)ClassFactory.CreateObject(valueFormatterType);
		}catch(Exception e)
		{}
	}
	
	public Formatter getKeyFormatter()
	{
		return _keyFormatter;
	}
	
	public void setKeyFormatter(Formatter value)
	{
		_keyFormatter = value;
	}
	
	public Formatter getValueFormatter()
	{
		return _valueFormatter;
	}
	
	public void setValueFormatter(Formatter value)
	{
		_valueFormatter = value;
	}
		
	
	@Override
	public String Format(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object Unformat(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
