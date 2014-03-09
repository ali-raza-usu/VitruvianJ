package vitruvianJ.communication.session.sockets;

import java.lang.reflect.Type;
import java.util.List;

import org.w3c.dom.Document;


import vitruvianJ.ByteList;
import vitruvianJ.core.ClassFactory;
import vitruvianJ.serialization.IEncoder;
import vitruvianJ.serialization.xml.XmlDeserializer;
import vitruvianJ.serialization.xml.XmlFramework;
import vitruvianJ.serialization.xml.XmlSerializer;
import vitruvianJ.serialization.xml.XmlStringEncoder;


public class XmlEncoder implements IEncoder
{
    private List<Class> _types = null;

    private XmlDeserializer deserializer = new XmlDeserializer();
    private XmlSerializer serializer = new XmlSerializer();
    private ByteList byteList = new ByteList();

    public XmlEncoder()
    {
    	
        _types = ClassFactory.GetAllTypes();
    	//serializer.getTypeChange().put(Chat, arg1)
    	
    }

   

    /// <summary>
    /// Convert the bytes to an object.
    /// </summary>
    /// <param name="bytes">The bytes that contain the object.</param>
    /// <param name="offset"></param>
    /// <param name="length"></param>
    /// <returns>The object converted from the bytes.</returns>
   

    /// <summary>
    /// Convert the object to bytes.
    /// </summary>
    /// <param name="value">The object to convert to bytes.</param>
    /// <returns>The bytes that contain the object.</returns>
   

   
    public Object clone()
    {
        return new XmlEncoder();
    }

	@Override
	public byte[] ToBytes(Object value) {
		 Document doc = XmlFramework.Serialize(value);// serializer.Serialize(value);	
		 String string = XmlFramework.SerializeToString(doc);
		 return string.getBytes();	        
	}

	
	public Object ToObject(byte[] bytes, int offset, int length)
	{
		 if (offset != 0 || length != bytes.length)
	        {
	            byte[] tmpBytes = new byte[length];
	            byteList.BlockCopy(byteList.getArrayObject(bytes), offset, byteList.getArrayObject(tmpBytes), 0, length);	            
	            bytes = tmpBytes;
	        }

	        
	        String string = new String (bytes);
	        Document doc =  XmlStringEncoder.ToXmlDocument(string);
	        return deserializer.Deserialize(doc);
	}



	@Override
	public Object ToObject(byte[] bytes) {
		// TODO Auto-generated method stub
		return null;
	}

}
