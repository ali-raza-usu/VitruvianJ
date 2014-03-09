package vitruvianJ.services;

import java.util.ArrayList;
import java.util.List;
import vitruvianJ.core.ClassFactory;
import vitruvianJ.logging.JLogger;
import vitruvianJ.plugins.*;
import vitruvianJ.serialization.xml.XmlFramework;

//public class PluginService extends BaseService
//{
//	private static JLogger _logger = new JLogger(PluginService.class);
//
//	private final String PLUGIN_EXT = ".xml";
//
//	private String _pluginDir = "";
//	private List<PluginDefination> _pluginDefinitions = new ArrayList<PluginDefination>();
//
//    public PluginService()
//    {
//        super("Plugin Service");
//    }
//
//	/// <summary>
//	/// Initialize this service by loading plugin assemblies and plugin definitions.
//	/// </summary>
//	public void Init()
//	{
////		if (!Directory.Exists(_pluginDir))
////			return;
//
//		// recursively pre-load all dlls
//		LoadAssemblies(_pluginDir);
//
//		// recursively load plugin definitions
//		LoadPluginDefinitions(_pluginDir);
//	}
//
//	/// <summary>
//	/// The directory where plugin definitions and plugin assemblies reside.
//	/// </summary>
//	//[AppPathFormatter]
//    //[Serialize]
//	public String getPluginDirectory()
//	{
//		return _pluginDir; 
//	}
//	
//	public void setPluginDirectory(String pluginDir)
//	{
//		_pluginDir = pluginDir; 		
//	}
//
//	/// <summary>
//	/// Recursively load all of the assemblies in the given directory.  The assemblies
//	/// will be loaded into the ClassFactory.
//	/// </summary>
//	/// <param name="directory">The directory to recursively load assemblies from.</param>
////	static private void LoadAssemblies(String directory)
////	{
////		String[] files = Directory.GetFiles(directory, "*.dll", SearchOption.AllDirectories);
////		for (int i = 0; i < files.Length; i++)
////		{
////			String assemblyName = Path.GetFileNameWithoutExtension(files[i]);
////			if (!ClassFactory.IsAssemblyLoaded(assemblyName))
////			{
////				try
////				{
////					Assembly assembly = Assembly.LoadFrom(files[i]);
////					ClassFactory.AddAssembly(assembly);
////				}
////				catch (Exception ex)
////				{
////					if (_logger.IsErrorEnabled)
////						_logger.ErrorFormat(ex, "Error loading assembly {0}.", files[i]);
////				}
////			}
//		}
//	}
//
//	/// <summary>
//	/// Recursively load all of the plugin definitions in the given directory.
//	/// </summary>
//	/// <param name="directory">The directory to recursively load plugin definitions from.</param>
//	private void LoadPluginDefinitions(String directory)
//	{
//		String[] files = Directory.GetFiles(directory, String.Format("*{0}", PLUGIN_EXT), SearchOption.AllDirectories);
//		for (int i = 0; i < files.Length; i++)
//		{
//			List<PluginDefination> localDefinitions = new ArrayList<PluginDefination>();
//
//			try
//			{
//				XmlFramework.Deserialize(files[i], localDefinitions);
//			}
//			catch (Exception ex)
//			{
//				_logger.ErrorFormat(ex, "Unable to load plugin definition file : {0}.", files[i]);
//				continue;
//			}
//
//			foreach (PluginDefination definition in localDefinitions)
//			{
//				definition.Filename = files[i];
//				_pluginDefinitions.Add(definition);
//
//				if (_logger.IsDebugEnabled)
//					_logger.DebugFormat("Plugin-Definition : {0}", definition.Name);
//			}
//		}
//	}
//
//	/// <summary>
//	/// Get the plugin definitions for the specified plugin point.  Only plugin definitions
//	/// that will create plugins that appropriately match the required type of the plugin
//	/// point will be returned.
//	/// </summary>
//	/// <param name="point">The plugin point to load plugins for.</param>
//	/// <returns>A list of plugin definitions that can be used to load plugins for the plugin point.</returns>
//	public List<PluginDefination> GetPluginDefinitions(PluginPoint point)
//	{
//		List<PluginDefination> result = new ArrayList<PluginDefination>();
//
//		foreach (PluginDefination definition in _pluginDefinitions)
//		{
//			if (!definition.PluginPoints.Contains(point.Id))
//				continue;
//
//			if (point.RequiredType.IsAssignableFrom(definition.PluginType))
//				result.Add(definition);
//		}
//
//		return result;
//	}
//
//    /// <summary>
//    /// Get the plugins for the specified plugin point.  Only plugin definitions
//    /// that will create plugins that appropriately match the required type of the plugin
//    /// point will be returned.
//    /// </summary>
//    /// <param name="point">The plugin point to load plugins for.</param>
//    /// <returns>A list of plugin definitions that can be used to load plugins for the plugin point.</returns>
//    public List<T> GetPlugins<T>(PluginPoint point)
//    {
//        List<T> result = new List<T>();
//
//        foreach (PluginDefination definition in _pluginDefinitions)
//        {
//            if (!definition.PluginPoints.Contains(point.Id))
//                continue;
//
//            if (point.RequiredType.IsAssignableFrom(definition.PluginType))
//            {
//                try
//                {
//                    T plugin = (T) definition.GetPlugin();
//                    result.Add(plugin);
//                }
//                catch (Exception ex)
//                {
//                    if (_logger.IsErrorEnabled)
//                        _logger.Error(ex);
//                }
//            }
//        }
//
//        return result;
//    }
//}