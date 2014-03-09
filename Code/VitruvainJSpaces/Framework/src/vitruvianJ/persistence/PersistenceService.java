package vitruvianJ.persistence;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.services.BaseService;
import vitruvianJ.serialization.Serialize;

public class PersistenceService extends BaseService
{
	private final String PLUGIN_POINT_ID = "Adapters";

	private IAdapter _defaultAdapter = null;

	private List<IAdapter> _adapters = new ArrayList<IAdapter>();

    public PersistenceService()
    {
        super("Persistence Service");
    }

    public PersistenceService(IAdapter defaultAdapter)        
    {
    	super("Persistence Service");
        _defaultAdapter = defaultAdapter;
    }

	/// <summary>
	/// The default adapter.
	/// </summary>
    @Serialize//(getName = "property")
	public IAdapter getDefaultAdapter()
	{
		return _defaultAdapter; 
	}
	public void	setDefaultAdapter(IAdapter adapter) 
	{ 
		_defaultAdapter = adapter; 
	}

	

	/// <summary>
	/// Begin the service.
	/// </summary>
	public void Init()
	{
//		if (_defaultAdapter == null)
//			throw new Exception("Default Adapter cannot be null.");

		_defaultAdapter.Init();

		List<PluginService> services = null;//ServiceRegistry.GetServices<PluginService>();

//		for (int i = 0; i < services.Count; i++)
//		{
//			PluginService pluginService = services[i];
//			PluginPoint pluginPoint = new PluginPoint(PLUGIN_POINT_ID, typeof (IAdapter));
//			List<PluginDefinition> definitions = pluginService.GetPluginDefinitions(pluginPoint);
//
//			for (int j = 0; j < definitions.Count; j++)
//			{
//				IAdapter adapter = (IAdapter) definitions[j].GetPlugin();
//				_adapters.Add(adapter);
//				adapter.Init();
//			}
//		}
	}

	/// <summary>
	/// End the service.
	/// </summary>
//	@override 
//	public void Cleanup()
//	{
//		_defaultAdapter.Cleanup();
//
//		for (int i = 0; i < _adapters.Count; i++)
//			_adapters[i].Cleanup();
//	}
//
//	#endregion

	/// <summary>
	/// Get an adapter for the requesting object.
	/// The adapter can serialize / deserialize objects.
	/// </summary>
	/// <param name="value">The object to get an adapter for.</param>
	/// <returns>The adapter for this type if one is specified, otherwise the default adapter.</returns>
//	public IAdapter GetAdapter(object value)
//	{
//		for (int i = 0; i < _adapters.Count; i++)
//		{
//			if (_adapters[i].IsAdapter(value))
//				return _adapters[i];
//		}
//
//		return _defaultAdapter;
//	}
}
