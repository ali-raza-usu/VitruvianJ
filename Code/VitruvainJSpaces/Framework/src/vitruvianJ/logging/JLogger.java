package vitruvianJ.logging;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.Locale;
//import org.apache.commons.logging.impl.*;

import org.apache.log4j.*;


import vitruvianJ.core.PathUtilities;



public final class JLogger {

	
	private Logger _logger =null;
	private Class _type;
	
	
	public JLogger()
	{
		_logger = Logger.getLogger(JLogger.class);			
	}
	
	
	public Type getType()
	{
		return _type;
	}
		
	public JLogger(Class loggerType)
	{
		if (!_knownLoggerTypes.contains(loggerType))
			_knownLoggerTypes.add(loggerType);		
		_logger = Logger.getLogger(loggerType);		
		_type = loggerType;
	
	}

//	public JLogger getLogger(Class logger)
//	{
//		for(Type templogger : _knownLoggerTypes)
//		{
//			if(logger instanceof templogger)
//		}
//	}
	
	
	private static Collection<Type> _knownLoggerTypes = Collections.synchronizedCollection(new ArrayList<Type>());
	
	
	public static Collection<Type> GetKnownLoggerTypes(){
		
		return _knownLoggerTypes;
	}
	
	
	public void TestLogger()
	{
		 SimpleLayout layout = new SimpleLayout();		 
	      FileAppender appender = null;
	      try {
	         appender = new FileAppender(layout,PathUtilities.GetAbsolutePath("output1.txt"),false);
	      } catch(Exception e) {}

	    _logger.addAppender(appender);	 
	    _logger.setLevel((Level) Level.DEBUG);    
		
	}
	
	public Level getLevel()
	{
		return _logger.getLevel();
		
	}
	public boolean IsDebugEnabled()
	{
		return _logger.isDebugEnabled(); 
	}
	

	public boolean IsErrorEnabled()
	{
		return _logger.isEnabledFor(Level.ERROR); 
	}
	
	public boolean IsWarnEnabled()
	{
		return _logger.isEnabledFor(Level.WARN); 
	}
	
	public boolean IsInfoEnabled()
	{
		return _logger.isInfoEnabled();
	}
	
	public boolean IsTraceEnabled()
	{
		return _logger.isTraceEnabled();
		
	}
	
	public void Debug(String format, Object... args)
	{
		if (_logger.isDebugEnabled())
			_logger.debug(getString(format,args));		
	}

	public void Debug(Object message)
	{
		if (_logger.isDebugEnabled())
			_logger.debug(message);		
		//System.out.println(_logger.getName() + "" + message);
	}
	public void Debug(Object message, Exception exception)
	{
		if (_logger.isDebugEnabled())
			_logger.debug(message, exception);
		
	}
	
	String getString(String format, Object... args)
	{
		StringBuilder sb = new StringBuilder();
		 Formatter formatter = new Formatter(sb, Locale.US);
		 return formatter.format(format, args).toString();	
	}
	
	public void DebugFormat(String format, Object... args)
	{
		if (_logger.isDebugEnabled())
		{			 		
			_logger.debug(getString(format,args));
		}
	}

	
	
	
	public void Error(Object message)
	{
	
			if (_logger.isEnabledFor(Level.ERROR))
			_logger.error(message);
	}
	
	public void Error(Object message, Exception exception)
	{
	
			if (_logger.isEnabledFor(Level.ERROR))
			_logger.error(message, exception);
	}
	
	public void ErrorFormat(String format, Object... args)
	{
		if (_logger.isEnabledFor(Level.ERROR))
		{			
			_logger.error(getString(format,args));
		}
	}
	
	public void Fatal(Object message)
	{
		if (_logger.isEnabledFor(Level.FATAL))		
			_logger.fatal(message);
	}
	
	public void Fatal(Object message, Exception exception)
	{		
			if (_logger.isEnabledFor(Level.ERROR))
			_logger.fatal(message, exception);
	}
	
	public void Info(Object message)
	{
		if (_logger.isInfoEnabled())
			_logger.info(message);
	}
	
	public void InfoFormat(String format, Object... args)
	{
		if (_logger.isInfoEnabled())
		{									
			_logger.info(getString(format,args));			
		}
	}

	
	public void Info(Object message, Exception exception)
	{
		if (_logger.isInfoEnabled())
			_logger.info(message, exception);
	}
	
	public void Warn(Object message)
	{
		if (_logger.isEnabledFor(Level.WARN))
			_logger.warn(message);
	}
	
	public void Warn(Object message, Exception exception)
	{
		if (_logger.isEnabledFor(Level.WARN))
			_logger.warn(message, exception);
	}
	
	
	/// <summary>
	/// Add a Warn level log.
	/// </summary>
	/// <param name="format">A string containing String.Format parameters.</param>
	/// <param name="args">The arguments to the String.Format.</param>
	public void WarnFormat(String format, Object... args)
	{
		if (_logger.isEnabledFor(Level.WARN))
		{
			_logger.warn(getString(format,args));
		}
	}

	
	public void Mandatory(Level level, Object message) 
	{
		
		Logger logLevel = Logger.getRootLogger();
		Level oldLevel = _logger.getLevel();
		logLevel.setLevel(level);		
		if(level.toString().equals("DEBUG")){			
				Debug(message);				
		}
		else if (level.toString().equals("INFO")){
				Info(message);		
		}
		else  if (level.toString().equals("WARN")){
				Warn(message);				
		}
		else  if (level.toString().equals("ERROR")){					
				Error(message);		
		}
		else  if (level.toString().equals("FATAL")){			
				Fatal(message);
		}

		//logLevel.setLevel(oldLevel);		
	}


	public void ErrorFormat(Exception ex, String string) {
		// TODO Auto-generated method stub
		if (_logger.isEnabledFor(Level.ERROR))
		{
			String message = "";			
			_logger.error(message, ex);
		}
		
	}

}
