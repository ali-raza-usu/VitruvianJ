package vitruvianJ.distribution.sessions.messages;

import java.lang.reflect.Type;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.distribution.proxies.ProxyUtilities;
import vitruvianJ.serialization.OptimisticSerialization;
import vitruvianJ.services.IService;

@OptimisticSerialization
 public class RequestRemoveService extends RequestMessage
	{
		private IService _service = null;

		public RequestRemoveService()
		{
			super(MessageIds.REQUEST_ADD_SERVICE);
		}

		public RequestRemoveService(IService service)
		{
			super(MessageIds.REQUEST_ADD_SERVICE);
			_service = service;
		}

		public IService getService()
		{
			return _service; 
		}
		public void	setService(IService value) 
		{
			_service = value; 
		}

     public String toString()
     {
         Type baseType = ProxyUtilities.getNonProxyBaseType(_service.getClass());
         return String.format("Request Remove Service : MessageId = %1s : Service = %2s", getMessageId(), baseType.getClass().getName());
     }
	}
