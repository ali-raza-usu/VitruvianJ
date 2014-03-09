package vitruvianJ.serialization;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;


import org.w3c.dom.Attr;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import vitruvianJ.collections.ISmartList;
import vitruvianJ.core.ClassFactory;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.logging.PathUtilities;
import vitruvianJ.serialization.formatters.Formatter;
import vitruvianJ.serialization.xml.XmlConstants;
import vitruvianJ.serialization.xml.XmlDeserializer;
import vitruvianJ.serialization.xml.XmlFramework;


import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SerializationUtilities
{
	
	private static JLogger _logger = new JLogger(SerializationUtilities.class);
    /// <summary>
    /// Serialization info based on type.
    /// </summary>
    private static Dictionary<Type, TypeSerializationInfo> _typeSerializationInfo = new Hashtable<Type, TypeSerializationInfo>();

    /// <summary>
    /// Get the serialization information available through reflection.
    /// This information is cached for subsequent use.
    /// </summary>
    /// <param name="type">The type to gather serialization information for.</param>
    /// <returns>The reflected serialization information.</returns>
    public static TypeSerializationInfo GetReflectionInfo(Type type)
    {
    	//_logger.Debug("Type : GetReflectionInfo " + type + "\n");
        synchronized (_typeSerializationInfo)
        {
            TypeSerializationInfo result = null;

            if (_typeSerializationInfo.equals(type))
                result = _typeSerializationInfo.get(type);
            else
            {
                result = new TypeSerializationInfo(type);
                _typeSerializationInfo.put(type, result);
            }

            return result;
        }
    }

    

	/// <summary>
    /// Determine if the object uses optimistic serialization.
    /// </summary>
    /// <param name="value"></param>
    /// <returns></returns>
    public static Boolean IsOptimistic(Type type)
    {    	
    	Class<?> type_class = (Class<?>)type;
    	
    	OptimisticSerialization attribute = (OptimisticSerialization)type_class.getAnnotation(OptimisticSerialization.class);
        if(attribute != null)
           return true;
        else
           return false;
                   
    	//GetCustomAttributes(OptimisticSerializationAttribute.class, false);    	
    }

    
	/// <summary>
	/// Determine if a property is deserializable.
	/// A property is serializable if it has a 'set' method and has a serializable attribute.
	/// </summary>
	/// <param name="info">The property info to determine serializability for.</param>
    /// <param name="isOptimistic">Is optimistic serialization being used.</param>
    /// <returns>True if the property should be serialized, otherwise False.</returns>
    /*
    public static Boolean IsDeserializable(PropertyInfo info, bool isOptimistic)
	{
		if (!info.CanWrite)
			return false;

        // Check that the # of parameters is 0.
        if (info.GetIndexParameters().Length > 0)
            return false;

        if (isOptimistic)
        {
            DontSerializeAttribute[] attributes =
                (DontSerializeAttribute[])info.GetCustomAttributes(typeof(DontSerializeAttribute), false);
            if (attributes.Length > 0)
                return false;
            else
                return true;
        }
        else
        {
            SerializeAttribute[] attributes =
                (SerializeAttribute[])info.GetCustomAttributes(typeof(SerializeAttribute), false);
            if (attributes.Length > 0)
                return true;
            else
                return false;
        }
	}
*/
	/// <summary>
	/// Determine if a field is deserializable.
	/// A field is deserializable if it has a serializable attribute.
	/// </summary>
	/// <param name="info">The field info to determine serializablity for.</param>
    /// <param name="isOptimistic">Is optimistic serialization being used.</param>
    /// <returns>True if the field should be serialized, otherwise False.</returns>
    public static Boolean IsDeserializable(Field info, Boolean isOptimistic)
	{
    	
//    	if(!info.isAccessible())
//    		return false;
    	
    	if(info.getModifiers()>0)
    		return false;
    	
        if (isOptimistic)
        {        	 
            DontSerialize attribute =
                (DontSerialize)info.getAnnotation(DontSerialize.class);
            if(attribute != null)
                return false;
            else
                return true;
        }
        else
        {
            Serialize attribute =
                (Serialize)info.getAnnotation(Serialize.class);
            if(attribute != null)
                return true;
            else
                return false;
        }
	}

    public static Boolean IsDeserializable(Method info, Boolean isOptimistic)
	{
    	
//    	if(!info.isAccessible())
//    		return false;
    	
    	if(info.getModifiers()>0)
    		return false;
    	
        if (isOptimistic)
        {        	 
            DontSerialize attribute =
                (DontSerialize)info.getAnnotation(DontSerialize.class);
            if(attribute != null)
                return false;
            else
                return true;
        }
        else
        {
            Serialize attribute =
                (Serialize)info.getAnnotation(Serialize.class);
            if(attribute != null)
                return true;
            else
                return false;
        }
	}
	/// <summary>
	/// Determine if a property is serializable.
	/// A property is serializable if it has a 'get' method and has a serializable attribute.
	/// </summary>
	/// <param name="info">The property info to determine serializability for.</param>
    /// <param name="isOptimistic">Is optimistic serialization being used.</param>
    /// <returns>True if the property should be serialized, otherwise False.</returns>
    /*
    public static bool IsSerializable(PropertyInfo info, bool isOptimistic)
	{
		if (!info.CanRead)
			return false;

		// Check that the # of parameters is 0.
        if (info.GetIndexParameters().Length > 0)
            return false;

        if (isOptimistic)
        {
            DontSerializeAttribute[] attributes =
                (DontSerializeAttribute[])info.GetCustomAttributes(typeof(DontSerializeAttribute), false);
            if (attributes.Length > 0)
                return false;
            else
                return true;
        }
        else
        {
            SerializeAttribute[] attributes =
                (SerializeAttribute[])info.GetCustomAttributes(typeof(SerializeAttribute), false);
            if (attributes.Length > 0)
                return true;
            else
                return false;
        }
	}
*/
	/// <summary>
	/// Determine if a field is serializable.
	/// A field is serializable if it has a serializable attribute.
	/// </summary>
	/// <param name="info">The field info to determine serializablity for.</param>
    /// <param name="isOptimistic">Is optimistic serialization being used.</param>
    /// <returns>True if the field should be serialized, otherwise False.</returns>
    public static Boolean IsSerializable(Field info, Boolean isOptimistic)
	{    	    	
		if (isOptimistic)// && info.isAccessible()) //IsPublic)
        {
			DontSerialize attribute = (DontSerialize)info.getAnnotation(DontSerialize.class);
            if(attribute != null)
               return false;
           else
               return true;
        }else
        {
            Serialize attribute = (Serialize)info.getAnnotation(Serialize.class);
             if(attribute != null)
                return true;
            else
                return false;
        }
		
                        
            
	}

    public static Boolean IsSerializable(Method info, Boolean isOptimistic)
	{    	    	
		//if (isOptimistic && info.isAccessible()) //IsPublic)
		if (isOptimistic)
		{
			DontSerialize attribute = (DontSerialize)info.getAnnotation(DontSerialize.class);
            if(attribute != null)
               return false;
           else
               return true;
        }else
        {
            Serialize attribute = (Serialize)info.getAnnotation(Serialize.class);
             if(attribute != null)
                return true;
            else
                return false;
        }
		
                        
            
	}
	/// <summary>
	/// Get the formatter attribute applied to the property.
	/// </summary>
	/// <param name="info">The property to get the formatter for.</param>
	/// <returns>The formatter if one exists, otherwise null.</returns>
    /*
	public static Formatter GetFormatter(PropertyInfo info)
	{
		Formatter[] attributes =
			(Formatter[]) info.GetCustomAttributes(typeof (Formatter), false);
		if (attributes.Length <= 0)
			return null;

		return attributes[0];
	}
*/
	/// <summary>
	/// Get the formatter attribute applied to the field.
	/// </summary>
	/// <param name="info">The field to get the formatter for.</param>
	/// <returns>The formatter if one exists, otherwise null.</returns>
	public static Formatter GetFormatter(Field info)
	{/*
		Formatter[] attributes =
			(Formatter[]) info.getAnnotations();// GetCustomAttributes(Formatter.class, false);
		if (attributes.length <= 0)
			return null;

		return attributes[0];
		*/
		return  null;
	}
	
	public static Formatter GetFormatter(Method info)
	{
		
		Annotation[] attributes = info.getAnnotations();// GetCustomAttributes(Formatter.class, false);		
		
		if (attributes.length > 1){
			try {
			Class attribute = attributes[0].annotationType();
			
			//To be designed later
			AnnotatedElement element = info;
			IPFormatter ipformatter = (IPFormatter)element.getAnnotation(attribute);
			String value = ipformatter.getFormatter();
			Type type = ClassFactory.FindType(value);
						
			//Type type = null;
			
				Formatter formatter = (Formatter)ClassFactory.CreateObject(type);
				return formatter;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

			return  null;
	}

	/// <summary>
	/// Get the value of an attribute from a node.
	/// </summary>
	/// <param name="attributeName">The attribute to of interest.</param>
	/// <param name="node">The node that possibly contains the attribute.</param>
	/// <returns>The value of the attribute, otherwise null.</returns>
	public static String GetAttributeValue(String attributeName, Node node)
	{						
		NamedNodeMap nodesMap =  node.getAttributes();
		if(nodesMap != null){
			for(int i =0; i<nodesMap.getLength(); i++){
				Attr attribute = (Attr) nodesMap.item(i);				
				if(attribute.getName().equals(attributeName))
					return attribute.getValue();
			}
		}		
		return null;	
	}
	
	public static void listNodes(Node node) {
	    String nodeName = node.getNodeName();
	    System.out.println(" Node: "+nodeName);
	    short type =node.getNodeType();
	    System.out.println(" Node Type: " + type);
	    if(type == Node.TEXT_NODE){
	      System.out.println(" Content is: "+((org.w3c.dom.Text)node).getWholeText());
	    } else if(node.hasAttributes()) {
	      System.out.println(" Element Attributes are:");
	      NamedNodeMap attrs = node.getAttributes();  
	      for(int i = 0 ; i<attrs.getLength() ; i++) {
	        Attr attribute = (Attr)attrs.item(i);     
	        System.out.println(" " + attribute.getName()+" = "+attribute.getValue());
	      }
	    }
	    
//	    NodeList list = node.getChildNodes();       
//	    if(list.getLength() > 0) {                  
//	      System.out.println(" Child Nodes of "+nodeName+" are:");
//	      for(int i = 0 ; i<list.getLength() ; i++) {
//	        listNodes(list.item(i));      
//	      }
//	    }         
	  }
	
	public static InputStream GetEmbeddedFile(Type type, String resource) throws Exception
	{

		try
		{
		
			InputStream str = new InputStream() {
				
				@Override
				public int read() throws IOException {
					// TODO Auto-generated method stub
					return 0;
				}
			};
					

			if (str == null)
				throw new Exception("Could not locate embedded resource '" + resource);
				//throw new Exception("Could not locate embedded resource '" + resource + "' in assembly '" + assemblyName + "'");

			return str;
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
	}

    /// <summary>
    /// Determine if the type is required in serialization.
    /// </summary>
    /// <param name="baseType">The base type.</param>
    /// <param name="valueType">The type of the value.</param>
    /// <returns>True if the type is different than the base type.</returns>
	static public Type ChangeType(Type valueType)
	{
		Type type = valueType;
    	if (valueType.equals(Boolean.class))
		{
    		type = boolean.class;
		}
		else if (valueType.equals(Byte.class))
		{
			type = byte.class;
		}

		else if (valueType.equals(Character.class))
		{
			type = char.class;
		}

		else if (valueType.equals(Double.class))
		{
			type = double.class;
		}
		else if (valueType.equals(Float.class))
		{
			type = float.class;
		}
		else if (valueType.equals(Integer.class))
		{
			type = int.class;
		}
		else if (valueType.equals(Long.class))
		{
			type = long.class;
		}
		else if (valueType.equals(Short.class))
		{
			type = short.class;
		}    	
		else if (valueType.equals(ArrayList.class))
		{
			type = List.class;
		} 
		else if (valueType.equals(JGUID.class))
		{
			type = JGUID.class;
		}    	
		else if (valueType.getClass().isArray())
		{
			type = Object[].class;
		}   
		else if (valueType.equals(ISmartList.class))
		{
			type = ISmartList.class;
		}    
		else if(valueType.toString().contains("List"))
			type = List.class;
    	return type;
	}
	
    static public Boolean RequireType(Type baseType, Type valueType)
    {    	      	
        return !baseType.equals(valueType);
    }
}