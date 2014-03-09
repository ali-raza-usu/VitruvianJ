package vitruvianJ.serialization;
import java.util.*;

public class SerializationInfo
{
    public List<PropertySerializationInfo> Properties = new ArrayList<PropertySerializationInfo>();
    public List<FieldSerializationInfo> Fields = new ArrayList<FieldSerializationInfo>();
    public List<MethodSerializationInfo> Methods = new ArrayList<MethodSerializationInfo>();

    private static Dictionary<String, PropertySerializationInfo> _propertyMap = new Hashtable<String, PropertySerializationInfo>();
    private static Dictionary<String, FieldSerializationInfo> _fieldMap = new Hashtable<String, FieldSerializationInfo>();
    private static Dictionary<String, MethodSerializationInfo> _methodMap = new Hashtable<String, MethodSerializationInfo>();

    /// <summary>
    /// Create a name, mapping to the info objects.
    /// </summary>
    void BuildMap()
    {
    	
    	for(PropertySerializationInfo propertyInfo:Properties)
    	{
    		_propertyMap.put(propertyInfo.Info.getName(), propertyInfo);
    	}
    	
    	
    	for(FieldSerializationInfo fieldInfo:Fields)        
            _fieldMap.put(fieldInfo.Info.getName(), fieldInfo);
    	
    	for(MethodSerializationInfo methodInfo:Methods)        
            _methodMap.put(methodInfo.Info.getName(), methodInfo);
    }

    /// <summary>
    /// Get a property with the given name.
    /// </summary>
    /// <param name="name">The name of the property to find.</param>
    /// <returns>The property serialization info or null.</returns>
    public PropertySerializationInfo GetProperty(String name)
    {
        if (_propertyMap.equals(name))
            return _propertyMap.get(name);
        else
            return null;
    }
    
    /// <summary>
    /// Get a field with the given name.
    /// </summary>
    /// <param name="name">The name of the field to find.</param>
    /// <returns>The field serialization info or null.</returns>
    public FieldSerializationInfo GetField(String name)
    {
    	Enumeration<FieldSerializationInfo> e = _fieldMap.elements();
    	while(e.hasMoreElements()){
    		FieldSerializationInfo field = e.nextElement();
    		if(field.Info.getName().equals(name))
    			return field;    		
    	}
    	return null;        
    }
    
    
    public MethodSerializationInfo GetMethod(String name)
    {
    	Enumeration<MethodSerializationInfo> e = _methodMap.elements();
    	while(e.hasMoreElements()){
    		MethodSerializationInfo method = e.nextElement();
    		if(method.Info.getName().equals(name))
    			return method;    		
    	}
    	return null;        
    }
}