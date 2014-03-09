package vitruvianJ.serialization.xml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;


/// <summary>
/// Serialize objects into simple xml using reflection and the SimpleXml tag.
/// </summary>
public class SimpleXmlSerializer {
	/// <summary>
	/// Serialize an object into an XmlDocument.
	/// </summary>
	/// <param name="value">The object to serialize.</param>
	/// <returns>The xml document representing the object.</returns>
	public Document Serialize(Object value)
	{
		try{
			SimpleXmlTypeSerializationInfo reflectionInfo = SimpleXmlSerializationUtilities.getReflectionInfo(value.getClass());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document document = dbf.newDocumentBuilder().newDocument();
			Node root = document.createElement(reflectionInfo.NodeName);

			Serialize(document, root, value);
			document.appendChild(root);
			return document;
		}catch(ParserConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/// <summary>
	/// Serialize the object into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the object into.</param>
	/// <param name="type">The type of the object being serialized.</param>
	/// <param name="value">The object to serialize.</param>
	protected void Serialize(Document document, Node node, Object value)
	{
		if (value == null) return;

		Class<?> type = value.getClass();

		if (type.equals(String.class))
		{
			node.setTextContent(XmlStringEncoder.W3CEncodeString(value.toString()));
		}
		else if (type.isEnum())
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(boolean.class) || type.equals(Boolean.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(byte.class) || type.equals(Byte.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(char.class) || type.equals(Character.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(double.class) || type.equals(Double.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(float.class) || type.equals(Float.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(int.class) || type.equals(Integer.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(long.class) || type.equals(Long.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(short.class) || type.equals(Short.class))
		{
			node.setTextContent(value.toString());
		}
		else if (type.equals(Date.class))
		{
			// HH = 24 hour time
			node.setTextContent(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format((Date)value));
		}
		else if ((List.class).isAssignableFrom(type))
		{
			SerializeList(document, node, (List)value);//serialize list types
		}
		else
		{
			SerializeObject(document, node, value);//serialize other non system types
		}
	}

	/// <summary>
	/// Serialize the object into the node using reflection.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the object into.</param>
	/// <param name="value">The object to serialize.</param>
	protected void SerializeObject(Document document, Node node, Object value)
	{
		SimpleXmlTypeSerializationInfo reflectionInfo = SimpleXmlSerializationUtilities.getReflectionInfo(value.getClass());

		Object item = null;

		for (SimpleXmlPropertySerializationInfo pInfo : reflectionInfo.Properties)
		{
			if(pInfo.Info.getName().contains("get"))//the accessor method name must contain "get" to be serialized
			{
				try {
					item = pInfo.Info.invoke(value, null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

				Node childNode = document.createElement(pInfo.NodeName);
				Serialize(document, childNode, item);
				node.appendChild(childNode);
			}
		}

		for (SimpleXmlFieldSerializationInfo fInfo : reflectionInfo.Fields)
		{
			try {
				item = fInfo.Info.get(value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Node childNode = document.createElement(fInfo.NodeName);
			Serialize(document, childNode, item);
			node.appendChild(childNode);
		}
	}

	/// <summary>
	/// Serialize the list into the node.
	/// </summary>
	/// <param name="document">The document containing the node.</param>
	/// <param name="node">The node to serialize the list into.</param>
	/// <param name="value">The list to serialize.</param>
	protected void SerializeList(Document document, Node node, List value)
	{
		int length = value.size();

		for (int i = 0; i < length; i++)
		{
			SimpleXmlTypeSerializationInfo reflectionInfo = SimpleXmlSerializationUtilities.getReflectionInfo(value.get(i).getClass());
			Node childNode = document.createElement(reflectionInfo.NodeName);
			Serialize(document, childNode, value.get(i));

			node.appendChild(childNode);
		}
	}
}
