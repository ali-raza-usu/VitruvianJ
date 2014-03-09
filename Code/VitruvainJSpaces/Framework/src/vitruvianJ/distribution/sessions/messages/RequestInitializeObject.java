package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.OptimisticSerialization;

//[OptimisticSerialization]
@OptimisticSerialization
  public class RequestInitializeObject extends RequestMessage
	{
     private JGUID _brokerId = new JGUID();
     private JGUID _proxyId = new JGUID();
     private Object _value = null;

     public RequestInitializeObject(){
         super(MessageIds.REQUEST_INIT_OBJECT);
		}

     public RequestInitializeObject(JGUID brokerId, JGUID proxyId, Object value)
     {
    	 super(MessageIds.REQUEST_INIT_OBJECT);
         _brokerId = brokerId;
         _proxyId = proxyId;
         _value = value;
     }

     public JGUID getBrokerId()
     {
         return _brokerId; 
     }
     public void   setBrokerId(JGUID value) 
     { 
    	 _brokerId = value;
     }

     public JGUID getProxyId()
     {
         return _proxyId; 
     }
     public void setProxyId(JGUID value) 
     { 
    	 _proxyId = value;
     }

     public Object getValue()
     {
         return _value; 
     }
     public  void  setValue(Object value) 
     { 
    	 _value = value;
     }

     public String toString()
     {
         return String.format("Request Initialize Object : MessageId = 1%s : BrokerId = 2%s : ProxyId = 3%s", getMessageId(), getBrokerId(), getProxyId());
     }
	}