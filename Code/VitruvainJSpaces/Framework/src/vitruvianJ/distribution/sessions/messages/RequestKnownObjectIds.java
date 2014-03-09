package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization
 
public class RequestKnownObjectIds extends RequestMessage
	{
     public RequestKnownObjectIds()
		{
    	 super(MessageIds.REQUEST_KNOWN_OBJECT_IDS);
		}

     public String toString()
     {
         return String.format("Request Known Object Ids : MessageId = %1s", getMessageId());
     }
	}
