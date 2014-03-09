package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.serialization.OptimisticSerialization;

 @OptimisticSerialization
 public class ReplyExecuteMethod extends ResponseMessage
	{
		private Object _retValue = null;
     private Object[] _updateArgs = null;

		public ReplyExecuteMethod()
		{
			super(MessageIds.REPLY_EXECUTE_METHOD);
		}

		public ReplyExecuteMethod(Integer requestMessageId, Object retValue)
		{
			super(requestMessageId, MessageIds.REPLY_EXECUTE_METHOD);
			_retValue = retValue;
		}

     public ReplyExecuteMethod(Integer requestMessageId, Object[] updateArgs, Object retValue)
     {
    	 super(requestMessageId, MessageIds.REPLY_EXECUTE_METHOD);
         _updateArgs = updateArgs;
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
		

		public Object[] getUpdatedArgs()
        { 
			return _updateArgs; 
		}
        
		public void setUpdatedArgs(Object[] value) 
		{
			_updateArgs = value; 
		}
     

     public String toString()
     {
         if (_retValue != null)
         {
             // don't log the return value, because it could be
             // a proxy and this could cause cyclic communications!
             return String.format("Reply Execute Method : MessageId = %1s : OriginalId = %2s : ValueType = %3s", getMessageId(), getOriginalMessageId(), _retValue.getClass().getName());
         }
         else
             return String.format("Reply Execute Method : MessageId = %1s : OriginalId = %2s : Value = null", getMessageId(), getOriginalMessageId());
     }
	}
