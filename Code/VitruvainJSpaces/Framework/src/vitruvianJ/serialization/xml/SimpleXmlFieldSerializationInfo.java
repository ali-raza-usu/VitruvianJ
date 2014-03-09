package vitruvianJ.serialization.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/// <summary>
/// The serialization info for a field.
/// </summary>
public class SimpleXmlFieldSerializationInfo {
	
	/// <summary>
    /// The field info object.
    /// </summary>
	public Field Info = null;
	
	/// <summary>
    /// The name of the node for this field.
    /// </summary>
	public String NodeName = "";
	
	/// <summary>
    /// Construct a SimpleXMLFieldSerializationInfo object.
    /// </summary>
    /// <param name="info">The field info object.</param>
	public SimpleXmlFieldSerializationInfo(Field field) {
		Info = field;
		NodeName = getNodeName(field);
	}
	
	private String getNodeName(Field field)
    {
        String result = "";
        
        Annotation annotation = field.getAnnotation(SimpleXml.class);

        if (annotation instanceof SimpleXml)
        {
            result = ((SimpleXml)annotation).name();
        }
        else
        {
            result = field.getName();
        }
        return result;
    }
}
