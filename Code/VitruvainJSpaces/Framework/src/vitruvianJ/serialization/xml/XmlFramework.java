package vitruvianJ.serialization.xml;
//aliraza
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import vitruvianJ.core.PathUtilities;
import vitruvianJ.serialization.SerializationUtilities;



public  class XmlFramework
{
	private static HashMap<String, Type> _deserializerTypeChange = new HashMap<String, Type>();
	private static HashMap<Type, String> _serializerTypeChange = new HashMap<Type, String>();

	/// <summary>
	/// Add a type change for serialization / deserialization.
	/// This helps to
	/// <list>
	/// <item>standardize serialization across platforms</item>
	/// <item>standardize serialization across applications</item>
	/// <item>backwards compatibility on type or namespace changes</item>
	/// </list>
	/// </summary>
	/// <param name="name">Name to use in serialization.</param>
	/// <param name="type">Type to change for the name.</param>
	public HashMap<Type, String> GetSerliazeTypeChange()
	{
		return _serializerTypeChange;
	}
	
	public HashMap<String, Type> GetDeserliazeTypeChange()
	{
		return _deserializerTypeChange;
	}
	
	
	public static void AddTypeChange(String name, Type type)
	{
		_serializerTypeChange.put(type, name);
		_deserializerTypeChange.put(name, type);
	}

	/// <summary>
	/// Serialize the object into an XmlDocument.
	/// </summary>
	/// <param name="value">The object to serialize.</param>
	/// <returns>A document representing the object.</returns>
	public static Document Serialize(Object value)
	{
		XmlSerializer serializer = new XmlSerializer();
		serializer.setTypeChange(_serializerTypeChange);
		return serializer.Serialize(value);
	}

	
	public static String SerializeObjectToString(Object value)
	{
		Document pointDoc = XmlFramework.Serialize(value);
        XmlFramework.SerializeToString(pointDoc);
        return XmlFramework.SerializeToString(pointDoc);	
	}
	
	
	public static String SerializeToString(Document value)
	{
		String str = null;
		try{			
		 DOMSource domSource = new DOMSource(value);
		 TransformerFactory tf = TransformerFactory.newInstance();
         Transformer m = tf.newTransformer();
         java.io.StringWriter sw = new java.io.StringWriter();
         StreamResult sr = new StreamResult(sw);
         m.transform(domSource, sr);
         str = sw.toString();
         return str;
		}catch(Exception e)
		{
			return str;
		}
		
	}

	public static Document DeserializeToDoc(String filename)
	{
		if (!new File(filename).exists())
			return null;

		PathUtilities.PushFilename(filename);

		try
		{
			DocumentBuilder doc_b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File file = new File(filename);
			
			 Document doc = doc_b.parse(file) ;
			 traverseNode(doc);			 
			 doc.normalize();
			return doc;
		}
		catch(Exception e){
			
			return null;
		}
		finally
		{
			PathUtilities.PopFilename(filename);
		}
	}

	public static void traverseNode(Node n)
	{
	
		 NodeList nodes =  n.getChildNodes();
		 for(int i =0; i<nodes.getLength(); i++)
		 {
			 Node temp_node = nodes.item(i);
			 if(temp_node.getNodeType() == temp_node.TEXT_NODE)
			 {
				 if(temp_node.getTextContent().trim().length() == 0)
				 {
					 temp_node.setTextContent("");					
				 }
			 }
			 traverseNode(temp_node);
		 }
	}
	
	/// <summary>
	/// Deserialize the object from the document.
	/// </summary>
	/// <param name="doc">The document representing the object.</param>
	/// <returns>The object represented by the document.</returns>
	public static Object Deserialize(Document doc)
	{
		XmlDeserializer deserializer = new XmlDeserializer();
		deserializer.setTypeChange(_deserializerTypeChange);
		return deserializer.Deserialize(doc);
	}

	/// <summary>
	/// Deserialize the document into the object.
	/// </summary>
	/// <param name="doc">The document representing the object.</param>
	/// <param name="value">The object to deserialize the document into.</param>
	public static void Deserialize(Document doc, Object value)
	{
		XmlDeserializer deserializer = new XmlDeserializer();
		deserializer.setTypeChange(_deserializerTypeChange);
		deserializer.Deserialize(doc, value);
	}

	

