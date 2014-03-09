package vitruvianJ.distribution;

import vitruvianJ.core.EventWaitHandle;
import vitruvianJ.events.Event;

 public class AsyncResult  implements IAsyncResult{

	 protected Object _state = null;

     protected EventWaitHandle _waitHandle = null;
     protected Event _callback = null;

     protected boolean _completedSynchronously = false;
	 protected boolean _isComplete = false;
		
	 
	 public AsyncResult(Event callback)//, Object state)
		{
         _waitHandle = new EventWaitHandle();//false, EventResetMode.AutoReset);
			_callback = callback;
			//_state = state;
		}
	
	 public AsyncResult(Event callback, Object someValue) {
		 _waitHandle = new EventWaitHandle();//false, EventResetMode.AutoReset);
			_callback = callback;
			_state = someValue;
	}

	public Object getAsyncState()
	{
			return _state; 
	}
	public void setAsyncState(Object value)
    {
             _state = value;

             _isComplete = true;
             _waitHandle.Set();
             if (_callback != null)
                 _callback.RaiseEvent();//(this);
    }
		
	 
	@Override
	public EventWaitHandle getAsyncWaitHandle() {
		// TODO Auto-generated method stub
		return _waitHandle;
	}

	

	public void setCompletedSynchronously(boolean value) {
		// TODO Auto-generated method stub
		_completedSynchronously = value;
	}
	
	@Override
	public boolean IsCompleted() {
		// TODO Auto-generated method stub
		return _isComplete;
	}

	@Override
	public boolean CompletedSynchronously() {
		// TODO Auto-generated method stub
		return _completedSynchronously;
	}

}
