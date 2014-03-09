package vitruvianJ.communication.session.protocols;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import vitruvianJ.HiddenMessages;
import vitruvianJ.communication.session.*;
import vitruvianJ.logging.JLogger;
import vitruvianJ.communication.*;
import vitruvianJ.core.SignalSyncThread;
import vitruvianJ.core.JStopWatch;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;

public class ProtocolSession extends Session
{
    static private JLogger _logger = new JLogger(ProtocolSession.class);

    private DelayedSyncList<PendingProtocol> _pendingProtocols = new DelayedSyncList<PendingProtocol>();
    private Dictionary<Integer, DelayedSyncList<IProcessor>> _processors = new Hashtable<Integer, DelayedSyncList<IProcessor>>();

    private JStopWatch _sw = new JStopWatch();
    private SignalSyncThread _monitorThread = null;

	/// <summary>
	/// Default Constructor.
	/// </summary>
	public ProtocolSession()
	{
		_monitorThread = new SignalSyncThread("Protocol Session Protocol Timeout", new MonitorMessages());
	}

	/// <summary>
	/// Start the session, and start monitoring protocols
	/// </summary>
	public  void Start()
	{
        if (_isRunning)
            return;

        super.Start();

        synchronized(_processors)
        {
            for (Enumeration<DelayedSyncList<IProcessor>> e  = _processors.elements(); e.hasMoreElements() ;)
            {
            	DelayedSyncList<IProcessor> processors = e.nextElement();            	
                processors.Lock();
                Iterator<IProcessor> iterator = processors.iterator();
                while(iterator.hasNext())
                {
                	IProcessor processor = iterator.next();
                    if (!processor.getInitialized())
                        processor.Init(this);
                }
                processors.Unlock();
            }
        }

        _monitorThread.Start(true);
	}

	
	public void SetupRoute(int messageType, IProcessor processor)
	{
        DelayedSyncList<IProcessor> processors = null;

        synchronized (_processors)
        {
            if (_processors.get(messageType)!=null)
                processors = _processors.get(messageType);
            else
            {
                processors = new DelayedSyncList<IProcessor>();
                _processors.put(messageType, processors);
            }
        }

        if (_isRunning && !processor.getInitialized())
            processor.Init(this);

        processors.Add(processor);
	}
	
