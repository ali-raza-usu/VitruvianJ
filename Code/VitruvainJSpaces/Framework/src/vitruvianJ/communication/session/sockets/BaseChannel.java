package vitruvianJ.communication.session.sockets;

import vitruvianJ.communication.HeartbeatMonitor;
import vitruvianJ.communication.channels.ChannelEventArgs;
import vitruvianJ.communication.channels.IChannel;
import vitruvianJ.communication.session.MessageEventArgs;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.EventRegistry;
import vitruvianJ.events.IEventSubject;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.IEncoder;

public abstract class BaseChannel implements IChannel, IEventSubject
{
    private JLogger _logger = new JLogger(BaseChannel.class);
    
    public static IEncoder _encoder = null;
    protected Event _messageReceived =  new Event(this);
    protected Event _channelClosed = new Event(this);

    protected HeartbeatMonitor _monitor = null;

    protected Object _syncObject = new Object();
    private boolean _isOpen = false;

    protected Object message = null;
    HeartbeatConnectionDead heartBeatConnectionDead = new HeartbeatConnectionDead();
    

    /// <summary>
    /// Construct the channel.
    /// </summary>
    /// <param name="heartbeatFrequency"></param>
    /// <param name="heartbeatTimeout"></param>
    public BaseChannel()
    {
    	EventRegistry.getInstance().addEvent("_messageReceived", _messageReceived);
    	EventRegistry.getInstance().addEvent("_channelClosed", _channelClosed);
    }
    
    
    public BaseChannel(int heartbeatFrequency, int heartbeatTimeout, IEncoder encoder)
    {
    	this();
    	_encoder = encoder;
        _monitor = new HeartbeatMonitor(this);
        _monitor.setFrequency(heartbeatFrequency);
        _monitor.setTimeout(heartbeatTimeout);
    }

    protected void ThreadSafeClose()
    {
        Close closeThread = new Close();
        closeThread.start();
    }



    /// <summary>
    /// Delegate called when a message is received.
    /// </summary>
    public Event getMessageReceived()
    {
    	return _messageReceived; 
    }
    
    public void setMessageReceived(Event value) 
    { 
    	_messageReceived = value; 
    }

    /// <summary>
    /// Delegate called when the channel is closed.
    /// </summary>
    public Event getChannelClosed()
    {
        return _channelClosed; 
    }
    
    public void setChannelClosed(Event value) 
    { 
    	_channelClosed = value;
    }

    /// <summary>
    /// The object used to encode messages.
    /// </summary>
    public IEncoder getEncoder()
    {
    	return _encoder;
    }
    
    public void setEncoder(IEncoder value) { 
    	_encoder = value; 
    	
    }

    protected boolean IsOpen()
    {
        return _isOpen; 
    }

    public void Open()
    {
        synchronized (_syncObject)
        {
            if (!_isOpen)
            {
                _isOpen = true;

                _monitor.ConnectionDead.addObservers(heartBeatConnectionDead);
                _monitor.Start();
            }
        }
    }

    /// <summary>
    /// Event called when the heartbeat determines the connection is dead.
    /// </summary>
    /// <param name="sender"></param>
    /// <param name="e"></param>
    class HeartbeatConnectionDead implements Delegate
    {
	    private void HeartbeatConnectionDead(EventArgs e)//Object sender, EventArgs e)
	    {
	    	_logger.Debug("HeartbeantConnectionDead : ThreadSageClose() ");
	        ThreadSafeClose();
	    }

		@Override
		public void invoke(EventArgs args) {
			// TODO Auto-generated method stub
			HeartbeatConnectionDead(args);
		}
    }
    
    class Close extends Thread
    {
	    public void run()
	    {
	        close();
	    }
    }

    abstract public void Send(Object message);    
    
    @Override 
	public EventArgs getEventArgs()
	{
    	if(getMessageReceived()!=null)
    		return new MessageEventArgs(message);
    	else
    		return  new ChannelEventArgs(this);		
    	
	}


	protected void close() {
		_logger.Debug("Channel is getting closed");
		synchronized (_syncObject)
        {
            _isOpen = false;            
            _monitor.ConnectionDead.removeObservers(heartBeatConnectionDead);
            _monitor.Stop();  
            if (_channelClosed != null)   
            	_channelClosed.RaiseEvent();
        }
	} 

}
