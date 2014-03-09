package vitruvianJ.distribution.encoders;

import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;

import vitruvianJ.content.ContentManager;
import vitruvianJ.services.ServiceRegistry;

public class FormatterMap<Type, Formatter> extends HashMap<Type, Formatter>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/// <summary>
    /// Default constructor.
    /// </summary>
    public FormatterMap()
    {
    }

	/// <summary>
	/// Default constructor.
	/// </summary>
	/// <param name="id">The location of the object.</param>
	public FormatterMap(String id) throws Exception
	{
        ContentManager content = (ContentManager)ServiceRegistry.getPreferredService(ContentManager.class);
        content.Load(id, this);
    }

	
}