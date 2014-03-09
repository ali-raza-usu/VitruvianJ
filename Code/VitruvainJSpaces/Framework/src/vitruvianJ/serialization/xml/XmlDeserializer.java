package vitruvianJ.serialization.xml;
import java.util.*;
import java.lang.reflect.*;


import org.w3c.dom.*;





import vitruvianJ.core.ClassFactory;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
import vitruvianJ.serialization.formatters.Formatter;
import vitruvianJ.serialization.formatters.KeyValueFormatter;

public class XmlDeserializer
{
	private static JLogger _logger = new JLogger(XmlDeserializer.class);
	private HashMap<String, Type> _typeChange = new HashMap<String, Type>();

	public XmlDeserializer()
	{
		
	}
	/// <summary>
	/// The object id and object reference mapping.
	/// </summary>
	protected class References extends HashMap<String, Object>
	{}

	/// <summary>
	/// The dictionary is used for type changes.
	/// The dictionary is used to
	/// <list>
	/// <item>standardize serialization across platforms</item>
	/// <item>standardize serialization across applications</item>
	/// <item>backwards compatibility on type or namespace changes</item>
	/// </list>
	/// <remarks>Key = name to use in serialization</remarks>
	/// <remarks>Value = type to change for the name</remarks>
	/// </summary>
	public HashMap<String, Type> getTypeChange()
	{
		return _typeChange; 
	}
	public void setTypeChange(HashMap<String, Type> value)
	{ 
		_typeChange = value; 	
	}

	/// <summary>
	/// Deserialize an XmlDocument into an object.
	/// </summary>
	/// <param name="document">The document that defines the object.</param>
	/// <returns>The object represented by the document.</returns>
	public Object Deserialize(Document document)
	{
        return Deserialize(document, null);
	}

    /// <summary>
    /// Deserialize an XmlDocument into an object.
    /// </summary>
    /// <param name="document">The document that defines the object.</param>
    /// <returns>The object represented by the document.</returns>
    public Object Deserialize(Document document, Formatter formatter)
    {
    	References references = new References();
    	try{
        Node root = document.getLastChild();
        Type type = GetType(root);
        
        Object value;

        if (type.getClass().isArray())
            value = ClassFactory.CreateObject(type, GetRanks(root));
        else
            value = ClassFactory.CreateObject(type);
  
            Deserialize(root, value.getClass(), value, formatter, references);
            return value;
        }
    	catch(Exception e){ 
    		return null;
    		}
        finally
        {
            references.clear();
        }
    }

	/// <summary>
	/// Deserialize an XmlDocument into the given object.
	/// </summary>
	/// <param name="document">The document that defines the object.</param>
	/// <param name="value">The object represented by the document.</param>
	public void Deserialize(Document document, Object value)
	{
        Deserialize(document, value, new vitruvianJ.serialization.formatters.DataSetFormatter());
	}

    /// <summary>
    /// Deserialize an XmlDocument into the given object.
    /// </summary>
    /// <param name="document">The document that defines the object.</param>
    /// <param name="value">The object represented by the document.</param>
    /// <param name="formatter">The formatter to use for this object</param>
    public void Deserialize(Document document, Object value, Formatter formatter)
    {    	
        Node root = document.getLastChild();               
        formatter = null;
        References references = new References();
        try
        {
            Deserialize(root, value.getClass(), value, formatter, references);
        }
        finally
        {
            references.clear();
        }
    }

