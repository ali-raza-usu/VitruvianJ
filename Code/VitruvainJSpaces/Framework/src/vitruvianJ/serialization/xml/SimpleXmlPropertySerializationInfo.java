package vitruvianJ.serialization.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/***
 * The equivalent of C# Property in Java is accessor methods(getters and setters).
 * So Method is used as a replacement of PropertyInfo here.
 * @author Sean
 *
 */
/// <summary>
/// The serialization info for a property.
/// </summary>
public class SimpleXmlPropertySerializationInfo {

	/// <summary>
    /// The property info object.
    /// </summary>
	public Method Info = null;
	
	/// <summary>
    /// The name of the node for this property.
    /// </summary>
	public String NodeName = "";
	
	/// <summary>
    /// Construct a PropertySerializationInfo object.
    /// </summary>
    /// <param name="info">The property info object.</param>
	public SimpleXmlPropertySerializationInfo(Method method) {
		Info = method;
		NodeName = getNodeName(method);
	}
	
	private String getNodeName(Method method)
    {
        String result = "";
        
        Annotation annotation = method.getAnnotation(SimpleXml.class);

        if (annotation instanceof SimpleXml)
        {
            result = ((SimpleXml)annotation).name();
        }
        else
        {
            result = method.getName();
        }
        return result;
    }
}
