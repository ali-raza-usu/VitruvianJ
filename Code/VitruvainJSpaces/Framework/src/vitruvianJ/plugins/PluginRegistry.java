package vitruvianJ.plugins;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.logging.*;
import vitruvianJ.services.*;


	/// <summary>
	/// A class that provides plugin capabilities.
	/// </summary>
	abstract public class PluginRegistry implements IService
	{
        private static JLogger _logger = new JLogger(PluginRegistry.class);

		protected ArrayList<PluginDefination> _pluginDefinitions = new ArrayList<PluginDefination>();

		/// <summary>
		/// Load plugin assemblies and plugin definitions.
		/// </summary>
		abstract public void Load();

		/// <summary>
		/// Get the plugin definitions for the specified plugin point.  Only plugin definitions
		/// that will create plugins that appropriately match the required type of the plugin
		/// point will be returned.
		/// </summary>
		/// <param name="point">The plugin point to load plugins for.</param>
		/// <returns>A list of plugin definitions that can be used to load plugins for the plugin point.</returns>
		public List<PluginDefination> GetPluginDefinations(PluginPoint point)
		{
			List<PluginDefination> result = new ArrayList<PluginDefination>();

			for (PluginDefination definition : _pluginDefinitions)
			{
				if (!definition.getPluginPoints().contains(point.getId()))
					continue;

				if (point.getRequiredType().getClass().isAssignableFrom(definition.getPluginType().getClass()))
					result.add(definition);
			}

			return result;
		}

        /// <summary>
        /// Get the plugins for the specified plugin point.  Only plugin definitions
        /// that will create plugins that appropriately match the required type of the plugin
        /// point will be returned.
        /// </summary>
        /// <param name="point">The plugin point to load plugins for.</param>
        /// <returns>A list of plugin definitions that can be used to load plugins for the plugin point.</returns>
        public List GetPlugins(PluginPoint point)
        {
            List result = new ArrayList();

            for (PluginDefination definition : _pluginDefinitions)
            {
                if (!definition.getPluginPoints().contains(point.getId()))
                    continue;

                if (point.getRequiredType().getClass().isAssignableFrom(definition.getPluginType().getClass()))
                {
                    try
                    {
                        Object plugin = definition.GetPlugin();
                        result.add(plugin);
                    }
                    catch (Exception ex)
                    {
                    	/*
                        if (_logger.isErrorEnabled)
                            _logger.Error(ex);
                            */
                    }
                }
            }

            return result;
        }

   
        private JGUID _id = new JGUID();

        public JGUID getId()
        {
            return _id;
        }

        public String getName()
        {
            return "Plugin Registry"; 
        }

        public void Init()
        {
            Load();
        }

        public void Cleanup()
        {
        }

       
}
