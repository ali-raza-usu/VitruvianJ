package vitruvianJ.serialization.xml;
import java.util.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import org.w3c.dom.*;


import vitruvianJ.logging.*;
import vitruvianJ.serialization.FieldSerializationInfo;
import vitruvianJ.serialization.MethodSerializationInfo;
import vitruvianJ.serialization.PropertySerializationInfo;
import vitruvianJ.serialization.SerializationUtilities;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.serialization.TypeSerializationInfo;
import vitruvianJ.serialization.formatters.Formatter;
import vitruvianJ.serialization.formatters.KeyValueFormatter;

import org.junit.*;

import javax.xml.parsers.*;

import junit.framework.TestCase;

public class XmlSerializer extends TestCase
{
	protected static  JLogger _logger = new JLogger(XmlSerializer.class);
	private HashMap<Type, String> _typeChange = new HashMap<Type, String>();
	
	 private boolean _strongTyped = true;

	/// <summary>
	/// The references to objects, and their associated xml node.
	/// </summary>
	protected class References extends HashMap<Object, Node>
	{
		private int _referenceCnt = 0;

		public References()
		{}
		
		public String NextReference()
		{
			int result = _referenceCnt++;
			return result+"";
		}
	}

	/// <summary>
	/// The dictionary is used for type changes.
	/// The dictionary is used to
	/// <list>
	/// <item>standardize serialization across platforms</item>
	/// <item>standardize serialization across applications</item>
	/// <item>backwards compatibility on type or namespace changes</item>
	/// </list> 
	/// </summary>
	public HashMap<Type, String> getTypeChange()
	{
		return _typeChange; 
	}
	
	public void setTypeChange(HashMap<Type, String> value)
	{
		 _typeChange = value; 
	}

	
	 @Serialize
      public boolean getStrongTyped()
      {
          return _strongTyped; 
      }
      
	 public void setStrongTyped(boolean value) 
	 { 
		 _strongTyped = value; 
      }
	 
	/// <summary>
	/// Serialize an object into an XmlDocument.
	/// </summary>
	/// <param name="value">The object to serialize.</param>
	/// <returns>The xml document representing the object.</returns>
	public Document Serialize(Object value)
	{
        return Serialize(value, null);
	}

	
    /// <summary>
    /// Serialize an Object into an XmlDocument.
    /// </summary>
    /// <param name="value">The Object to serialize.</param>
    /// <param name="formatter">The formatter to use for this Object</param>
    /// <returns>The xml document representing the Object.</returns>
    public Document Serialize(Object value, Formatter formatter)
    {
    	References references = new References();
    	try
        {
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().newDocument();         
        Node root = document.createElement(XmlConstants.ROOT_ID);// CreateNode(Node.E XmlNodeType.Element, XmlConstants.ROOT_ID, "");                
        Serialize(document, root, true, value, formatter, references);  
        printAllChildNodes(root);
        document.appendChild(root);
     //   XmlFramework.Save("printMessage.xml", document);
      //  printAllChildNodesFromDocument(document);
            return document;
        }catch(Exception e){ 
        	e.printStackTrace();
        	return null;
        }finally
        {
            references.clear();
        }
        
    }

