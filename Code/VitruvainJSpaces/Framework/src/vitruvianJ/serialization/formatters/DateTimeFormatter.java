package vitruvianJ.serialization.formatters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFormatter extends Formatter {

	@Override
	public String Format(Object value) {
		try{
		DateFormat dateForamt = new SimpleDateFormat();
		Date date = dateForamt.parse(value.toString());
			return date.toString();
		}catch(Exception e){
			return value.toString();
		} 
				
	}

	@Override
	public Object Unformat(String value) {
		try{
		DateFormat dateForamt = new SimpleDateFormat();
		Date date = dateForamt.parse(value.toString());	
		return date;
		}catch(Exception e)
		{
			return value;
		}
	}

}
