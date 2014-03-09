
package vitruvianJ.communication.session;

import vitruvianJ.serialization.OptimisticSerialization;


	/// <summary>
	/// A response message to a request.
	/// </summary>
    @OptimisticSerialization
    
    public class ResponseMessage extends Message
	{
		private Integer _originalMessageId = 0;

		public ResponseMessage(Integer messageType) 
		{
			super(messageType);
		}

		public ResponseMessage(Integer originalMessageId, Integer messageType) 
		{
			super(messageType);
			_originalMessageId = originalMessageId;
		}

		/// <summary>
		/// The message id of the request.
		/// </summary>
		public Integer getOriginalMessageId()
		{
			return _originalMessageId; 
		}
		
		public void	setOriginalMessageId(Integer value) 
		{ 
			_originalMessageId = value; 
		}
		
	}

