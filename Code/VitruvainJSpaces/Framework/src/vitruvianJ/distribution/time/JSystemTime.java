package vitruvianJ.distribution.time;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import vitruvianJ.logging.JGUID;

public class JSystemTime extends GlobalTime
{
	public JSystemTime()
	{
		int a = 0;
	}
    /// <summary>
    /// Is the time valid?
    /// </summary>
    public boolean getIsTimeValid()
    {
        return true;
    }

    public static void main(String args[])
    {
    	System.out.println("Hi Ali Raza");
    }
    /// <summary>
    /// Get the UTCTime
    /// </summary>
    public Date getUtcTime()
    {
    	return new Date(UTC_Format()); 
    }
    
    String UTC_Format()
    {
    	SimpleDateFormat utc_formatter = null;
    	utc_formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return utc_formatter.format(new Date());
    }

	@Override
	public boolean isTimeValid() {
		// TODO Auto-generated method stub
		return false;
	}
	
    
}