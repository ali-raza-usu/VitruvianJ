package vitruvianJ.distribution.encoders;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.*;

import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;





import vitruvianJ.serialization.*;
import vitruvianJ.binary.BinaryDeserializer;
import vitruvianJ.communication.Heartbeat;
import vitruvianJ.communication.session.Message;
import vitruvianJ.distribution.ReplyInitializeObject;
import vitruvianJ.distribution.gossip.ReplicationManager;
import vitruvianJ.distribution.sessions.messages.ReplyBrokerId;
import vitruvianJ.distribution.sessions.messages.ReplyExecuteMethod;
import vitruvianJ.distribution.sessions.messages.ReplyExecuteSyncPatternMethod;
import vitruvianJ.distribution.sessions.messages.ReplyKnownObjectIds;
import vitruvianJ.distribution.sessions.messages.ReplyServices;
import vitruvianJ.distribution.sessions.messages.RequestAddService;
import vitruvianJ.distribution.sessions.messages.RequestBrokerId;
import vitruvianJ.distribution.sessions.messages.RequestExecuteMethod;
import vitruvianJ.distribution.sessions.messages.RequestExecuteSyncPatternMethod;
import vitruvianJ.distribution.sessions.messages.RequestInitializeObject;
import vitruvianJ.distribution.sessions.messages.RequestKnownObjectIds;
import vitruvianJ.distribution.sessions.messages.RequestRemoveService;
import vitruvianJ.distribution.sessions.messages.RequestServices;
import vitruvianJ.distribution.syncpatterns.fragments.PropertyChangedEventArgs;
import vitruvianJ.logging.*;
//using System.IO;
import vitruvianJ.serialization.formatters.*;
import vitruvianJ.serialization.formatters.Formatter;
import vitruvianJ.serialization.xml.*;

	/// <summary>
	/// An encoder that switches object markers for proxies, and proxies for object markers.
	/// </summary>
	public class XmlObjectEncoder extends ObjectEncoder
	{
        private JLogger _logger = new JLogger(XmlObjectEncoder.class);

        protected static XmlObjectSerializer _serializer = new XmlObjectSerializer();
        protected static XmlObjectDeserializer _deserializer = new XmlObjectDeserializer();

        private FormatterMap<Type, Formatter> _formatters = new FormatterMap<Type, Formatter>();
        private String _formattersContentPath = "";

        private boolean _strongTyped = false;
        private boolean _compress = false;

        private HashMap<String, Type> _typeChange = new HashMap<String, Type>();
        
        /// <summary>
        /// Flag indicating if types should be serialized
        /// with fully qualified assembly names.
        /// </summary>
        
        public XmlObjectEncoder()
        {
        	//serializer.getTypeChange().put(ChatMessage.class, "ChatMessage");
        	//deserializer.getTypeChange().put("ChatMessage", ChatMessage.class);
        	
        	_serializer.getTypeChange().put(Heartbeat.class, "Heartbeat");
        	_deserializer.getTypeChange().put("Heartbeat", Heartbeat.class);
        	
        	_serializer.getTypeChange().put(RequestBrokerId.class, "RequestBrokerId");
        	_deserializer.getTypeChange().put("RequestBrokerId", RequestBrokerId.class);
        	
        	_serializer.getTypeChange().put(ReplyBrokerId.class, "ReplyBrokerId");
        	_deserializer.getTypeChange().put("ReplyBrokerId", ReplyBrokerId.class);
        	
        	_serializer.getTypeChange().put(ReplyExecuteMethod.class, "ReplyExecuteMethod");
        	_deserializer.getTypeChange().put("ReplyExecuteMethod", ReplyExecuteMethod.class);
        	
        	_serializer.getTypeChange().put(ReplyExecuteSyncPatternMethod.class, "ReplyExecuteSyncPatternMethod");
        	_deserializer.getTypeChange().put("ReplyExecuteSyncPatternMethod", ReplyExecuteSyncPatternMethod.class);
        	
        	_serializer.getTypeChange().put(ReplyInitializeObject.class, "ReplyInitializeObject");
        	_deserializer.getTypeChange().put("ReplyInitializeObject", ReplyInitializeObject.class);
        	
        	_serializer.getTypeChange().put(ReplyKnownObjectIds.class, "ReplyKnownObjectIds");
        	_deserializer.getTypeChange().put("ReplyKnownObjectIds", ReplyKnownObjectIds.class);
        	
        	_serializer.getTypeChange().put(ReplyServices.class, "ReplyServices");
        	_deserializer.getTypeChange().put("ReplyServices", ReplyServices.class);
        	
        	_serializer.getTypeChange().put(RequestAddService.class, "RequestAddService");
        	_deserializer.getTypeChange().put("RequestAddService", RequestAddService.class);
        	
        	_serializer.getTypeChange().put(RequestExecuteMethod.class, "RequestExecuteMethod");
        	_deserializer.getTypeChange().put("RequestExecuteMethod", RequestExecuteMethod.class);
        	
        	_serializer.getTypeChange().put(RequestExecuteSyncPatternMethod.class, "RequestExecuteSyncPatternMethod");
        	_deserializer.getTypeChange().put("RequestExecuteSyncPatternMethod", RequestExecuteSyncPatternMethod.class);
        	
        	_serializer.getTypeChange().put(RequestInitializeObject.class, "RequestInitializeObject");
        	_deserializer.getTypeChange().put("RequestInitializeObject", RequestInitializeObject.class);
        	
        	_serializer.getTypeChange().put(RequestKnownObjectIds.class, "RequestKnownObjectIds");
        	_deserializer.getTypeChange().put("RequestKnownObjectIds", RequestKnownObjectIds.class);
        	
        	_serializer.getTypeChange().put(ReplyExecuteSyncPatternMethod.class, "ReplyExecuteSyncPatternMethod");
        	_deserializer.getTypeChange().put("ReplyExecuteSyncPatternMethod", ReplyExecuteSyncPatternMethod.class);
        	
        	_serializer.getTypeChange().put(RequestRemoveService.class, "RequestRemoveService");
        	_deserializer.getTypeChange().put("RequestRemoveService", RequestRemoveService.class);
        	
        	_serializer.getTypeChange().put(RequestServices.class, "RequestServices");
        	_deserializer.getTypeChange().put("RequestServices", RequestServices.class);
        	
        	_serializer.getTypeChange().put(ReplicationManager.class, "ReplicationManager");
        	_deserializer.getTypeChange().put("ReplicationManager", ReplicationManager.class);
        	
        	_serializer.getTypeChange().put(ObjectMarker.class, "ObjectMarker");
        	_deserializer.getTypeChange().put("ObjectMarker", ObjectMarker.class);
        	
        	_serializer.getTypeChange().put(ObjectMarker.class, "ObjectMarker");
        	_deserializer.getTypeChange().put("ObjectMarker", ObjectMarker.class);
        	
        	_serializer.getTypeChange().put(PropertyChangedEventArgs.class, "PropertyChangedEventArgs");
        	_deserializer.getTypeChange().put("PropertyChangedEventArgs", PropertyChangedEventArgs.class);
        	
        	
        	
        	_serializer.getTypeChange().put(java.lang.Class.class, "Type");
        	_deserializer.getTypeChange().put("Type", java.lang.Class.class);
        	
        	
        }
        @Serialize//(getName = "get")
        public boolean getStrongTyped()
        {
            return _strongTyped; 
        }
        
        @Serialize//(getName = "set")
        public void setStrongTyped(boolean value) 
        { 
        	_strongTyped = value;
        }

        /// <summary>
        /// Flag indicating
        /// </summary>
        @Serialize //(getName = "get")
        public boolean getCompress()
        {
            return _compress; 
        }
        
        @Serialize //(getName = "set")        
        public void setCompress(boolean value) 
        { 
        	_compress = value;
        }

        /// <summary>
        /// The dictionary is used for type changes.
        /// The dictionary is used toet
        /// <list>
        /// <item>standardize serialization across platforms</item>
        /// <item>standardize serialization across applications</item>
        /// <item>backwards compatibility on type or namespace changes</item>
        /// </list>
        /// <remarks>Key = name to use in serialization</remarks>
        /// <remarks>Value = type to change for the name</remarks>
        /// </summary>
        @Serialize //(getName = "get")
        public HashMap<String, Type> getTypeChange()
        {
            return _typeChange; 
        }
            
        @Serialize //(getName = "set")
        public void setTypeChange(HashMap<String, Type> value) 
        { 
        	_typeChange = value; 
        }
        
        @Serialize// (getName = "get")
        public FormatterMap getFormatters()
        {
            return _formatters; 
        }

        
        public void Init()
        {
            if (!_formattersContentPath.equals(""))
				try {
					_formatters = new FormatterMap(_formattersContentPath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            Set<Entry<String, Type>>   _typeChangesSet =  _typeChange.entrySet();
            for(Entry<String, Type> value : _typeChangesSet)
                _serializer.getTypeChange().put(value.getValue(), value.getKey());
            _serializer.setStrongTyped(_strongTyped);
            _deserializer.setTypeChange(_typeChange);

            _serializer.setFormatters(_formatters);
            _deserializer.setFormatters(_formatters);
        }

        public void Cleanup()
        {
            _formatters.clear();
        }

        /// <summary>
        /// The persistence id locating the formatters.
        /// </summary>
        @Serialize//(getName = "get")
        public String getFormattersContentPath()
        {
            return _formattersContentPath; 
        }
        @Serialize//(getName = "set")    
        public void setFormattersContentPath(String value) 
        { 
        	_formattersContentPath = value; 
        }

        

        /// <summary>
        /// Convert the bytes to an object.
        /// </summary>
        /// <param name="bytes">The bytes that contain the object.</param>
        /// <param name="offset"></param>
        /// <param name="length"></param>
        /// <returns>The object converted from the bytes.</returns>
        public Object ToObject(byte[] bytes, int offset, int length)
        {
        	try {
            _serializer.setBrokerId(getBrokerId());
            _deserializer.setBrokerId(getBrokerId());

            if (offset != 0 || length != bytes.length)
            {
                byte[] tmpBytes = new byte[length];
                System.arraycopy(bytes, offset, tmpBytes, 0, length);
                bytes = tmpBytes;
            }

            if (_compress)
                bytes = Compressor.Decompress(bytes);

            DocumentBuilder docBuilder;
            Document document;
			
				docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				String str = new String(bytes);
	            document = docBuilder.parse(new InputSource(new StringReader(str)));
			
            
            

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("ToObject:: %1s bytes\r\n %2s", bytes.length, null);//FormatXml(doc.OuterXml));

            Object result = _deserializer.Deserialize(document);  
            Message msg = (Message)result;
        	if(msg.getMessageTypeId() == 9)
        		msg = null;
            return result;
        	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

        }

        /// <summary>
        /// Convert the object to bytes.
        /// </summary>
        /// <param name="value">The object to convert to bytes.</param>
        /// <returns>The bytes that contain the object.</returns>
        public byte[] ToBytes(Object value)
        {  
        	Message msg = (Message)value;
        	if(msg.getMessageTypeId() == 7){
        		RequestExecuteMethod r_msg	=(RequestExecuteMethod)msg;
        		int a =4;
        	}
            _serializer.setBrokerId(getBrokerId());
            _deserializer.setBrokerId(getBrokerId());
            Document doc = null;        	
            doc = _serializer.Serialize(value);            
            String string = XmlFramework.SerializeToString(doc);
            try{
            _logger.Debug(" ToBytes : \n" + format(string));
            }catch(Exception ex)
            {
            	ex.printStackTrace();
            }
   		    byte[] bytes =  string.getBytes();	 
            if (_compress)
                bytes = Compressor.Compress(bytes);
            return bytes;
        }

        @Override
    	public Object ToObject(byte[] bytes) {
            String string = new String (bytes);
            String[] str = string.split("</object>");
            string = str[0]+"</object>"; 
            _logger.Debug("To Object : \n " + format(string));
            

           
            Document doc = null;
            Object obj = null;
    		try {			
    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			InputSource is = new InputSource( new StringReader(string ) );                
    	        doc = builder.parse( is );    	        
    	         obj = _deserializer.Deserialize(doc, null);    	         
    	         Message msg = (Message)obj;
    	         if(msg.getMessageTypeId() == 9)
    	         {    	        	
    	        	// obj = _deserializer.Deserialize(doc, null);    
    	        	 msg = msg;    	        	
    	         }
    	        
    		} catch (Exception e) {
    			e.printStackTrace();
    			_deserializer.Deserialize(doc, null);
    		}                       
            return obj;
            
    	}


            public String format(String unformattedXml) {
                try {
                    final Document document = parseXmlFile(unformattedXml);

//                    OutputFormat format = new OutputFormat(document);
//                    format.setLineWidth(65);
//                    format.setIndenting(true);
//                    format.setIndent(2);
//                    Writer out = new StringWriter();
//                    XMLSerializer serializer = new XMLSerializer(out, format);
//                    serializer.serialize(document);
//
//                    return out.toString();
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            private Document parseXmlFile(String in) {
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(in));
                    return db.parse(is);
                } catch (ParserConfigurationException e) {
                    throw new RuntimeException(e);
                } catch (SAXException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        private String FormatXml(String xml)
        {
        	try{
            String outXml = "";
//            MemoryStream ms = new MemoryStream();
//            // Create a XMLTextWriter that will send its output to a memory stream (file)
//            XmlTextWriter xtw = new XmlTextWriter(ms, Encoding.Unicode);
//            XmlDocument doc = new XmlDocument();
//
//            try
//            {
//                // Load the unformatted XML text string into an instance
//                // of the XML Document Object Model (DOM)
//                doc.LoadXml(xml);
//
//                // Set the formatting property of the XML Text Writer to indented
//                // the text writer is where the indenting will be performed
//                xtw.Formatting = Formatting.Indented;
//
//                // write dom xml to the xmltextwriter
//                doc.WriteContentTo(xtw);
//                // Flush the contents of the text writer
//                // to the memory stream, which is simply a memory file
//                xtw.Flush();
//
//                // set to start of the memory stream (file)
//                ms.Seek(0, SeekOrigin.Begin);
//                // create a reader to read the contents of
//                // the memory stream (file)
//                StreamReader sr = new StreamReader(ms);
//                // return the formatted string to caller
//                return sr.ReadToEnd();
            return null;
            }
            catch(Exception ex)
            {
                return ex.toString();
            }
        }

        public Object Clone()
        {
            XmlObjectEncoder clone = new XmlObjectEncoder();

            if (!getFormattersContentPath().equals(""))
            {
                clone.setFormattersContentPath(getFormattersContentPath());
            }
            else
            {
                for (Entry<Type, Formatter> item : _formatters.entrySet())
                {
                    clone._formatters.put(item.getKey(), item.getValue());
                }
            }

            clone.setStrongTyped(getStrongTyped());
            clone.setTypeChange(getTypeChange());
            return clone;
        }

		

		@Override
		public Object clone() {
			// TODO Auto-generated method stub
			return Clone();
		}

        
    }