	/// <summary>
	/// Deserialize the node into the given object.
	/// If the given object is null an object will be created.
	/// </summary>
	/// <param name="node">The node defining the object.</param>
	/// <param name="type">The type of the object.</param>
	/// <param name="curValue">The current value of the object; possibly null.</param>
	/// <param name="formatter">The formatter to use when deserializing this object; possibly null.</param>
	/// <returns>The object that was deserialized.</returns>
	/// <param name="references">The references object that allows for deserialization by reference.</param>
	public Object Deserialize(Node node, Type type, Object curValue, Formatter formatter, References references)
	{	
		//_logger.Debug("Node : " + node.getNodeName() + " type : " + type + " curvalue " + curValue);
		Object refObj = DeserializeReference(node, references);
		Class<?> type_val;
		Type collection_type = Object.class;
		if (type instanceof ParameterizedType){					
			type_val = (Class<?>)((ParameterizedType)type).getRawType();	
			collection_type =  ((ParameterizedType)type).getActualTypeArguments()[0];
		}
		else
			type_val = (Class<?>)type;
		
	    type_val  = (Class<?>)SerializationUtilities.ChangeType(type_val);

		if (refObj != null)
			return refObj;

		
		boolean isNull = IsNull(node);
		
		if (type_val.equals(String.class))
		{
			 if (isNull)
                 curValue = null;
			 
			 else if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue = GetValue(node);

			DeserializeObjectId(node, curValue, references);
		}
		
		else if ( ((Class)type_val).getSuperclass()!= null && ((Class)type_val).getSuperclass().equals(java.lang.Enum.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue = Enum.valueOf((Class)type, GetValue(node));

			DeserializeObjectId(node, curValue, references);
		}
		
		else if (type_val.equals(boolean.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue = Boolean.parseBoolean(GetValue(node));// bool.Parse(GetValue(node));

			DeserializeObjectId(node, curValue, references);
		}
		else if (type_val.equals(byte.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue = Byte.parseByte(GetValue(node));

			DeserializeObjectId(node, curValue, references);
		}
		else if (type_val.equals(char.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue =  GetValue(node).charAt(0);

			DeserializeObjectId(node, curValue, references);
		}

		else if (type_val.equals(double.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue = Double.parseDouble(GetValue(node));

			DeserializeObjectId(node, curValue, references);
		}
		else if (type_val.equals(float.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue = Float.parseFloat(GetValue(node));

			DeserializeObjectId(node, curValue, references);
		}
		else if (type_val.equals(int.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				curValue = Integer.parseInt(GetValue(node));

			DeserializeObjectId(node, curValue, references);
		}
		else if (type_val.equals(long.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else								
				curValue = Long.parseLong(GetValue(node));				

			DeserializeObjectId(node, curValue, references);
		}
		else if (type_val.equals(short.class))
		{
			if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				
				curValue = Short.parseShort(GetValue(node));
				
			DeserializeObjectId(node, curValue, references);
		}
		
		else if (type_val.equals(JGUID.class))
        {
            if (formatter != null)
                curValue = formatter.Unformat(GetValue(node));
            else
                curValue = new JGUID(GetValue(node));

            DeserializeObjectId(node, curValue, references);
        }
		else if (type_val.equals(Type.class))
		{
			 if (isNull)
                 curValue = null;
			 
			 else if (formatter != null)
				curValue = formatter.Unformat(GetValue(node));
			else
				try{
					String value  = GetValue(node);
					Type value_type = _typeChange.get(value);
					if(value_type != null)
						value = ((Class)value_type).getCanonicalName();
					//_typeChange.
				curValue = ClassFactory.FindType(value);//GetValue(node));
				}catch(Exception e){}

			DeserializeObjectId(node, curValue, references);
		}
		else			
		{
			if(isNull)
			{
				curValue = null;
			}
			else
			{
			if (type_val.isArray())
			{
				if (curValue == null)
					try{
					curValue = ClassFactory.CreateObject(type, GetRanks(node));
					}catch(Exception e){}

				DeserializeObjectId(node, curValue, references);
				Deserialize(node, curValue, formatter, references);
			}
			else if (List.class.isAssignableFrom(type_val))
			{
				if (curValue == null)
					try{
					curValue = ClassFactory.CreateObject(type_val);
					}catch(Exception e){}

				DeserializeObjectId(node, curValue, references);
				Deserialize(node, (List)curValue, formatter, references, collection_type);
			}
			else if (Dictionary.class.isAssignableFrom(type_val))
			{
				if (curValue == null)
					try{
					curValue = ClassFactory.CreateObject(type_val);
					}catch(Exception e){}

				DeserializeObjectId(node, curValue, references);
				Deserialize(node, (Hashtable)curValue, formatter, references);
			}
			else
			{				
				if (formatter != null)
					curValue = formatter.Unformat(GetValue(node));
				else
				{
					if (curValue == null)
						try{
						curValue = ClassFactory.CreateObject(type_val);
						}catch(Exception e){
							//e.printStackTrace();
						}
				  
					DeserializeObjectId(node, curValue, references);
					Deserialize(node, curValue, references);
				}
			}
			}
		}

		return curValue;
	}

	
    private boolean IsNull(Node node)
    {
        String strIsNull = SerializationUtilities.GetAttributeValue(XmlConstants.NULL_ATTRIB_ID, node);
        if (strIsNull == null)
            return false;
        else
            return Boolean.parseBoolean(strIsNull);
    }
    
	/// <summary>
	/// Deserialize the node into the given object.
	/// </summary>
	/// <param name="node">The node defining the object.</param>
	/// <param name="value">The object to deserialize into.  The object is 
	/// deserialized using reflection.</param>
	/// <param name="references">The references object that allows for deserialization by reference.</param>
	protected void Deserialize(Node node, Object value, References references)
	{
		Type type = null;
		try{
			type = value.getClass();
		}catch(Exception ex)
		{
			//_logger.Debug("value : " + value + " Node: " + node.getLocalName());
		}
        TypeSerializationInfo info = SerializationUtilities.GetReflectionInfo(type);

        for(int i =0; i<node.getChildNodes().getLength(); i++)
        {	
		  Node childNode  = node.getChildNodes().item(i);
		  
			if (childNode.getNodeType() == Node.COMMENT_NODE)
				continue;

			Object childValue = null;
			Type childType;



			if (childNode.getNodeName().equals(XmlConstants.PROPERTY_ID))
			{
				String name = SerializationUtilities.GetAttributeValue(XmlConstants.NAME_ATTRIB_ID, childNode);

                PropertySerializationInfo propertyInfo = info.Deserializable.GetProperty(name);

				if (propertyInfo == null)
				{
//					if (_logger.IsInfoEnabled())
//						_logger.InfoFormat("Property %1s in xml has no match on object type %2s.", name, type.getClass().getName());
					continue;
				}
				try{
				childType = GetType(childNode, propertyInfo.Info.getType());
				if (propertyInfo.Info.getType().isMemberClass() && propertyInfo.Info.isAccessible())
					childValue = propertyInfo.Info.get(value);

				childValue = Deserialize(childNode, childType, childValue, propertyInfo.Formatter, references);
				propertyInfo.Info.set(value, childValue);
				}catch(Exception e ){}
			}


			else if (childNode.getNodeName().equals(XmlConstants.FIELD_ID))
			{
				String name = SerializationUtilities.GetAttributeValue(XmlConstants.NAME_ATTRIB_ID, childNode);
					
                FieldSerializationInfo fieldInfo = info.Deserializable.GetField(name);

				if (fieldInfo == null)
				{
//					if (_logger.IsInfoEnabled())
//						_logger.InfoFormat("Field {0} in xml has no match on object type %1s.", name, type.getClass().getName());
					continue;
				}
				try{
				
				childType = GetType(childNode, fieldInfo.Info.getGenericType());
				
							
				if (!fieldInfo.Info.getGenericType().getClass().isMemberClass())					
                    childValue = fieldInfo.Info.get(value);					

				childValue = Deserialize(childNode, childType, childValue, fieldInfo.Formatter, references);
				fieldInfo.Info.set(value, childValue);
				}catch(Exception e ){
					e.printStackTrace();
				}
			}

			
			else if (childNode.getNodeName().equals(XmlConstants.METHOD_ID))
			{
				String name = SerializationUtilities.GetAttributeValue(XmlConstants.NAME_ATTRIB_ID, childNode);
				
				
                MethodSerializationInfo methodInfo = info.Deserializable.GetMethod("get"+name);
               
				if (methodInfo == null)
				{
//					if (_logger.IsInfoEnabled())
//						_logger.InfoFormat("Method {0} in xml has no match on object type {1}.", name, type.getClass().getName());
					continue;
				}
				try{
				
				childType = GetType(childNode, methodInfo.Info.getGenericReturnType());
				
				
				if (!methodInfo.Info.getGenericReturnType().getClass().isMemberClass())
					try{
                    childValue = methodInfo.Info.invoke(value,null);					
					}catch(Exception e){}
				childValue = Deserialize(childNode, childType, childValue, methodInfo.Formatter, references);
				
				String methodName = "set"+name;
				
				Class methodClass = null;
				if(childType.equals(Type.class))
				{
					methodClass = (Class) childType;
				}else
				{
					try{
					methodClass = childValue.getClass();
					}catch(Exception ex)
					{
						System.out.println(childValue + " " + methodName + " " + methodClass);
					}
				}
			
			    methodClass = FindCompatibleMethodForClassInterface(value.getClass(), methodClass, methodName);			
				Method method = value.getClass().getMethod(methodName, methodClass);
				
				method.invoke(value, childValue);
				}catch(Exception e ){
					e.printStackTrace();
				}
			}
		}
	}
	Method getMethod(Method method, Object obj)
	{
		
		Method[] methods = obj.getClass().getMethods();
		for(Method myMethod : methods)
		{
			if(myMethod.getName().equals(method.getName()) )
				return myMethod;
		}
		return null;
	}
	protected void Deserialize(Node node, Type type, Object value, References references)
	{
		
        TypeSerializationInfo info = SerializationUtilities.GetReflectionInfo(type);

        for(int i =0; i<node.getChildNodes().getLength(); i++)
        {	
		  Node childNode  = node.getChildNodes().item(i);
		  
			if (childNode.getNodeType() == Node.COMMENT_NODE)
				continue;

			Object childValue = null;
			Type childType;

			if (childNode.getNodeName().equals(XmlConstants.METHOD_ID))
			{
				String name = SerializationUtilities.GetAttributeValue(XmlConstants.NAME_ATTRIB_ID, childNode);
				
                MethodSerializationInfo methodInfo = info.Deserializable.GetMethod("get"+name);

				if (methodInfo == null)
				{
//					if (_logger.IsInfoEnabled())
//						_logger.InfoFormat("Method {0} in xml has no match on object type {1}.", name, type.getClass().getName());
					continue;
				}
				try{
				
				childType = GetType(childNode, methodInfo.Info.getGenericReturnType());
				
							
				if (!methodInfo.Info.getGenericReturnType().getClass().isMemberClass())
				{
					try{
					Method method = getMethod(methodInfo.Info, value);
                    childValue = method.invoke(value,null);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				childValue = Deserialize(childNode, childType, childValue, methodInfo.Formatter, references);
				
				String methodName = "set"+name;
				
				Class methodClass = null;
				if(childType.equals(type))
				{
					methodClass = (Class) childType;
				}else
				{
					try{
					methodClass = childValue.getClass();
					}catch(Exception ex)
					{
						System.out.println(childValue + " " + methodName + " " + methodClass);
					}
				}
			
			    methodClass = FindCompatibleMethodForClassInterface((Class)type, methodClass, methodName);			
				Method method = value.getClass().getMethod(methodName, methodClass);
				//methodInfo.Info.invoke(value, childValue);
				method.invoke(value, childValue);
				}catch(Exception e ){
					e.printStackTrace();
				}
			}
		}
	}
	Class FindCompatibleMethodForClassInterface(Class ownerClass, Class className, String methodName)
	{
		Class requiredMethodParameterType = null;
		
		// Get the Parametertype of the method in Owner class
		Method[] methods =  ownerClass.getMethods();
		for(int i =0; i<methods.length; i++)
		{
			Method method = methods[i];
			
			if(method.getName().equals(methodName))
			{
				requiredMethodParameterType = method.getParameterTypes()[0];
				break;
			}
		}
		
		if(requiredMethodParameterType.isPrimitive())
		{
			return requiredMethodParameterType;
		}
		else
		while(className != null && !className.getCanonicalName().equals(requiredMethodParameterType.getCanonicalName()))				
		{				
			Class[] interfaces  = className.getInterfaces();
			if(interfaces!=null && interfaces.length >0)
				className = interfaces[0];
			else{
				className = requiredMethodParameterType;
				break;
			}
				
		}
		//==============================================================================
		//	VitruvianJ.Distribution: This code was commented out because of converstion Integer->Copararable interface
		//==============================================================================
//		Class requiredMethodParameterType = null;
//		
//			// Get the Parametertype of the method in Owner class
//			Method[] methods =  ownerClass.getMethods();
//			for(int i =0; i<methods.length; i++)
//			{
//				Method method = methods[i];
//				
//				if(method.getName().equals(methodName))
//				{
//					requiredMethodParameterType = method.getParameterTypes()[0];
//					break;
//				}
//			}
//			
//			while(className != null && !className.getCanonicalName().equals(requiredMethodParameterType.getCanonicalName()))				
//			{				
//				Class[] interfaces  = className.getInterfaces();
//				if(interfaces!=null && interfaces.length >0)
//					className = interfaces[0];
//				else
//					break;
//			}
			
		return className;
	}
	/// <summary>
	/// Deserialize the node into the given list.
	/// </summary>
	/// <param name="node">The node defining the list.</param>
	/// <param name="value">The list to deserialize into.</param>
	/// <param name="formatter">The formatter to use for contained items.</param>
	/// <param name="references">The references object that allows for deserialization by reference.</param>
	protected void Deserialize(Node node, List value, Formatter formatter, References references, Type type)
	{
		if(value!=null)
		value.clear();		
		//Why UPDProvider is not beeing extracted here.
		Type elementType = Object.class;				
			elementType = type;
			
			
		for (int i =0; i<node.getChildNodes().getLength(); i++)
		{
			Node childNode = node.getChildNodes().item(i);
			if (childNode.getNodeType() == Node.COMMENT_NODE )
				continue;
		//	SerializationUtilities.listNodes(childNode);
		//	String typeStr = SerializationUtilities.GetAttributeValue(XmlConstants.TYPE_ATTRIB_ID, childNode);
		//	if(typeStr!= null){
			Type childType = GetType(childNode, elementType);	
			Object childValue = new Object();
			try{
				childValue = Deserialize(childNode, childType, null, formatter, references);			
			//_logger.Debug(" Type : " + childType + " Value : " + childValue);
			}catch(Exception e){}
			if(childValue != null && !childValue.toString().contains("Object"))
			value.add(childValue);			
		//	}
		}
	}

	/// <summary>
	/// Deserialize the node into the given dictionary.
	/// </summary>
	/// <param name="node">The node defining the dictionary.</param>
	/// <param name="value">The dictionary to deserialize into.</param>
	/// <param name="formatter">The formatter to use for contained items.</param>
	/// <note>The formatter needs to be changed to allow for key / value formatting.</note>
	/// <param name="references">The references object that allows for deserialization by reference.</param>
	Hashtable<String,Type> name = null;
	protected void Deserialize(Node node, Hashtable<String, Type> value, Formatter formatter, References references)
	{
		Type keyType = Object.class; 
		Type valueType = Object.class;
		Field field = null;
		ParameterizedType type = null;
		try {
			name = value;
			 field = XmlDeserializer.class.getDeclaredField("name");
			 type = (ParameterizedType) field.getGenericType();		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
		Type[] genericKeyArgs = type.getActualTypeArguments();// value.getClass().getClasses();//getTypeParameters();// .keys().getClass().getTypeParameters();//getGenericInterfaces();
		Type[] genericValueArgs = type.getActualTypeArguments();// value.getClass().getTypeParameters();//value.elements().getClass().getTypeParameters();//.getGenericInterfaces();

		if (genericKeyArgs != null)
			keyType = genericKeyArgs[0];//.getClass().getGenericSuperclass();
		if (genericValueArgs.length > 1)
			valueType = genericValueArgs[1];//.getClass();
		

        Formatter keyFormatter = null;
        Formatter valueFormatter = null;
        if (formatter != null && formatter.getClass().equals(KeyValueFormatter.class))
        {
            keyFormatter = ((KeyValueFormatter)formatter).getKeyFormatter();
            valueFormatter = ((KeyValueFormatter)formatter).getValueFormatter();
        }

        for(int index = 0; index < node.getChildNodes().getLength(); index++)
        {
        	Node childNode = node.getChildNodes().item(index);
        		
			if ((childNode.getNodeName()).equals(XmlConstants.ENTRY_ID) )
			{
				Node keyNode = null;
				Node valueNode = null;
				for(int i = 0; i < childNode.getChildNodes().getLength(); i++)
				{
					Node entryChild  = childNode.getChildNodes().item(i);
				
					if (entryChild.getNodeName().equals(XmlConstants.ENTRY_KEY_ID))
						keyNode = entryChild;
					else if (entryChild.getNodeName().equals(XmlConstants.ENTRY_VALUE_ID))
						valueNode = entryChild;
				}

				if ((keyNode == null) ||
				    (valueNode == null))
					continue;

				Type actualKeyType = GetType(keyNode, keyType);
				Type actualValueType = GetType(valueNode, valueType);

                Object entryKey = Deserialize(keyNode, actualKeyType, null, keyFormatter, references);
                Object entryValue = Deserialize(valueNode, actualValueType, null, valueFormatter, references);

				value.put((String)entryKey, (Class)entryValue);
			}
		}
	}

	/// <summary>
	/// Deserialize the node into the given array.
	/// </summary>
	/// <param name="node">The node defining the array.</param>
	/// <param name="value">The array to deserialize into.</param>
	/// <param name="formatter">The formatter to use for contained items.</param>
	/// <param name="references">The references object that allows for deserialization by reference.</param>
	protected void Deserialize(Node node, Object value, Formatter formatter, References references)
	{
		Type elementType = value.getClass().getComponentType();// value.getClass().getClass();//.GetElementType();

		for (int i = 0; i < node.getChildNodes().getLength(); i++)
		{
			Node childNode = node.getChildNodes().item(i);
			Type childType = GetType(childNode, elementType);
			Object childValue = Deserialize(childNode, childType, null, formatter, references);
			int[] childIndex = GetIndices(childNode, i);
			for(int j =0;j<childIndex.length; j++)			
			{				
				Array.set(value,childIndex[j], childValue);
			}
		}
	}

	protected static void DeserializeObjectId(Node node, Object value, References references)
	{
		String objectId = SerializationUtilities.GetAttributeValue(XmlConstants.OBJECT_UID, node);
		if (objectId != null)
			references.put(objectId, value);
	}

	static private Object DeserializeReference(Node node, References references)
	{
		
		String refId = SerializationUtilities.GetAttributeValue(XmlConstants.REFERENCE_ID, node);
		if (refId != null)
			return references.get(refId);

		return null;
	}

	private Type GetType(Node node)
	{
		try{
		String typeStr = SerializationUtilities.GetAttributeValue(XmlConstants.TYPE_ATTRIB_ID, node);
		if (typeStr == null)
			return null;

		if (_typeChange.containsKey(typeStr))
			return _typeChange.get(typeStr);
		else
			return ClassFactory.FindType(typeStr);
		}catch(Exception e)
		{ 
			e.printStackTrace();
			return null;}
	}

	private Type GetType(Node node, Type defaultType)
	{
		Type type = GetType(node);
		if (type == null)
			return defaultType;
		return type;
	}

	private static int[] GetRanks(Node node)
	{
		String sizeStr = SerializationUtilities.GetAttributeValue(XmlConstants.SIZE_ATTRIB_ID, node);

		if (sizeStr != null)
		{
			String[] splitStr = sizeStr.split(",");
			int[] result = new int[splitStr.length];
			for (int i = 0; i < splitStr.length; i++)
				result[i] = Integer.parseInt(splitStr[i]);
			return result;
		}
		else
			return new int[] { node.getChildNodes().getLength() };
	}

	private static int[] GetIndices(Node node, int defaultIndex)
	{
		String indexStr = SerializationUtilities.GetAttributeValue(XmlConstants.INDEX_ATTRIB_ID, node);

		if (indexStr != null)
		{
			String[] splitStr = indexStr.split(",");
			int[] result = new int[splitStr.length];
			for (int i = 0; i < splitStr.length; i++)
				result[i] = Integer.parseInt(splitStr[i]);
			return result;
		}
		else
			return new int[] { defaultIndex };
	}

	private static String GetValue(Node node)
	{
		String strValue = SerializationUtilities.GetAttributeValue(XmlConstants.VALUE_ATTRIB_ID, node);
		if (strValue != null)
			return strValue;
		
		return node.getTextContent();
	}
}