package vitruvianJ.communication;

//import vitruvianJ.delegate.Delegator;
import vitruvianJ.core.EventWaitHandle;
import vitruvianJ.events.Event;

/// <summary>
	/// A simple implementation of IAsyncResult
	/// </summary>
	public class AsyncResult implements IAsyncResult
	{
        protected Object _state = null;

        protected EventWaitHandle _waitHandle = null;
        protected Event _callback = null;

        protected boolean _completedSynchronously = false;
		protected boolean _isComplete = false;

		/// <summary>
		/// Construct an AsyncResult.
		/// </summary>
		/// <param name="callback">The function to call on receipt of the response message.</param>
		/// <param name="startTime">The time that the message was sent.</param>
		public AsyncResult(Event callback)
		{
            _waitHandle = new EventWaitHandle();//false, EventResetMode.AutoReset);
			_callback = callback;
		}

	
		public AsyncResult(Event callback, Object p_state)
		{
            _waitHandle = new EventWaitHandle();//false, EventResetMode.AutoReset);
			_callback = callback;
			_state = p_state;
		}

		/// <summary>
		/// The result of the asynchronous method.
		/// </summary>
		public Object getAsyncState()
		{
			return _state; 
		}
		
		public void setAsyncState(Object value)
        {
                _state = value;

                _isComplete = true;
                _waitHandle.Set();//notifyAll();
                /*
                if (_callback != null)
                    _callback.build(this, "MethodName");
                    */
        }
		

		/// <summary>
		/// The wait handle used to block until completion or timeout.
		/// </summary>
		public EventWaitHandle getAsyncWaitHandle()
		{
            return _waitHandle; 
		}

		/// <summary>
		/// Indication if this was a synchronous call.
		/// </summary>
		public boolean getCompletedSynchronously()
		{
			return _completedSynchronously; 
		}
		
        public void   setCompletedSynchronously(boolean value) 
        { 
        	_completedSynchronously = value; 
		}

		/// <summary>
		/// Determine if the response was received.
		/// </summary>
		public boolean IsCompleted()
		{
			return _isComplete; 
		}


	}
