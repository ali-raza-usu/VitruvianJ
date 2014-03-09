package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization

 public class RequestServices extends RequestMessage
	{
		public RequestServices()
		{
			super(MessageIds.REQUEST_SERVICES);
		}

     public String toString()
     {
         return String.format("Request Services : MessageId = %1s", getMessageId());
     }
	}
