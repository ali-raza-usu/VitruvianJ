package vitruvianJ.serialization.xml;

import java.lang.reflect.Type;
import java.util.HashMap;

/// <summary>
/// Utilities pertaining to serialization.
/// </summary>
public class SimpleXmlSerializationUtilities {
	
	 /// <summary>
    /// Serialization info based on type.
    /// </summary>
    private static HashMap<Type, SimpleXmlTypeSerializationInfo> _typeSerializationInfo = new HashMap<Type, SimpleXmlTypeSerializationInfo>();
	
    /// <summary>
    /// Get the serialization information available through reflection.
    /// This information is cached for subsequent use.
    /// </summary>
    /// <param name="type">The type to gather serialization information for.</param>
    /// <returns>The reflected serialization information.</returns>
	public static SimpleXmlTypeSerializationInfo getReflectionInfo(Type type) {
		synchronized (_typeSerializationInfo) // thread-safe
        {
            SimpleXmlTypeSerializationInfo result = null;

            if (_typeSerializationInfo.containsKey(type))
                result = _typeSerializationInfo.get(type);
            else
            {
                result = new SimpleXmlTypeSerializationInfo(type);
                _typeSerializationInfo.put(type, result);
            }

            return result;
        }
	}

}
