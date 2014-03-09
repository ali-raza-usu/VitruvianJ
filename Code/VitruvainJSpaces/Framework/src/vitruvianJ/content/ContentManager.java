package vitruvianJ.content;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.logging.JGUID;
import vitruvianJ.plugins.*;
import vitruvianJ.serialization.*;
import vitruvianJ.serialization.formatters.*;
import vitruvianJ.services.*;


	/// <summary>
	/// Service that abstracts persistence of objects.
	/// </summary>
    //@PluginPointAttribute(id= "ContentLoaders")// , _requireType = IContentLoader.class)
	public class ContentManager implements IService
	{
        private final char RESOURCE_DELIM = '|';

        private PluginPoint pluginPoint = new PluginPoint("ContentLoaders", IContentLoader.class);

        private List<IContentLoader> _loaders = new ArrayList<IContentLoader>();

        private PluginRegistry _pluginRegistry = null;

		/// <summary>
		/// Begin the service.
		/// </summary>        
        //@PluginPointAttribute(id= "ContentLoaders" , _requireType = IContentLoader.class)
		public void Init()
		{
            _pluginRegistry = (PluginRegistry) ServiceRegistry.getPreferredService(PluginRegistry.class);

            List<PluginDefination> definitions = _pluginRegistry.GetPluginDefinations(pluginPoint);

            for (int j = 0; j < definitions.size(); j++)
            {
                IContentLoader loader = (IContentLoader)definitions.get(j).GetPlugin();
                loader.Init();
                _loaders.add(loader);
            }
		}

		/// <summary>
		/// End the service.
		/// </summary>
		public void Cleanup()
		{
            for (int i = 0; i < _loaders.size(); i++)
                _loaders.get(i).Cleanup();
		}

        /// <summary>
        /// Load the resource with the given resource id.
        /// </summary>
        /// <param name="resource"></param>
        /// <returns></returns>
		public Object Load(String resource) throws Exception
		{
            String contentType = "";

            int index = resource.indexOf(RESOURCE_DELIM);
            if (index > 0)
            {
                // split the content type and resource path
                contentType = resource.substring(0, index);
                resource = resource.substring(index);
            }


			for (int i = 0; i < _loaders.size(); i++)
			{
                if (_loaders.get(i).CanLoad(contentType))
                {
                    return _loaders.get(i).Load(resource);
                }
			}

            throw new Exception(String.format("Unable to find an appropriate content loader for {0}", resource));
		}

        /// <summary>
        /// Load the resource with the given resource id.
        /// </summary>
        /// <param name="resource"></param>
        /// <returns></returns>
        public void Load(String resource, Object value)throws Exception
        {
            String contentType = "";

            int index = resource.indexOf(RESOURCE_DELIM);
            if (index > 0)
            {
                // split the content type and resource path
                contentType = resource.substring(0, index);
                resource = resource.substring(index + 1);
            }

            for (int i = 0; i < _loaders.size(); i++)
            {
                if (_loaders.get(i).CanLoad(contentType))
                {
                    _loaders.get(i).Load(resource, value);
                    return;
                }
            }

            throw new Exception(String.format("Missing Content Type {0} for resource {1}.", contentType, resource));
        }

        
        private JGUID _id = new JGUID();

        public JGUID getId()
        {
            return _id;
        }

        public String getName()
        {
            return "Content Manager"; 
        }

		
        
    }
