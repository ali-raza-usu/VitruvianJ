package vitruvianJ.communication.session;

import vitruvianJ.serialization.OptimisticSerialization;
import vitruvianJ.serialization.Serialize;


	/// <summary>
	/// A message with a message id.
	/// </summary>
    @OptimisticSerialization
	public class Message
	{
		static private Integer _nextMessageId = 0;//Integer.MAX_VALUE;

        // protects (_nextMessageId)
		static private Object _syncObject = new Object();

		/// <summary>
		/// Get the next pseudo-unique message id.
		/// </summary>
		/// <returns>The next pseudo-unique message id.</returns>
		static private Integer NextMessageId()
		{
            synchronized (_syncObject) // thread-safe
			{
				_nextMessageId++;
				return _nextMessageId;
			}
		}

		private Integer _messageId = NextMessageId();
		private Integer _messageTypeId = 0;

		/// <summary>
		/// Default constructor.
		/// </summary>
		public Message()
		{}

		/// <summary>
		/// Construct a message from a message type id.
		/// </summary>
		/// <param name="messageTypeId">The id of the message type.</param>
		public Message(Integer messageTypeId)
		{
			_messageTypeId = messageTypeId;
		}

		/// <summary>
		/// The id of the message.
		/// </summary>
		@Serialize
		public Integer getMessageId()
		{
			return _messageId; 						
		}

		@Serialize
		public void setMessageId(Integer value)
		{
			_messageId = value; 
		}
		/// <summary>
		/// The id of the message type.
		/// </summary>
		@Serialize
		public Integer getMessageTypeId()
		{
			return _messageTypeId; 
			
		}
		@Serialize
		public void setMessageTypeId(Integer value)
		{
			_messageTypeId = value;	
		}
	}

