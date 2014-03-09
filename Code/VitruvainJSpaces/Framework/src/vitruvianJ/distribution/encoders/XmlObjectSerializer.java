package vitruvianJ.distribution.encoders;

import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;

import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.proxies.ProxyUtilities;
import vitruvianJ.distribution.proxies.ReflectionInfo;
import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.formatters.Formatter;
import vitruvianJ.serialization.xml.XmlConstants;
import vitruvianJ.serialization.xml.XmlSerializer;

/// </summary>
public class XmlObjectSerializer extends XmlSerializer
{
    private final String INITIAL_STATE = "Initial-State";

    private JGUID _brokerId = null;
    private HashMap<Type, Formatter> _formatters = null;

    /// <summary>
    /// The id of the broker using this session.
    /// </summary>
    public JGUID getBrokerId()
    {
        return _brokerId; 
    }
     public void setBrokerId(JGUID value) 
     { 
    	 _brokerId = value; 
    }
    
    /// <summary>
    /// Formatters used to deserialize objects contained in an ObjectMarker.Value property.
    /// </summary>
   // [DontSerialize]
    public HashMap<Type, Formatter> getFormatters()
    {
        return _formatters; 
    }
    
    
   

    public void setFormatters(HashMap<Type, Formatter> value) 
    { 
    	_formatters = value;
    }
    
    public Document Serialize(Object value)
    {
        Formatter formatter = null;

        if (value != null)
        {
            if (_formatters != null)
            {
                Type type = value.getClass();
                if (_formatters.get(type)!=null)
                    formatter = _formatters.get(type);
            }
        }

        return super.Serialize(value, formatter);
    }

    /// <summary>
    /// Serialize the object into the node.
    /// Exchange proxy types for the correct type.
    /// </summary>
    /// <param name="document">The document containing the node.</param>
    /// <param name="node">The node to serialize the object into.</param>
    /// <param name="requireType">Indication if the type of the object needs to be added as an attribute to the node.</param>
    /// <param name="type">The type of the object being serialized.</param>
    /// <param name="value">The object to serialize.</param>
    /// <param name="formatter">The formatter to use when serializing the object; possibly null.</param>
    /// <param name="references">The references object that allows for serialization by reference.</param>
    protected void Serialize(Document document, Node node, boolean requireType, Object value, Formatter formatter, References references)
    {
    	
//    	if(((Type)value).toString().contains("Basics.SharedServices_$$_javassist_4"))
//    		_logger.Debug("XmlObjectSerializer : Basics.SharedServices_$$_javassist_4 ");
    	
        if (value != null)
        {
            if (_formatters != null)
            {
                Type type = value.getClass();
                if (_formatters.get(type)!=null)
                    formatter = _formatters.get(type);
            }

            if (value instanceof Type)
                value = ProxyUtilities.getNonProxyBaseType((Type)value);
        }

        super.Serialize(document, node, requireType, value, formatter, references);
    }

    /// <summary>
    /// Serialize the object into the node using reflection.
    /// Exchange objects for object markers.
    /// </summary>
    /// <param name="document">The document containing the node.</param>
    /// <param name="node">The node to serialize the object into.</param>
    /// <param name="value">The object to serialize.</param>
    /// <param name="references">The references object that allows for serialization by reference.</param>
    protected void Serialize(Document document, Node node, Object value, References references)
    {
        if (value == null)
        {
            Serialize(document, node, value, references);
            return;
        }
       
     
        Type type = ProxyUtilities.getNonProxyBaseType(value.getClass());
        ReflectionInfo reflectionInfo = ReflectionInfo.getReflectionInfo(type);

        if (reflectionInfo.getGenerateProxy())
        {
        	ISyncProxy proxy = null;
        
        	try{
            proxy = (ISyncProxy)value;
        	}catch(Exception e){
        		//e.printStackTrace();
        	}    
            if (proxy == null)
				try {
					proxy = ObjectBroker.GetLocalProxy(value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			
            ObjectMarker marker = new ObjectMarker();
            marker.setId(proxy.getProxyId());
            marker.setObjectType(type);
            _logger.Debug("Local Proxy Id : " + proxy.getProxyId()  + " Object Marker " + marker.getId());
            Serialize(document, node, marker, references);
         //   printAllChildNodes(node);
            AddTypeAttribute(document, node, ObjectMarker.class);
           // System.out.println(extractTextChildren(node));
            if (!ObjectBroker.DoesBrokerKnowObject(_brokerId, proxy.getProxyId()))
            {
                ObjectBroker.AddKnownObjectToBroker(_brokerId, proxy.getProxyId());

                // serialize the initial state of the object
               // printAllChildNodes(node);
                SerializeInitialState(document, node, value, references);
               // printAllChildNodes(node);
               // System.out.println(extractTextChildren(node));
                
               
            }
        }
        else
        {
            super.Serialize(document, node, value, references);
          
            
        }
    }
    
    public static String extractTextChildren(Node parentNode) {
        NodeList childNodes = parentNode.getChildNodes();
        String result = new String();
        for (int i = 0; i < childNodes.getLength(); i++) {
          Node node = childNodes.item(i);
          
            result += node.getNodeValue() + " - " + node.getNodeName();          
        }
        return result;
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
				System.out.print(itemNode.getNodeValue() + "  ");
				
			}
			if(item.getChildNodes()!=null)
				printAllChildNodes(item);
			System.out.println("");
		}
		System.out.println("");
	}

    
    /// <summary>
    /// Serialize the initial state of the object.
    /// </summary>
    /// <param name="document"></param>
    /// <param name="node"></param>
    /// <param name="value"></param>
    /// <param name="references"></param>
    /// <returns></returns>
    private void SerializeInitialState(Document document, Node node, Object value, References references)
    {
        //Node initialStateNode = document.createNode(NodeType.Element, INITIAL_STATE, "");
    	Node initialStateNode = document.createElement(XmlConstants.INITIAL_STATE);
        super.Serialize(document, initialStateNode, value, references);        
        node.appendChild(initialStateNode);
        
        
    }
}
