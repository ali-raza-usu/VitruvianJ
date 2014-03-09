package vitruvianJ.serialization.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vitruvianJ.serialization.SerializationUtilities;


/// <summary>
/// Serialization info based on type.
/// </summary>
public class SimpleXmlTypeSerializationInfo {

	private HashMap<String, SimpleXmlPropertySerializationInfo> _propertyMap = new HashMap<String, SimpleXmlPropertySerializationInfo>();
	private HashMap<String, SimpleXmlFieldSerializationInfo> _fieldMap = new HashMap<String, SimpleXmlFieldSerializationInfo>();
	
    /// <summary>
    /// The type this information is about.
    /// </summary>
    final public Type Type;

    /// <summary>
    /// The name of the node for this type.
    /// </summary>
    public String NodeName = "";

    /// <summary>
    /// The serialization info for the type's properties.
    /// </summary>
    public List<SimpleXmlPropertySerializationInfo> Properties = new ArrayList<SimpleXmlPropertySerializationInfo>();
    /// <summary>
    /// The serialization info for the type's fields.
    /// </summary>
    public List<SimpleXmlFieldSerializationInfo> Fields = new ArrayList<SimpleXmlFieldSerializationInfo>();

    
    /// <summary>
    /// Construct a simple xml type serialization object.
    /// </summary>
    /// <param name="type">The type this information is about.</param>
	public SimpleXmlTypeSerializationInfo(Type type) {
		Type = type;

        Class<?> type_val = (Class<?>)type;
//        PropertyInfo[] properties = type.GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance);
        Method[] methods = type_val.getMethods();
        Field[] fields = type_val.getFields();
        boolean isOptimistic = SerializationUtilities.IsOptimistic(type_val);
        
        NodeName = getNodeName(type_val);

        for (int i = 0; i < methods.length; i++)
        {
            if (SerializationUtilities.IsSerializable(methods[i], isOptimistic))
            {
                SimpleXmlPropertySerializationInfo info = new SimpleXmlPropertySerializationInfo(methods[i]);
                Properties.add(info);
            }
        }
        
        for (int i = 0; i < fields.length; i++)
        {
            if (SerializationUtilities.IsSerializable(fields[i], isOptimistic))
            {
                SimpleXmlFieldSerializationInfo info = new SimpleXmlFieldSerializationInfo(fields[i]);
                Fields.add(info);
            }
        }

        BuildMap();
	}
	
	/**
	 * Use the name in SimplXml annotation as name if the class is annotated with it.
	 * Otherwise, use class name as its xml node name
	 * @param type
	 * @return
	 */
	private String getNodeName(Class<?> type)
    {
        String result = "";
        
        Annotation annotation = type.getAnnotation(SimpleXml.class);

        if (annotation instanceof SimpleXml)
        {
            result = ((SimpleXml)annotation).name();//use the SimpleXml annotation name field if available
        }
        else
        {
            result = type.getSimpleName();
        }
        return result;
    }


    /// <summary>
    /// Create a node name, mapping to the info objects.
    /// </summary>
    private void BuildMap()
    {
        for (SimpleXmlPropertySerializationInfo propertyInfo : Properties)
            _propertyMap.put(propertyInfo.NodeName, propertyInfo);
        
        for (SimpleXmlFieldSerializationInfo fieldInfo : Fields)
            _fieldMap.put(fieldInfo.NodeName, fieldInfo);
    }

    /// <summary>
    /// Get a property with the given node name.
    /// </summary>
    /// <param name="name">The name of the property to find.</param>
    /// <returns>The property serialization info or null.</returns>
    public SimpleXmlPropertySerializationInfo GetProperty(String nodeName)
    {
        if (_propertyMap.containsKey(nodeName))
            return _propertyMap.get(nodeName);
        else
            return null;
    }

    /// <summary>
    /// Get a field info with the given node name.
    /// </summary>
    /// <param name="name">The name of the field to find.</param>
    /// <returns>The filed serialization info or null.</returns>
    public SimpleXmlFieldSerializationInfo GetField(String nodeName)
    {
        if (_fieldMap.containsKey(nodeName))
            return _fieldMap.get(nodeName);
        else
            return null;
    }

	
	
}
