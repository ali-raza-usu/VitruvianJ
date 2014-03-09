package vitruvianJ.plugins;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.core.PathUtilities;
import vitruvianJ.serialization.*;
import vitruvianJ.core.ClassFactory;
import vitruvianJ.serialization.xml.*;

public class PluginDefination
{
	private String _name = "";
	private List<String> _pluginPoints = new ArrayList<String>();
	private Type _pluginType = null;
	private boolean _isSingleton = false;
	private Object _singletonPlugin = null;
	private String _configuration = "";
	
	private String _filename = "";
	
	/// <summary>
	/// The filename where the plugin definition comes from.
	/// </summary>
	public String getFilename()
	{
		return _filename; 
	}
	
	public void setFilename(String filename)
	{
		_filename = filename; 
	}

	/// <summary>
	/// The name of the plugin.
	/// </summary>
	@Serialize//(getName = "property")
	public String getName()
	{
		return _name; 
	}
	
	public void setName(String name)
	{
		_name = name;
	}

	/// <summary>
	/// The plugin points that the plugin wants to be loaded into.
	/// </summary>
	@Serialize//(getName = "property")
    public List<String> getPluginPoints()
	{
		return _pluginPoints; 
	}
	
	public void setPluginPoints(List<String> pluginPoints)
	{
		_pluginPoints = pluginPoints; 
	}

	
	/// <summary>
	/// Flag that indicates how the plugin should be loaded.  A singleton
	/// plugin will only be created once, but possibly used multiple times.
	/// A non-singleton plugin will be recreated on every request.
	/// </summary>
	@Serialize//(getName = "property")
    public boolean getIsSingleton()
	{
		return _isSingleton; 
	}
	
	public void setIsSingleton(boolean isSingleton)
	{
		_isSingleton = isSingleton; 
	}

	/// <summary>
	/// The type of object to create when the plugin is loaded.
	/// </summary>
	//[TypeFormatter]
    //[Serialize]
    public Type getPluginType()
	{
		return _pluginType; 
	}
    
    public void setPluginType(Type pluginType)
    {
		_pluginType = pluginType; 
	}

	/// <summary>
	/// The xml used to configure the plugin once it is created.
	/// </summary>
    //[Serialize]
    public String getConfiguration()
	{
		return _configuration; 
	}
    
    public void setConfiguration(String configuration)
    {
		_configuration = configuration; 
	}

	/// <summary>
	/// Create the plugin, configure it, and return it.
	/// A singleton plugin will only be created and configured once, but
	/// possibly returned multiple times.  A non-singleton plugin will
	/// be created, configured, and returned on every request.
	/// </summary>
	/// <returns>The plugin object.</returns>
	public Object GetPlugin()
	{
		if (_isSingleton)
		{
			if (_singletonPlugin == null)
			{
				try {
					_singletonPlugin = ClassFactory.CreateObject(_pluginType);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                if (_configuration != null && !_configuration.equals(""))
                {
                	PathUtilities.PushFilename(_filename);
                	try
                	{
                        XmlFramework.DeserializeXml(_configuration, _singletonPlugin);
                	}
                    catch(Exception e)
                    {
                        _singletonPlugin = null;
                    }
                	finally
                	{
                		PathUtilities.PopFilename(_filename);
                	}
                }
			}
			return _singletonPlugin;
		}
		else
		{
			Object plugin = null;
			try {
				plugin = ClassFactory.CreateObject(_pluginType);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            if (_configuration != null && !_configuration.equals(""))
            {
            	PathUtilities.PushFilename(_filename);
                try
                {
                    XmlFramework.DeserializeXml(_configuration, plugin);
                }
                catch(Exception e)
                {
                    plugin = null;
                }
            	finally
            	{
                	PathUtilities.PopFilename(_filename);
            	}
            }
            return plugin;
		}
	}

    public String ToString()
    {
        return _name;
    }
}