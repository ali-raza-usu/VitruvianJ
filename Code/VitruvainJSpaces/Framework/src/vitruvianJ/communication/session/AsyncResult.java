
package vitruvianJ.communication.session;

import vitruvianJ.communication.*;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;

	/// <summary>
	/// The intermediate object used to implement asynchronous communication.
	/// </summary>
	class AsyncResult extends vitruvianJ.communication.AsyncResult
	{
		private int _timeout = -1;
		private long _startTime = 0;
		private Integer _messageId = 0;

		/// <summary>
		/// Construct an AsyncResult.
		/// </summary>
		/// <param name="messageId">The id of the sent message.</param>
		/// <param name="callback">The function to call on receipt of the response message.</param>
		/// <param name="timeout">The amount of time to wait for the response.</param>
		/// <param name="startTime">The time that the message was sent.</param>
		public AsyncResult(Integer messageId, Event callback, int timeout, long startTime)
           // : base(callback)
		{
			super(callback);
			_messageId = messageId;
			_timeout = timeout;
			_startTime = startTime;
		}

		/// <summary>
		/// The id of the sent message.
		/// </summary>
		public Integer getMessageId()
		{
			return _messageId; 
		}
		
		public void setMessageId(Integer value)
		{
			_messageId = value;
		}

		/// <summary>
		/// The amount of time to wait for a response.
		/// </summary>
		public int getTimeOut()
		{
			return _timeout;
		}
		
		public void setTimeOut(int value)
		{
			_timeout = value;
		}

		/// <summary>
		/// The time that the original message was sent.
		/// </summary>
		public long getStartTime()
		{
			return _startTime; 
		}
		
		public void setStartTime(long value)
		{
			_startTime = value;
		}

		/// <summary>
		/// Handle the receipt of the response message.
		/// </summary>
		public void CompleteMessage()
		{
			_isComplete = true;
/*
			if (_callback != null)
				_callback.build(this, "MethodName");//Invoke(this);
*/
            _waitHandle.notifyAll();//Set();
		}

		/// <summary>
		/// Handle the timeout of the response message.
		/// </summary>
		public void TimeOutMessage()
		{
			_isComplete = false;
/*
			if (_callback != null)
				_callback.build(this, "MethodName");//Invoke(this);
*/
            _waitHandle.notifyAll();//Set();
		}
	}