	class MonitorMessages implements Delegate
	{
		public void invoke(EventArgs args)
		{
			MonitorMessages();
		}
	}
	/// <summary>
	/// Stop the session, and stop monitoring protocols
	/// </summary>
	public  void Stop()
	{
        if (!_isRunning)
            return;

        if (_monitorThread.IsRunning())
        {
            if (_logger.IsDebugEnabled())
                _logger.Debug("Stopping monitoring thread.");
            try {
				_monitorThread.Stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        if (_logger.IsDebugEnabled())
            _logger.Debug("Closing pending protocols.");

        _pendingProtocols.Lock();
        for (int i = 0; i < _pendingProtocols.getCount(); i++)
        {
            IProtocol protocol = _pendingProtocols.get(i).getProtocol();
            while (!_pendingProtocols.get(i).getProtocol().IsFinished())
                protocol.TimeExpired();
        }
        _pendingProtocols.Clear();
        _pendingProtocols.Unlock();

        super.Stop();

        if (_logger.IsDebugEnabled())
            _logger.Debug("Cleaning up processors.");

        synchronized (_processors)
        {
            for (Enumeration<DelayedSyncList<IProcessor>> e  = _processors.elements(); e.hasMoreElements() ;)
            {
            	DelayedSyncList<IProcessor> processors = e.nextElement();  
                processors.Lock();
                Iterator<IProcessor> iterator = processors.iterator();
                while(iterator.hasNext())
                {
                	IProcessor processor = iterator.next();                
                    if (processor.getInitialized())
                        processor.Cleanup();
                }
                processors.Unlock();
            }
            clear(_processors);
        }
	}

	public void clear(Dictionary<?, ?> collection)
	{
		Enumeration<?> iterator = collection.elements();
		while(iterator.hasMoreElements())
		{
			collection.remove(iterator.nextElement());
		}
	}
	public void Send(Message message)
	{
        if (!_isRunning)
            return;

        if (_logger.IsDebugEnabled() && !HiddenMessages.getMsgList().contains(message.getClass()))
            _logger.DebugFormat("Send : %1s", message.toString());

		BeforeSend();
		//_logger.Debug("Protocol Session :  type " + message.getMessageTypeId() + " id " + message.getMessageId());
		_channel.Send(message);
	}

	/// <summary>
	/// Send a message.
	/// </summary>
	/// <param name="message">The message to send.</param>
	public  void Send(Object message)
	{
		Send((Message)message);
	}

	/// <summary>
	/// Determine if the message is consumed by a protocol.
	/// </summary>
	protected  void Process(Object msg)
	{
		if (!_isRunning)
			return;

		Message message = (Message) msg;
		int a =2;
		if(message.getMessageTypeId() == 9)
			a = 3;
        if (IsProtocolMessage(message))
            return;

        DelayedSyncList<IProcessor> processors = null;

        synchronized (_processors)
        {
            if (_processors.get(message.getMessageTypeId())!=null)
                processors = _processors.get(message.getMessageTypeId());
        }

        if (processors != null)
        {
            processors.Lock();       
            Iterator<IProcessor> iterator = processors.iterator();
            while(iterator.hasNext())
            {
            	IProcessor processor = iterator.next();               
                try
                {
                    processor.ProcessMessage(message);
                }
                catch (Exception ex)
                {
                    if (_logger.IsErrorEnabled())
                        _logger.Error(ex);
                }
            }
            processors.Unlock();
        }
        
        else
        {
            if (_logger.IsWarnEnabled())
            {
                if (message.getClass().equals(ResponseMessage.class))
                    _logger.WarnFormat("A response message with original message id %1s, message id %2s, and type %3s was received, but there is no processor or protocol to handle it.", ((ResponseMessage)message).getOriginalMessageId(), message.getMessageId(), message.getMessageTypeId());
                else
                    _logger.WarnFormat("A message with id 1%s and type %2s was received, but there is no processor or protocol to handle it.", message.getMessageId(), message.getMessageTypeId());
            }
        }
	
	}
    private boolean IsProtocolMessage(Message message)
    {
        PendingProtocol mProtocol = null;

        _pendingProtocols.Lock();

        Iterator<PendingProtocol> iterator =  _pendingProtocols.iterator(); 
       
        while(iterator.hasNext())// PendingProtocol pendingProtocol  _pendingProtocols)
        {
            PendingProtocol pendingProtocol = iterator.next();
           if (pendingProtocol.getProtocol().IsProtocolMessage(message))
                {
                    mProtocol = pendingProtocol;
                    break;
                }            
        }
        _pendingProtocols.Unlock();

        boolean result = false;
        try
        {
            if (mProtocol != null)
            {
                mProtocol.getProtocol().ProcessMessage();
                if (mProtocol.getProtocol().IsFinished())
                {
                    _pendingProtocols.Remove(mProtocol);
                    result = true;
                }
            }
        }
        catch (Exception ex)
        {
            if (_logger.IsErrorEnabled())
                _logger.Error(ex);
        }
        
        return result;
    }

	/// <summary>
	/// Add the protocol to session.
	/// </summary>
	/// <param name="protocol">The protocol to add to the session.</param>
	public boolean AddProtocol(IProtocol protocol)
	{
		if (!_isRunning)
			return false;

		long expiration = GetExpiration(protocol);

//        if (_logger.IsDebugEnabled())
//            _logger.DebugFormat("Adding protocol %1s : CurTime %2s : Expiration %3s", protocol.toString(), _sw.GetElapsed_ms(), expiration);

		_pendingProtocols.Add(new PendingProtocol(protocol, expiration));

		// signal the monitor to update
		_monitorThread.Signal();
		return true;
	}

	private long GetExpiration(IProtocol protocol)
	{
		// calculate the expiration time
		long expiration = -1;
		if (protocol.getTimeOut() >= 0)
			expiration = -1;//protocol.getTimeOut() + _sw.GetElapsed_ms();
		return expiration;
	}

	/// <summary>
	/// Add a processor to handle a given message type.
	/// </summary>
	/// <param name="messageType">The message type to handle.</param>
	/// <param name="processor">The processor that should handle the message type.</param>
	public void AddProcessor(Integer messageType, IProcessor processor)
	{
        DelayedSyncList<IProcessor> processors = null;

        synchronized (_processors)
        {
            if (_processors.get(messageType) != null)
                processors = _processors.get(messageType);
            else
            {
                processors = new DelayedSyncList<IProcessor>();
                _processors.put(messageType, processors);
            }
        }

        if (_isRunning && !processor.getInitialized())
            processor.Init(this);

        processors.Add(processor);
	}

    /// <summary>
    /// Remove a processor so it doesn't handle a given message type.
    /// </summary>
    /// <param name="messageType">The message type to be removed from.</param>
    /// <param name="processor">The processor that was handling the message type.</param>
    public void RemoveProcessor(Integer messageType, IProcessor processor)
    {
        synchronized (_processors)
        {
            if (_processors.get(messageType) != null)
            {
                DelayedSyncList<IProcessor> processors = _processors.get(messageType);
                
                processors.Remove(processor);

                if (processors.getCount() == 0)
                    _processors.remove(messageType);
            }
        }

        if (_isRunning && processor.getInitialized())
            processor.Cleanup();
    }

	/// <summary>
	/// Determine if protocols have timed out.  This is done by waiting
	/// for the next protocol with the smallest timeout time.
	/// </summary>
	private void MonitorMessages()
	{
		long minTime = -1;
			
		// timeout / remove protocols
		// find the next timeout time
        _pendingProtocols.Lock();
        
        long elapsed =-1;//_sw.GetElapsed_ms();
        Iterator<PendingProtocol> iterator =  _pendingProtocols.iterator(); 
        
        while(iterator.hasNext())// PendingProtocol pendingProtocol  _pendingProtocols)
        {
            PendingProtocol pendingProtocol = iterator.next();        
            long expiration = pendingProtocol.getExpiration();

            if (expiration < 0)
                continue;

            long timeLeft = expiration - elapsed;

            if (timeLeft <= 0)
            {
                IProtocol protocol = pendingProtocol.getProtocol();
                protocol.TimeExpired();
//
//                if (_logger.IsDebugEnabled())
//                    _logger.DebugFormat("Expiring protocol %1s : CurTime %2s : Expiration %3s", protocol.toString(), _sw.GetElapsed_ms(), expiration);

                if (protocol.IsFinished())
                {
                    _pendingProtocols.Remove(pendingProtocol);
                    continue;
                }
                else
                {
                    pendingProtocol.setExpiration(GetExpiration(protocol));
                    timeLeft = protocol.getTimeOut();
                }
            }

            if (minTime < 0)
                minTime = timeLeft;
            else
                minTime = Math.min(minTime, timeLeft);
        }

        _pendingProtocols.Unlock();

		if (_isRunning)
		{
			// wait for the next timeout ... could be infinite
			
				_monitorThread.Sleep((long)minTime);
			
			// run this loop again
			_monitorThread.Signal();
		}
	}

	@Override
	public EventArgs getEventArgs() {
		// TODO Auto-generated method stub
		return null;
	}
}
