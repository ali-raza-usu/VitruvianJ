package vitruvianJ.distribution.sessions.messages;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.serialization.OptimisticSerialization;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.services.IService;

@OptimisticSerialization
 public class ReplyServices extends ResponseMessage
	{
		private List<IService> _services = new ArrayList<IService>();

		public ReplyServices()
		{
			super(MessageIds.REPLY_SERVICES);
		}

		public ReplyServices(int requestMessageId, List<IService> services)
		{
			super(requestMessageId, MessageIds.REPLY_SERVICES);
			_services = services;
		}

		@Serialize//(getName = "get")
		public List<IService> getServices()
		{
			return _services; 
		}
		@Serialize//(getName = "set")
		public void setServices(List<IService> value) 
		{ 
			_services = value;
		}

		
     public String toString()
     {
         if (_services != null)
             return String.format("Reply Services : MessageId = %1s : OriginalId = %2s : Cnt = %3s", getMessageId(), getOriginalMessageId(), _services.size());
         else
             return String.format("Reply Services : MessageId = %1s : OriginalId = %2s : Cnt = 0", getMessageId(), getOriginalMessageId());
     }
	}
