package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization
 
public class ReplyInitializeObject extends ResponseMessage
	{
     private JGUID _brokerId = null;
     private JGUID _proxyId = null;
     private Object _value = null;

		public ReplyInitializeObject() 
		{
			super(MessageIds.REPLY_INIT_OBJECT);
		}

     public ReplyInitializeObject(Integer requestMessageId, JGUID brokerId, JGUID proxyId, Object value)
		{
    	 super(requestMessageId, MessageIds.REPLY_INIT_OBJECT);
         _brokerId = brokerId;
         _proxyId = proxyId;
         _value = value;
		}

     public JGUID getBrokerId()
     { 
    	 return _brokerId; 
     }
     
     public void setBroketId(JGUID value) 
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
     
	public void setValue(Object value) 
	{ 
		_value = value; 
	}

     public String toString()
     {
         return String.format("Reply Initialize Object : MessageId = %1s : OriginalId = %2s : BrokerId = %3s : ProxyId = %4s", getMessageId(), getOriginalMessageId(), getBrokerId(), getProxyId());
     }
	}
