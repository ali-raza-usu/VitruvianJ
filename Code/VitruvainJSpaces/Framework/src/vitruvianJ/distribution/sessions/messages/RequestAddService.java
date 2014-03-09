package vitruvianJ.distribution.sessions.messages;

import java.lang.reflect.Type;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.distribution.proxies.ProxyUtilities;
import vitruvianJ.serialization.OptimisticSerialization;
import vitruvianJ.services.IService;

@OptimisticSerialization
 public class RequestAddService extends RequestMessage
	{
		private IService _service = null;

		public RequestAddService()
		{
			super(MessageIds.REQUEST_ADD_SERVICE);
		}

		public RequestAddService(IService service)
		{
			super(MessageIds.REQUEST_ADD_SERVICE);
			_service = service;
		}

		public IService getService()
		{
			return _service; 
		}
		public	void setService(IService value) 
		{ 
			_service = value; 
		}

     public String toString()
     {
         Type baseType = ProxyUtilities.getNonProxyBaseType(_service.getClass());
         return String.format("Request Add Service : MessageId = %1s : ServiceType = %2s", getMessageId(), baseType.getClass().getName());
     }
	}
