package vitruvianJ.services;
import java.lang.reflect.Type;
import java.util.*;
import org.apache.log4j.xml.DOMConfigurator;
import vitruvianJ.core.PathUtilities;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;

import vitruvianJ.serialization.xml.XmlFramework;


public class ServiceRegistry
{

	public ServiceRegistry()
	{
		
	}
	
	
	private static JLogger _logger = new JLogger(new ServiceRegistry().getClass());

	private static Object _syncObject = new Object();

	private static List<IService> _services = new ArrayList<IService>();
	private static List<IService> _initializedServices = new ArrayList<IService>();

	
	/*
	public static List<IService> GetServices()
	{
		return _services; 
	}
*/
	public static IService getPreferredService(Type T)
	{
		Class type = (Class)T;//IService.class;
		synchronized (_initializedServices) {
			for (int i = 0; i < _initializedServices.size(); i++)
			{
				IService service = _initializedServices.get(i);
				Class service_type = service.getClass();
				if (type.isAssignableFrom(service_type))
					return service;	
			}
		}		
		return null;
	}
	
	
	
	/// <summary>
	/// Get the service with the specified id.
	/// </summary>
	/// <param name="id">The unique identifier of the service.</param>
	/// <returns>The service containing the unique id.</returns>
	public static  IService GetService(JGUID id)
	{
		synchronized (_syncObject) {		
			for (int i = 0; i < _initializedServices.size(); i++)
			{
				IService service = _initializedServices.get(i);
				if (service.getId().toString().equals(id.toString()))
					return service;
			}
		}

		return null;
	}

	/// <summary>
	/// Find the services of a known type
	/// </summary>
	/// <typeparam name="T">The type of service to locate.</typeparam>
	/// <returns>A list of services known to the application of the known type.</returns>
		
	public static List<? extends IService> GetServices(Class<? extends IService> T)
	{				  
		
		List <IService> result = new LinkedList<IService>();
		
				
		synchronized (_syncObject) 
		{
			for (int i = 0; i < _initializedServices.size(); i++)
			{
				
				IService service = _initializedServices.get(i);				
				if (T.isAssignableFrom(service.getClass()))
					result.add(service);	
			}	
		}
		return result;
	}
 	
	
	/// <summary>
	/// Initialize all of the services known to the application at this time.
	/// <remarks>This will resolve required dependencies of the services.</remarks>
	/// </summary>
	public static void Init()
	{
		//_logger.Debug("Initializing Service : ");
		synchronized (_syncObject) 			
		{
			for (int i = 0; i < _services.size(); i++)
			{			
				
//				if (_logger.IsDebugEnabled()){														
//					_logger.Debug("Initializing Service : ", _services.get(i).getName());
//				}
					
				_services.get(i).Init();
				_initializedServices.add(_services.get(i));
			}
		}
				
	}

	/// <summary>
	/// Cleanup all of the services known to the application at this time.
	/// </summary>
	public static void Cleanup()
	{
		synchronized (_syncObject)
		{
			for (int i = 0; i<_services.size();  i++)
			{
				if (_logger.IsInfoEnabled())
					_logger.InfoFormat("Cleaning up Service : %1s ", _services.get(i).getName());

				_services.get(i).Cleanup();
			}
		}
	}

	/// <summary>
	/// Add a new service to the registry.  The new service will
	/// be initialized before being added to the registry.
	/// A ServiceAdded event will be fired.
	/// </summary>
	/// <param name="service">The new service to add.</param>
	public static void Add(IService service)
	{
		
		service.Init();

		synchronized (_syncObject)
		{
			_services.add(service);
			_initializedServices.add(service);
		}
		
		//ServiceAdded.FireEvent(new ServiceEventArgs(service));
	}

	/// <summary>
	/// Swap the services.
	/// </summary>
	/// <param name="oldService">The old service to replace.</param>
	/// <param name="newService">The new service to use.</param>
	public static void Swap(IService oldService, IService newService)
	{
		newService.Init();

		synchronized (_syncObject)
		{
			int index0 = _services.indexOf(oldService);
			int index1 = _initializedServices.indexOf(oldService);

			if (index0 >= 0 && index1 >= 0)
			{
				_services.set(index0,newService);
				_initializedServices.set(index1, newService);

				//ServiceSwapped.FireEvent(new ServiceSwappedEventArgs(oldService, newService));
			}
		}

		oldService.Cleanup();
	}

	/// <summary>
	/// Remove a service from the registry.  The service will
	/// be cleanup up after being removed from the registry.
	/// A ServiceRemoved event will be fired.
	/// </summary>
	/// <param name="service">The service to remove.</param>
	public static void Remove(IService service)
	{
		synchronized (_syncObject)
		{
			_services.remove(service);
			_initializedServices.remove(service);
		}

		service.Cleanup();
		//ServiceRemoved.FireEvent(new ServiceEventArgs(service));
	}

	/// <summary>
	/// Determine if the service registry contains a service.
	/// </summary>
	/// <param name="service">The service to test containment on.</param>
	/// <returns>True if contained, otherwise False.</returns>
	public static boolean Contains(IService service)
	{
		synchronized (_syncObject)
		{
			return _initializedServices.contains(service);
		}
	}

	/// <summary>
	/// Determine if the service registry contains a service.
	/// </summary>
	/// <param name="id">The id of the service to look for.</param>
	/// <returns>True if contained, otherwise False.</returns>
	
	public static List<IService> getServices()
	{
		return _services; 
	}
	
	public static boolean Contains(JGUID id)
	{
		return (GetService(id) != null);
	}
	
	public static void Load()
    {
		_logger.Debug("Hello Ali");
		DOMConfigurator.configure(PathUtilities.GetAbsolutePath("VitruvianJ_CLogger.xml"));
        XmlFramework.Deserialize(PathUtilities.GetAbsolutePath("Services.xml"), ServiceRegistry.getServices());
        _logger.Debug("Service Registry : Load() ");
        
        //Call the other load method.
    }
	
	public static void Load(String path)
    {
	
		DOMConfigurator.configure(PathUtilities.GetAbsolutePath("VitruvianJ_CLogger.xml"));
		
        XmlFramework.Deserialize(PathUtilities.GetAbsolutePath(path), ServiceRegistry.getServices());
    }
		

	
	public static void Run()
    {
        // IRunnable.Run
        List<IRunnable> runnables = (List<IRunnable>) ServiceRegistry.GetServices(IRunnable.class);
        for (int i = 0; i < runnables.size(); i++)
            runnables.get(i).Run();
    }

	
}