    protected void Serialize(Document document, Node node, JGUID value)
    {
        AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value.toString());
    }
	/// <summary>
	/// Serialize the Object into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the Object into.</param>
	/// <param name="requireType">Indication if the type of the Object needs to be added as an attribute to the node.</param>
	/// <param name="type">The type of the Object being serialized.</param>
	/// <param name="value">The Object to serialize.</param>
	/// <param name="formatter">The formatter to use when serializing the Object; possibly null.</param>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
    
    protected void AddNullAttribute(Document document, Node node)
    {
        Attr attribute = document.createAttribute(XmlConstants.NULL_ATTRIB_ID);
        attribute.setValue("True");
        node.getAttributes().setNamedItem(attribute);        
    }
    
	protected void Serialize(Document document, Node node, boolean requireType, Object value, Formatter formatter, References references)
	{		
		
		if (value == null)
        {
            AddNullAttribute(document, node);
            return;
        }
		
		Class<?> type;	
		type = value.getClass();//type;
	    type  = (Class<?>)SerializationUtilities.ChangeType(type);	    
	    
		Type collection_type = (Type)type;
		if (collection_type instanceof ParameterizedType){									
			collection_type =  ((ParameterizedType)collection_type).getActualTypeArguments()[0];			
		}
		
		if (type.equals(String.class))
		{
			
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, (String) value);
		}
		else if ( ((Class)type).getSuperclass()!= null && ((Class)type).getSuperclass().equals(java.lang.Enum.class))
		//else if (type.getClass().isEnum())
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, (Enum) value);
		}
		else if (type.equals(boolean.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, Boolean.parseBoolean(value.toString()));
		}
		else if (type.equals(byte.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, Byte.parseByte(value.toString()));
		}

		else if (type.equals(char.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, value.toString().charAt(0));
		}

		else if (type.equals(double.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node,Double.parseDouble(value.toString()));
		}
		
		else if (type.equals(float.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, Float.parseFloat(value.toString()));
		}
		else if (type.equals(int.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, Integer.parseInt(value.toString()));
		}
		else if (type.equals(long.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, Long.parseLong(value.toString()));
		}
		/***
		 * Add primitive objects
		 * 
		 */
		
		else if (type.equals(short.class))
		{
			if (requireType)
				AddTypeAttribute(document, node, type);

			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
				Serialize(document, node, Short.parseShort(value.toString()));
		}
        else if (type.equals(JGUID.class))
        {
            if (requireType)
                AddTypeAttribute(document, node, type);

            if (formatter != null)
                AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
            else
                Serialize(document, node, (JGUID)value);
        }
		else if (type.equals(java.lang.Class.class))
		{
			if (formatter != null)
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
			else
			{
				Class valueClass = (java.lang.Class)value;
				
				Serialize(document, node, valueClass);//(Type) value);
			//	getTypeChange()
			}
		}
		else
		{
			if (SerializeReference(document, node, value, references))
				return;

            if (requireType)            	
                AddTypeAttribute(document, node,value.getClass());
            

			if (type.isArray())
			{
				//Array arr = Array.
				Serialize(document, node, value, formatter, references);
			}
			else if (type.equals(List.class))
				Serialize(document, node, (List) value, formatter, references, collection_type);
			
			else if (type.equals(ArrayList.class))
				Serialize(document, node, (ArrayList) value, formatter, references, collection_type);
			
			else if (type.equals(Dictionary.class))
				Serialize(document, node, (Dictionary) value, formatter, references);
			else
			{
				if (formatter != null)
					AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, formatter.Format(value));
				else
					Serialize(document, node, value, references);
			}
		  }
		
	}

	/// <summary>
	/// Serialize the Object into the node using reflection.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the Object into.</param>
	/// <param name="value">The Object to serialize.</param>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
	protected void Serialize(Document document, Node node, Object value, References references)
	{
        TypeSerializationInfo info = SerializationUtilities.GetReflectionInfo(value.getClass());

        Object item = null;

        for(PropertySerializationInfo propertyInfo  : info.Serializable.Properties)
		{
			if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Serializing Property : %1s ", propertyInfo.Info.getName());
			try{
            item = propertyInfo.Info.get(value);
			}catch(Exception e) {}
			if (item == null)
				continue;

            Node childNode = Serialize(document, propertyInfo, item, references);
			node.appendChild(childNode);
		}


        for (FieldSerializationInfo fieldInfo : info.Serializable.Fields)
		{
			if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Serializing Field : {" + fieldInfo.Info.getName() + "}", fieldInfo.Info.getName());
			try{
				item = fieldInfo.Info.get(value);
			}catch(Exception e){				
			}					
            Node childNode = Serialize(document, fieldInfo, item, references);
			node.appendChild(childNode);
		}


		for (MethodSerializationInfo methodInfo : info.Serializable.Methods)
		{
			
			if(methodInfo.Info.getName().contains("get")){			
//				if (_logger.IsDebugEnabled())
//		            _logger.DebugFormat("Serializing Method : %1$2s", methodInfo.Info.getName());
				try{				
					item = methodInfo.Info.invoke(value, null);		
//					if(methodInfo.Info.getDeclaringClass().getName().contains("RequestExecuteMethod"))
//					{
//						_logger.Debug("XmlSerializer : message : " + methodInfo.Info.getName() + " value " + item);
//					}
				}catch(Exception e){	
					e.printStackTrace();
				}					
		        	Node childNode = Serialize(document, methodInfo, item, references);
		        	
		        	//printAllChildNodes(node);
		        	node.appendChild(childNode);
		        //	printAllChildNodes(node);
			}
		}

	}
	
	void printAllChildNodes(Node node)
	{
		NodeList list = node.getChildNodes();
		
		for(int i = 0; i<list.getLength(); i++)
		{
			Node item = list.item(i);
			NamedNodeMap attributes = item.getAttributes();		
			for(int j=0; j<attributes.getLength(); j++)
			{
				 Node itemNode = attributes.item(j);
				System.out.print(itemNode.getNodeName() + ": " + itemNode.getNodeValue() + " -> ");
			}
		}
		System.out.println("");
	}

	void printAllChildNodesFromDocument(Document doc)
	{
		NodeList list = doc.getChildNodes();
		//NodeList list = node.getChildNodes();
		
		for(int i = 0; i<list.getLength(); i++)
		{
			Node item = list.item(i);
			NamedNodeMap attributes = item.getAttributes();		
			for(int j=0; j<attributes.getLength(); j++)
			{
				 Node itemNode = attributes.item(j);
				System.out.print(itemNode.getNodeName() + ": " + itemNode.getNodeValue() + " -> ");
			}
		}
		System.out.println("");
	}
	/// <summary>
	/// Serialize the property from the Object.
	/// </summary>
	/// <param name="document">The document to serialize into.</param>
	/// <param name="pInfo">The property to serialize.</param>
	/// <param name="value">The Object that the property is from.</param>
	/// <returns>A node with the serialized value of the property.</returns>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
    protected Node Serialize(Document document, PropertySerializationInfo pInfo, Object value, References references)
	{
		Node childNode = document.createElement(XmlConstants.PROPERTY_ID);
		AddAttribute(document, childNode, XmlConstants.NAME_ATTRIB_ID, pInfo.Info.getName());
		Serialize(document,
                  childNode, 
                  SerializationUtilities.RequireType(pInfo.Info.getType(), value.getClass()),
                  value,
				  pInfo.Formatter, references);
		return childNode;
	}

	/// <summary>
	/// Serialize the field from the Object.
	/// </summary>
	/// <param name="document">The document to serialize into.</param>
	/// <param name="fInfo">The field to serialize.</param>
	/// <param name="value">The Object that the field is from.</param>
	/// <returns>A node with the serialized value of the field.</returns>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
    protected Node Serialize(Document document, FieldSerializationInfo fInfo, Object value, References references)
	{
		Node childNode = document.createElement(XmlConstants.FIELD_ID);				
		AddAttribute(document, childNode, XmlConstants.NAME_ATTRIB_ID, fInfo.Info.getName());
		
		if(value != null)
        Serialize(document, childNode, SerializationUtilities.RequireType(SerializationUtilities.ChangeType(fInfo.Info.getType()), SerializationUtilities.ChangeType(value.getClass()) ), value,
				  fInfo.Formatter, references);
		else
			
			Serialize(document, childNode, SerializationUtilities.RequireType(fInfo.Info.getType(), null ), value,
					  fInfo.Formatter, references);
		return childNode;
	}

    
  /// <summary>
	/// Serialize the method from the Object.
	/// </summary>
	/// <param name="document">The document to serialize into.</param>
	/// <param name="mInfo">The method to serialize.</param>
	/// <param name="value">The Object that the method is from.</param>
	/// <returns>A node with the serialized value of the method.</returns>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
    protected Node Serialize(Document document, MethodSerializationInfo mInfo, Object value, References references)
	{
		Node childNode = document.createElement(XmlConstants.METHOD_ID);				
		AddAttribute(document, childNode, XmlConstants.NAME_ATTRIB_ID, mInfo.Info.getName().substring(3));
		
		if(value != null)
        Serialize(document, childNode, SerializationUtilities.RequireType(SerializationUtilities.ChangeType(mInfo.Info.getGenericReturnType()), SerializationUtilities.ChangeType(value.getClass()) ), value,
				  mInfo.Formatter, references);
		else
			
			Serialize(document, childNode, SerializationUtilities.RequireType(mInfo.Info.getGenericReturnType(), null ), value,
					  mInfo.Formatter, references);
		return childNode;
	}
    
