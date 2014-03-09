package vitruvianJ.distribution.encoders;

import java.lang.reflect.Type;
import java.util.Dictionary;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.ILocalSyncProxy;
import vitruvianJ.distribution.proxies.IRemoteSyncProxy;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.formatters.Formatter;
import vitruvianJ.serialization.xml.XmlDeserializer;

public class XmlObjectDeserializer extends XmlDeserializer
{
	JLogger _logger = new JLogger(XmlObjectDeserializer.class);
    private final String INITIAL_STATE = "Initial-State";

    private HashMap<Type, Formatter> _formatters = null;
    private JGUID _brokerId = null;

    /// <summary>
    /// The id of the broker using this session.
    /// </summary>
    public JGUID getBrokerId()
    {
        return _brokerId; 
    }
    
    public void setBrokerId(JGUID value) {
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

    /// <summary>
    /// Deserialize the node into the given object.
    /// If the given object is null an object will be created.
    /// Exchange object markers for proxy objects.
    /// </summary>
    /// <param name="node">The node defining the object.</param>
    /// <param name="type">The type of the object.</param>
    /// <param name="curValue">The current value of the object; possibly null.</param>
    /// <param name="formatter">The formatter to use when deserializing this object; possibly null.</param>
    /// <returns>The object that was deserialized.</returns>
    /// <param name="references">The references object that allows for deserialization by reference.</param>
    public Object Deserialize(Node node, Type type, Object curValue, Formatter formatter, References references)
    {
        if (_formatters != null)
        {
            if (_formatters.get(type)!=null)
                formatter = _formatters.get(type);
        }

        if (type.equals(ObjectMarker.class))
        {
            ObjectMarker marker = new ObjectMarker();
            super.Deserialize(node, type, marker, null, references);

            _logger.Debug(" marker Id : " + marker.getId());// + " current Object class : " + curValue.getClass());
            Object result = null;
            if (ObjectBroker.IsLocalProxy(marker.getId()))
            {
                result = ((ILocalSyncProxy)ObjectBroker.GetProxy(marker.getId())).getProxyParent();
                DeserializeObjectId(node, result, references);
            }
            else
            {
                try {
					result = ObjectBroker.GetRemoteProxy(_brokerId, marker.getId(), marker.getObjectType());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}              

                DeserializeObjectId(node, result, references);

                _logger.Debug("Before Deserialize initial state  result : " + result);
                if (!((IRemoteSyncProxy)result).IsInitialized())
                {
                    if (!DeserializeInitialState(node, marker.getObjectType(), result, references))
						try {
							ObjectBroker.InitializeObject(_brokerId, (IRemoteSyncProxy)result);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    
                    ((IRemoteSyncProxy)result).setInitialized(true);

                    // only start the sync patterns
                    // after it has been initialized
                    ((IRemoteSyncProxy)result).StartSyncPatterns();
                }
            }

            return result;
        }
        else
        {
            return super.Deserialize(node, type, curValue, formatter, references);
        }
    }

    /// <summary>
    /// Deserialize the initial state into the proxy.
    /// </summary>
    /// <param name="node"></param>
    /// <param name="proxy"></param>
    /// <param name="references"></param>
    private boolean DeserializeInitialState(Node node, Type type, Object proxy, References references)
    {
    	NodeList nodes = node.getChildNodes();
    	
        for (int i =0; i<nodes.getLength(); i++)
        {
        	Node child = nodes.item(i);
        	
        	String name = child.getNodeName();
            if (name != null && name.equals(INITIAL_STATE))
            {                       
                //super.Deserialize(child,type, proxy,null, references); //Am I calling the right method
            	super.Deserialize(child,type, proxy, references); //Am I calling the right method
                return true;
            }
        }

        return false;
    }
}
