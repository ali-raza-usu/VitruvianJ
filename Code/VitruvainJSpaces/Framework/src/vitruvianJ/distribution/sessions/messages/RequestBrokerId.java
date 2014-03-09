package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization
 public class RequestBrokerId extends RequestMessage
	{
     public RequestBrokerId()
		{
    	 super(MessageIds.REQUEST_BROKER_ID);
		}

     public String toString()
     {
         return String.format("Request BrokerId : MessageId = %1s", getMessageId());
     }
	}