//    protected String getAnnotatedPropertyName(FieldSerializationInfo fInfo)
//    {
//    	String name = "";
//    	Annotation[] annotations = fInfo.Info.getDeclaredAnnotations();
//		for(Annotation annotation : annotations){
//		    if(annotation instanceof Serialize){
//		    	Serialize myAnnotation = (Serialize) annotation;
//		        name = 	myAnnotation.getName();
//		        return name;
//		    }
//		}
//    	return name;
//    }
    
	/// <summary>
	/// Serialize the list into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the list into.</param>
	/// <param name="value">The list to serialize.</param>
	/// <param name="formatter">The formatter to use when serializing the items in the list; possibly null.</param>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
	protected void Serialize(Document document, Node node, List value, Formatter formatter, References references, Type type)
	{	
		
		Type elementType = Object.class;			
		
	elementType = type;		
		
		int length = value.size();

		for (int i = 0; i < length; i++)
		{
			if (value.get(i) == null)
				continue;
			Node childNode = document.createElement(XmlConstants.ITEM_ID);
            Serialize(document, childNode, SerializationUtilities.RequireType(elementType, value.get(i).getClass()), value.get(i), formatter, references);

			node.appendChild(childNode);
		}		
	}

	/// <summary>
	/// Serialize the dictionary into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the dictionary into.</param>
	/// <param name="value">The dictionary to serialize.</param>
	/// <param name="formatter">The formatter to use when serializing the items in the list; possibly null.</param>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
	protected void Serialize(Document document, Node node, Dictionary value, Formatter formatter, References references)
	{
		AddTypeAttribute(document, node, value.getClass());	

		Type keyType = Object.class;
		Type valueType = Object.class;

		Type[] genericKeyArgs = value.keys().getClass().getGenericInterfaces();
		Type[] genericValueArgs = value.elements().getClass().getGenericInterfaces();

		if (genericKeyArgs != null)
			keyType = genericKeyArgs[0];
		if (genericValueArgs != null)
			valueType = genericValueArgs[1];

        Formatter keyFormatter = null;
        Formatter valueFormatter = null;
        if (formatter.getClass().equals(KeyValueFormatter.class))
        {
            keyFormatter = ((KeyValueFormatter)formatter).getKeyFormatter();
            valueFormatter = ((KeyValueFormatter)formatter).getValueFormatter();
        }

		while(value.keys().hasMoreElements())
		{
			Object key = value.keys().nextElement();
			Node entryNode = document.createElement( XmlConstants.ENTRY_ID);			

			Node keyNode = document.createElement(XmlConstants.ENTRY_KEY_ID);
            Serialize(document, keyNode, SerializationUtilities.RequireType(keyType, key.getClass()), key, keyFormatter, references);
			

			Node valueNode = document.createElement(XmlConstants.ENTRY_VALUE_ID);
            Serialize(document, valueNode, SerializationUtilities.RequireType(valueType, value.get(key).getClass()), value.get(key), valueFormatter, references);			

			entryNode.appendChild(keyNode);
			entryNode.appendChild(valueNode);
			node.appendChild(entryNode);
		}
	}

	/// <summary>
	/// Serialize the array into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the array into.</param>
	/// <param name="value">The array to serialize.</param>
	/// <param name="formatter">The formatter to use when serializing the items in the array, possibly null.</param>
	/// <param name="references">The references Object that allows for serialization by reference.</param>
	@Test
	public void testRank()
	{
		
		Object sampleArray = Array.newInstance(String.class,1, 5,5,6,7,1,4);
		int dim = Rank(sampleArray, 0);
		Assert.assertEquals(7,dim);
		
		int[] intArray = (int[]) Array.newInstance(int.class, 3);
		dim = Rank(intArray, 0);
		Array.set(intArray, 0, 123);
		Array.set(intArray, 1, 456);
		Array.set(intArray, 2, 456);
		Assert.assertEquals(1,dim);
		
	}
	
	int Rank(Object value, int rank)
	{	 					
		try{
		 value = Array.get(value, 0);		 
		 return Rank(value, ++rank);
		}catch(Exception e)
		{
			return rank;
		}
	}
		
