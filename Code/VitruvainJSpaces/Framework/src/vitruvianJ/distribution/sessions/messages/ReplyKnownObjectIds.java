package vitruvianJ.distribution.sessions.messages;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization
 public class ReplyKnownObjectIds extends ResponseMessage
	{
     private List<JGUID> _knownObjectIds = new ArrayList<JGUID>();

		public ReplyKnownObjectIds()
		{
			super(MessageIds.REPLY_KNOWN_OBJECT_IDS);
		}

     public ReplyKnownObjectIds(int requestMessageId, List<JGUID> knownObjectIds)
		{
    	 super(requestMessageId, MessageIds.REPLY_KNOWN_OBJECT_IDS);
         _knownObjectIds = knownObjectIds;
		}

     public List<JGUID> getKnownObjectIds()
		{
         return _knownObjectIds; 
        }
     public void setKnownObjectIds(List<JGUID> values) 
     { 
    	 _knownObjectIds = values; 
	 }

     public String toString()
     {
         return String.format("Reply Known Object Ids : MessageId = %1s : OriginalId = %2s", getMessageId(), getOriginalMessageId());
     }
	}