	/// <summary>
	/// Serialize the Object and save it to the file.
	/// </summary>
	/// <param name="filename">The xml file.</param>
	/// <param name="value">The Object to serialize.</param>
	public static void Serialize(String filename, Object value)
	{
		PathUtilities.PushFilename(filename);

		try
		{
			Document doc = Serialize(value);			
			Save(filename,doc);
		}
		finally
		{
			PathUtilities.PopFilename(filename);
		}
	}

	public static boolean Save(String fileName, Document doc) {
		
		         File xmlOutputFile = new File(fileName);
		         FileOutputStream fos;
		         Transformer transformer;
		         try {
		             fos = new FileOutputStream(xmlOutputFile);
		         }
		         catch (FileNotFoundException e) {		
		             return false;
		         }
		
		         TransformerFactory transformerFactory = TransformerFactory.newInstance();
		         try {
		             transformer = transformerFactory.newTransformer();
		         }
		         catch (TransformerConfigurationException e) {		
		             return false;
		         }
		         DOMSource source = new DOMSource(doc);
		         StreamResult result = new StreamResult(fos);		
		         try {
		             transformer.transform(source, result);
		         }
		         catch (TransformerException e) {		
		}		
		return true;
		}

	/// <summary>
	/// Deserialize an Object from the file.
	/// </summary>
	/// <param name="filename">The xml file.</param>
	/// <returns>The Object that was deserialized.</returns>
	public static Object Deserialize(String filename)
	{
		if (!new File(filename).exists())
			return null;

		PathUtilities.PushFilename(filename);

		try
		{
			DocumentBuilder doc_b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File file = new File(filename);
			
			 Document doc = doc_b.parse(file) ;
			return Deserialize(doc);
		}
		catch(Exception e){
			
			return null;
		}
		finally
		{
			PathUtilities.PopFilename(filename);
		}
	}

	/// <summary>
	/// Deserialize the file into the Object.
	/// </summary>
	/// <param name="filename">The xml file.</param>
	/// <param name="value">The Object to deserialize into.</param>
	
	
	
	public static void Deserialize(String filename, Object value)
	{
		
		if (!new File(filename).exists())
			return;

		PathUtilities.PushFilename(filename);

		try
		{
			DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
			doc_factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder doc_b = doc_factory.newDocumentBuilder();
	
			File file = new File(filename);
			
			 Document doc = doc_b.parse(file) ;
			
			doc.getDocumentElement().normalize();
			Deserialize(doc, value);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		finally
		{
			PathUtilities.PopFilename(filename);
		}
	}


	/// <summary>
	/// Deserialize an Object from the xml fragment.
	/// </summary>
	/// <param name="xml">The xml in a String.</param>
	/// <returns>The deserialized Object.</returns>
	public static Object DeserializeXml(String xml)
	{
		try{
		DocumentBuilder doc_b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				
		 Document doc = doc_b.parse(xml) ;
		return Deserialize(doc);
		}catch(Exception e ){
			return null;
		}
	}

	/// <summary>
	/// Deserialize the xml fragment into the Object.
	/// </summary>
	/// <param name="xml"></param>
	/// <param name="value"></param>
	public static void DeserializeXml(String xml, Object value)
	{
		try{
		DocumentBuilder doc_b = DocumentBuilderFactory.newInstance().newDocumentBuilder();		
		 Document doc = doc_b.parse(xml) ;		
		Deserialize(doc, value);
		}catch(Exception e){}
	}


	/// <summary>
	/// Deserialize the embedded xml into the Object.
	/// </summary>
	/// <param name="type">The type that the resource name is relative to.</param>
	/// <param name="resource">The name of the resource.</param>
	/// <param name="value">The Object to deserialize into.</param>
	public static void Deserialize(Type type, String resource, Object value)
	{
		Document doc = GetEmbeddedXml(type, resource);
		Deserialize(doc, value);
	}

	/// <summary>
	/// Deserialize the embedded xml.
	/// </summary>
	/// <param name="type">The type that the resource name is relative to.</param>
	/// <param name="resource">The name of the resource.</param>
	/// <returns>The deserialized Object.</returns>
	public static Object Deserialize(Type type, String resource)
	{
		Document doc = GetEmbeddedXml(type, resource);
		return Deserialize(doc);
	}

	private static Document GetEmbeddedXml(Type type, String resource)
	{
		try{	
		//Stream s SerializationUtilities.GetEmbeddedFile(type, resource);	
		
		{
			
			Document doc = DeserializeToDoc(PathUtilities.GetAbsolutePath(resource));			
			 return doc;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//using (Stream stream = SerializationUtilities.GetEmbeddedFile(type, resource))
		
		
		return null;//new DocumentImpl();
	}


}