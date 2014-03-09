package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization
 public class ReplyExecuteSyncPatternMethod extends ResponseMessage
	{
		private Object _retValue = null;

		public ReplyExecuteSyncPatternMethod()
		{
			super(MessageIds.REPLY_EXECUTE_SYNC_PATTERN_METHOD);
		}

     public ReplyExecuteSyncPatternMethod(Integer requestMessageId, Object retValue)
		{
    	    super(requestMessageId, MessageIds.REPLY_EXECUTE_SYNC_PATTERN_METHOD);
			_retValue = retValue;
		}

		public Object getRetValue()
		{
			return _retValue; 
		}
		public void	setRetValue(Object value) 
		{ 
			_retValue = value;
		}

     public String toString()
     {
         if (_retValue != null)
         {
             // don't log the return value, because it could be
             // a proxy and this could cause cyclic communications!
             return String.format("Reply Execute SyncPattern Method : MessageId = %1s : OriginalId = %2s : ValueType = %3s", getMessageId(), getOriginalMessageId(), _retValue.getClass().getName());
         }
         else
             return String.format("Reply Execute SyncPattern Method : MessageId = {0} : OriginalId = {1} : Value = null", getMessageId(), getOriginalMessageId());
     }
	}
