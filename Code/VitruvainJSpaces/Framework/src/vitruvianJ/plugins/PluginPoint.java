package vitruvianJ.plugins;

import java.lang.reflect.Type;




	/// <summary>
	/// The application can request plugins from the plugin registry
	/// based on on a plugin point.  The plugin point is used to guarantee
	/// the type of the plugin created.  The id uniquely identifies the
	/// plugin point.  A plugin uses this id to be loaded into the application.
	/// </summary>
	public class PluginPoint
	{
		private String _id = "";
		private Type _requiredType = null;

		/// <summary>
		/// Default constructor
		/// </summary>
		public PluginPoint()
		{
		}

		/// <summary>
		/// Construct a plugin point.
		/// </summary>
		/// <param name="id">A unique identifier for this plugin point.</param>
		/// <param name="requiredType">The plugins that want to use this plugin point must be of the required type.</param>
		public PluginPoint(String id, Type requiredType)
		{
			_id = id;
			_requiredType = requiredType;
		}

		/// <summary>
		/// A unique identifier for this plugin point.
		/// A plugin uses this identifier to be loaded into the application.
		/// </summary>
		public String getId()
		{
			return _id;
		}
		
		public void setId(String value)
		{
			_id = value; 
		}

		/// <summary>
		/// Plugins that use this plugin point must be of this type.
		/// </summary>
		public Type getRequiredType()
		{
			return _requiredType; 
		}
		
		public void setRequiredType(Type value)
		{
			_requiredType = value; 
		}
	}