//		int rank = 0;
//		try{
//		while(true)
//		{
//			//Object val = Array.setLong(value, index, l);
//			val = Array.get(value, rank);
//			if(val != null && val.equals(new Integer(Integer.MAX_VALUE)) )
//				break;
//			rank++;
//		}
//		return rank;
//		}catch(Exception e)
//			{
//				return rank;
//			}
//				
//		}
		
	
	protected void Serialize(Document document, Node node, Object value, Formatter formatter, References references)
	{		
		AddTypeAttribute(document, node, value.getClass());		

		Type elementType = value.getClass();//.GetElementType();

		StringBuilder strRanks = new StringBuilder();
		for (int i = 0; i < Rank(value,0); i++)
		{
			if (i != 0)
				strRanks.append(", ");
			Object val = Array.get(value, i);
			strRanks.append(Array.getLength(value));
		}
		if(strRanks.toString().equals(""))
			strRanks.append(0);
		AddAttribute(document, node, XmlConstants.SIZE_ATTRIB_ID, strRanks.toString());
		SerializeArrayElement(document, node, elementType, formatter, references, value, 0, new int[] {});		
	}

	/// <summary>
	/// Recursively serialize array elements.
	/// </summary>
	/// <param name="document">The document to serialize into.</param>
	/// <param name="node">The parent node.</param>
	/// <param name="elementType">The type of the element.</param>
	/// <param name="formatter">The formatter to use for elements in the array.</param>
	/// <param name="references">Objects that have already been serialized.</param>
	/// <param name="value">The array to serialize.</param>
	/// <param name="curRank">The current rank.</param>
	/// <param name="indices">The set of indices for the current element.</param>
	private void SerializeArrayElement(Document document, Node node, Type elementType, Formatter formatter, References references, Object value, int curRank, int... indices)
	{
		int rank = Rank(value,0);
		if (curRank == rank)
		{
			Object childValue = null;
			try{
			childValue = Array.get(value, indices[0]);// value.GetValue(indices);
			}catch(Exception e){ _logger.Debug(e.getStackTrace().toString()); }
			if (childValue == null)
				return;

			Node childNode = document.createElement(XmlConstants.ITEM_ID);

			StringBuilder strIndices = new StringBuilder();
			for (int i = 0; i < rank; i++)
			{
				if (i != 0)
					strIndices.append(", ");
				strIndices.append(indices[i]);
			}

			AddAttribute(document, childNode, XmlConstants.INDEX_ATTRIB_ID, strIndices.toString());
            Serialize(document, childNode, SerializationUtilities.RequireType(elementType, childValue.getClass()), childValue,
					  formatter, references);

			node.appendChild(childNode);
		}
		else
		{
			int[] extIndices = new int[curRank+1];
			
			Copy(indices, extIndices, curRank);
			Object array  = Array.get(value,curRank);
			
			int length = Array.getLength(value);//array);
			for (int i = 0; i < length; i++)
			{
				extIndices[curRank] = i;
				SerializeArrayElement(document, node, elementType, formatter, references, value, curRank + 1, extIndices);
			}
		}
	}

	void Copy(int[] src, int[] dst, int length)
	{
		for(int i=0; i<length; i++)
			dst[i] = src[i];	
	}
	/// <summary>
	/// Serialize the String into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the String into.</param>
	/// <param name="value">The String to serialize.</param>
	protected void Serialize(Document document, Node node, String value)
	{
		if (value == null)
			value = "";

		
			
		Document xmlDoc = XmlStringEncoder.ToXmlDocument(value);
		if (xmlDoc != null)
		{
			AddAttribute(document, node, XmlConstants.ENCODING_ID, XmlConstants.XML_STRING_ENCODING_ID);
			Node childNode = document.importNode(xmlDoc.getFirstChild(), true);
			node.appendChild(childNode);
		}
		else
		{
			if (!value.equals("null")){
			AddAttribute(document, node, XmlConstants.ENCODING_ID, XmlConstants.W3C_STRING_ENCODING_ID);
			AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, XmlStringEncoder.W3CEncodeString(value));
			}else
			{
				AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, XmlStringEncoder.W3CEncodeString(value));
			}
		}
	}

	/// <summary>
	/// Serialize the enum into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the enum into.</param>
	/// <param name="value">The enum to serialize.</param>
	protected void Serialize(Document document, Node node, Enum value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value.toString());
	}

	/// <summary>
	/// Serialize the bool into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the bool into.</param>
	/// <param name="value">The bool to serialize.</param>
	 protected void Serialize(Document document, Node node, boolean value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value + "");
	}

	/// <summary>
	/// Serialize the byte into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the byte into.</param>
	/// <param name="value">The byte to serialize.</param>
	protected void Serialize(Document document, Node node, byte value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	/// <summary>
	/// Serialize the sbyte into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the sbyte into.</param>
	/// <param name="value">The sbyte to serialize.</param>
	

	/// <summary>
	/// Serialize the char into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the char into.</param>
	/// <param name="value">The char to serialize.</param>
	protected void Serialize(Document document, Node node, char value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	/// <summary>
	/// Serialize the decimal into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the decimal into.</param>
	/// <param name="value">The decimal to serialize.</param>
	

	/// <summary>
	/// Serialize the double into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the double into.</param>
	/// <param name="value">The double to serialize.</param>
	protected void Serialize(Document document, Node node, double value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	/// <summary>
	/// Serialize the float into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the float into.</param>
	/// <param name="value">The float to serialize.</param>
	protected void Serialize(Document document, Node node, float value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	/// <summary>
	/// Serialize the int into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the int into.</param>
	/// <param name="value">The int to serialize.</param>
	protected void Serialize(Document document, Node node, int value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	/// <summary>
	/// Serialize the uint into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the uint into.</param>
	/// <param name="value">The uint to serialize.</param>
	

	/// <summary>
	/// Serialize the long into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the long into.</param>
	/// <param name="value">The long to serialize.</param>
	protected void Serialize(Document document, Node node, long value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	protected void Serialize(Document document, Node node, Long value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	/// <summary>
	/// Serialize the ulong into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the ulong into.</param>
	/// <param name="value">The ulong to serialize.</param>
	

	/// <summary>
	/// Serialize the short into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the short into.</param>
	/// <param name="value">The short to serialize.</param>
	protected void Serialize(Document document, Node node, short value)
	{
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, value+"");
	}

	/// <summary>
	/// Serialize the ushort into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the ushort into.</param>
	/// <param name="value">The ushort to serialize.</param>
	

	/// <summary>
	/// Serialize the Type into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the Type into.</param>
	/// <param name="value">The Type to serialize.</param>
	protected void Serialize(Document document, Node node, Type value)
	{
		
		String qualifiedName = _typeChange.get(value);
		AddAttribute(document, node, XmlConstants.VALUE_ATTRIB_ID, qualifiedName);//AssemblyQualifiedName);
	}

	/// <summary>
	/// Add an attribute to the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to add the attribute to.</param>
	/// <param name="name">The name of the attribute.</param>
	/// <param name="value">The value of the attribute.</param>
	protected static void AddAttribute(Document document, Node node, String name, String value)
	{
		Attr attribute = document.createAttribute(name);
		attribute.setValue(value);
		node.getAttributes().setNamedItem(attribute);
	}

	/// <summary>
	/// Add a type attribute to the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to add the attribute to.</param>
	/// <param name="type">The type to be added as an attribute.</param>
	protected void AddTypeAttribute(Document document, Node node, Type type)
	{
		int a = 2;
		if(type.toString().contains("Shared"))
			a = 4;
		Attr attribute = document.createAttribute(XmlConstants.TYPE_ATTRIB_ID);
		if (_typeChange.containsKey(type))
			attribute.setValue(_typeChange.get(type));//[type]);
		else
			attribute.setValue(FormatType(type));//AssemblyQualifiedName);
		Element element = (Element)node;
		element.setAttribute(attribute.getName(), attribute.getValue());
		node = element;
		
		//node.getAttributes().setNamedItem(attribute);// Append(attribute);
	}

	private String FormatType(Type type)
	{
		String temp_type = ((Class)type).getName();
		return temp_type;
	}
	
	static private boolean SerializeReference(Document document, Node node, Object value, References references)
	{
		if (!value.getClass().isMemberClass())//IsClass)
			return false;

		if (references.containsKey(value))
		{
			String objectId = AddObjectIdAttribute(document, references.get(value), references);
			AddAttribute(document, node, XmlConstants.REFERENCE_ID, objectId);
			return true;
		}
		else
		{
			references.put(value, node);
			return false;
		}
	}

	static private String AddObjectIdAttribute(Document document, Node node, References references)
	{
		Node attribute = node.getAttributes().getNamedItem(XmlConstants.OBJECT_UID);// Attributes[XmlConstants.OBJECT_UID];
		if (attribute != null)
			return attribute.getTextContent(); //getValue();

		String objectId = references.NextReference();
		AddAttribute(document, node, XmlConstants.OBJECT_UID, objectId);
		return objectId;
	}		
}