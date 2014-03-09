package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization
 public class ReplyBrokerId extends ResponseMessage
	{
     private JGUID _brokerId = new JGUID();

		public ReplyBrokerId()
		{
			super(MessageIds.REPLY_BROKER_ID);
		}

     public ReplyBrokerId(int requestMessageId, JGUID brokerId)
		{
    	 super(requestMessageId, MessageIds.REPLY_BROKER_ID);
         _brokerId = brokerId;
		}

		public JGUID getBrokerId()
		{
			return _brokerId; 
        }
        public void setBrokerId(JGUID value) 
        {
        	_brokerId = value;
		}

     public String toString()
     {
         return String.format("Reply Broker Id : MessageId = "+getMessageId()+" : OriginalId = "+ getOriginalMessageId()+" : BrokerId = " +_brokerId);
     }
}


