package vitruvianJ.communication.channels;

import java.util.*;

import vitruvianJ.content.ContentManager;
import vitruvianJ.services.ServiceRegistry;
	/// <summary>
	/// A persistent list of channel providers.
	/// </summary>
	public class ChannelProviderList extends ArrayList<IChannelProvider>
	{
		/// <summary>
		/// Default constructor.
		/// </summary>
		/// <param name="id">The location of the object.</param>
		public ChannelProviderList(){}
		
		public ChannelProviderList(String resource)
		{
            ContentManager manager = (ContentManager)ServiceRegistry.getServices();
            try {
				manager.Load(resource, this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}

