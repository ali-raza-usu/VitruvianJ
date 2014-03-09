package vitruvianJ.collections;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.logging.JLogger;



public class SingletonSession {
	
	private static JLogger _logger = new JLogger(SingletonSession.class);

	private static Hashtable<Method, ISyncPattern> _instance = null;
	
	public static Object getInstance(Hashtable<Method, ISyncPattern> p_object)
	{
		if(_instance != null)
			return _instance;
		else{
			_instance = p_object;
			print();
			return _instance;
		}
	
	}
	
	
	private static void print()
	{
		for (Entry<Method, ISyncPattern> element : _instance.entrySet()) {
			_logger.Debug("Method : Pattern " + element.getKey().getName() + " : " + element.getValue().toString());
			
		}
		
	}
}
