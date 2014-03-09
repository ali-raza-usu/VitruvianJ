package vitruvianJ.communication.session;

import vitruvianJ.serialization.OptimisticSerialization;

	/// <summary>
	/// A message with a message id.
	/// </summary>
    @OptimisticSerialization
    
	public class RequestMessage extends Message
	{
		/// <summary>
		/// Default constructor.
		/// </summary>
		public RequestMessage()
		{}

		/// <summary>
		/// Construct a message from a message type id.
		/// </summary>
		/// <param name="messageTypeId">The id of the message type.</param>
        public RequestMessage(Integer messageTypeId)
            
		{
        	super(messageTypeId);
		}
	}

