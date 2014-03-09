package vitruvianJ.logging;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.*;

import vitruvianJ.core.PathUtilities;






public class JFileAppender extends FileAppender {

	public JFileAppender()
	{			
		String folder = PathUtilities.ApplicationName + "\\Logs\\";		
		fileName = folder.substring(6) + GetLogPathName();		
		try{		
		this.fileName = getFileName();		
		this.setAppend(true);		
		this.setLayout(getLayout());
		//JLogger.instance().addAppender(this);			
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}			
	}
	
	public JFileAppender(Layout layout, String name) throws Exception
	{
		super(layout, name);	
	}
	
	
	
	public Layout getLayout()
	{
		return getLayout(new SimpleLayout());//.getLayout());
		
	}
	public static String getFileName()
	{
		String folder = PathUtilities.ApplicationName + "\\Logs\\";		
		String fileName = folder + JFileAppender.GetLogPathName();		
		try{
														
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return fileName;
	}
	
	public static Layout getLayout(Layout layout)
	{
			
		String header = null;
		String footer = null;
		if(layout != null)
		{
				if(layout.getHeader() == null)
				{
					
					header = "%d %-5p [%C{1}]: %m%n"; 
									
				}			
				
				if(layout.getFooter() == null)
				{
					footer = "";				
				}
				 
				
		}
		
		return new PatternLayout(header + footer);		
	}
	
	public static String GetLogPathName()
	{
		String path = "";		
		String packageName = JFileAppender.class.getPackage().getName();
		path = path + packageName;
		
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		String dateString  = dateFormat.format(date);
		path+= "[("+dateString+")";
		DateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
		String timeString  = timeFormat.format(date);
		path+= "("+timeString+")].txt";
		return path;
		
	}
}
