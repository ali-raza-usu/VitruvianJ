package vitruvianJ.communication.session.protocols;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.ResponseMessage;
import vitruvianJ.core.EventWaitHandle;
import vitruvianJ.distribution.sessions.messages.*;
import vitruvianJ.delegate.Delegator;
import vitruvianJ.events.Event;
import vitruvianJ.logging.JLogger;

public class AsyncProtocol implements IProtocol
{
	JLogger _logger = new JLogger(AsyncProtocol.class);
	protected ProtocolSession _session = null;
	private Message _message = null;
	private ResponseMessage _responseMessage = null;

	private int _timeout = 0;
	private int _numTries = 1;
	protected Event _callback = null;

	private boolean _isFinished = false;

	private EventWaitHandle _waitHandle = null;

    private Object _syncObject = new Object();

	/// <summary>
	/// Construct an AsyncProtocol
	/// </summary>
	/// <param name="session">The session to add the protocol to.</param>
	/// <param name="timeout">The time to wait for a response before timing out.</param>
	/// <param name="numTries">The number of times to resend the message.</param>
	/// <param name="callback">The function to call when a timeout expires, or the response is recieved.  This can be null.</param>
	public AsyncProtocol(ProtocolSession session, int timeout, int numTries, Event callback)
	{
		_session = session;
		_callback = callback;
		_timeout = timeout;
		_numTries = numTries;

		_waitHandle = new EventWaitHandle();//false, EventResetMode.AutoReset);
	}

	/// <summary>
	/// Begin the asynchronous send.
	/// </summary>
	/// <param name="message">The message to send.</param>
	public void BeginSend(Message message)
	{
		_message = message;

		if (!_session.AddProtocol(this))
		{
			ProtocolFinished();
			return;
		}
		_logger.Debug("ASyncProtocol : Begin Send() " + message.toString());
		_session.Send(_message);
	}

	/// <summary>
	/// Block until the send is complete.
	/// </summary>
	public void EndSend() throws InterruptedException
	{
		_logger.Debug("ASyncProtocol : EndSend() "); 
		_waitHandle.WaitOne();
	}

	public void EndSend(long time) throws InterruptedException
	{
		_logger.Debug("ASyncProtocol : EndSend() "); 
		_waitHandle.WaitOne(time);
	}

	/// <summary>
	/// Handle the finished protocol process.
	/// </summary>
	private void ProtocolFinished()
	{
        // make this method thread-safe
        synchronized (_syncObject)
        {
            if (_isFinished)
                return;

            _isFinished = true;
        }

		_waitHandle.Set();//notifyAll();//Set();

		if (_callback != null)
			_callback.RaiseEvent();//invoke(this);
	}

	/// <summary>
	/// The response message.  This can be null if no response was received.
	/// </summary>
	public ResponseMessage getResponseMessage()
	{
		return _responseMessage;
	}

	 protected void SetTimeout(int timeout)
     {
         _timeout = timeout;
     }

	/// <summary>
	/// Time in (ms) until the timeout occurs.
	/// </summary>
	/// <returns>The time in (ms) until the protocol times out.</returns>
	public int getTimeOut()
	{
		return _timeout; 
	}

	/// <summary>
	/// Flag indicating that the protocol is finished, and should
	/// not be used for further message processing, or timeouts.
	/// </summary>
	public boolean IsFinished()
	{
		return _isFinished;
	}

	/// <summary>
	/// Indicate to the protocol that a timeout has happened.
	/// </summary>
	public void TimeExpired()
	{
		_numTries--;
		if (_numTries > 0)
		{
			_session.Send(_message);
			return;				
		}

		ProtocolFinished();
	}

    /// <summary>
    /// Determine if the message is for this protocol.
    /// </summary>
    /// <param name="message"></param>
    /// <returns></returns>
    public boolean IsProtocolMessage(Message message)
    {
    	ResponseMessage rMessage = null;
    	try{
    		rMessage = (ResponseMessage)message;
    	}catch(Exception e)
    	{
    		return false;
    	}
        if (rMessage == null)
            return false;

        if (rMessage.getOriginalMessageId().intValue() != _message.getMessageId().intValue())
            return false;

        _responseMessage = rMessage;
        return true;
    }

	/// <summary>
	/// Finish the protocol.
	/// </summary>
	public void ProcessMessage()
	{
        ProtocolFinished();
	}

	@Override
	public Delegator setProtocolCallbackHandler(IProtocol protocol) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
