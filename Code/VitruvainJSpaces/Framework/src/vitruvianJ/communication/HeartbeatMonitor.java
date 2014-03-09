package vitruvianJ.communication;
import vitruvianJ.serialization.*;
import vitruvianJ.logging.*;
import vitruvianJ.communication.Heartbeat;
import vitruvianJ.communication.channels.*;
import vitruvianJ.core.*;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.eventargs.PropertyEventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.EventRegistry;
import vitruvianJ.events.IEventSubject;


	/// <summary>
	/// Manages connectivity for a channel.
	/// </summary>
	public class HeartbeatMonitor implements IEventSubject
	{
        private JLogger _logger = new JLogger(HeartbeatMonitor.class);
		private SignalSyncThread _heartbeatThread = null;
		private SignalSyncThread _monitorThread = null;

		private int _frequency = 500;
		private int _timeout = 1500;

		private boolean _receivedHeartbeat = false;

        // protects (_receivedHeartbeat)
		private Object _syncObject = new Object();

        private JStopWatch _heartbeatReceivedTimer = new JStopWatch();
        private JStopWatch _heartbeatSentTimer = new JStopWatch();

        private IChannel _channel = null;

        private boolean _monitorHeartbeats = true;

        private boolean _isAlive = false;

        private boolean _enabled = true;
		/// <summary>
		/// Delegate called when the connection becomes alive.
		/// </summary>
		//public event EventHandler ConnectionAlive;
        public Event ConnectionAlive = new Event(this);

		/// <summary>
		/// Delegate called when the connection becomes dead.
		/// </summary>
		//public event EventHandler ConnectionDead;
		public Event ConnectionDead = new Event(this);

		public HeartbeatMonitor()
		{
			EventRegistry.getInstance().addEvent("ConnectionAlive", ConnectionAlive);
			EventRegistry.getInstance().addEvent("ConnectionDead", ConnectionDead);
		}
		/// <summary>
		/// Default Constructor.
		/// </summary>
		public HeartbeatMonitor(IChannel channel)
		{
			this();
			_heartbeatThread = new SignalSyncThread("Heartbeat", new SendHeartbeat());
			_monitorThread = new SignalSyncThread("Monitor Heartbeat",new MonitorHeartbeat());			
            _channel = channel;
		}

		/// <summary>
		/// The frequency of the heartbeat.
		/// </summary>
		@Serialize
        public int getFrequency()
		{
			return _frequency;
		}
        
		@Serialize
        public void setFrequency(int value)
		{
        	_frequency = value; 
        }
	

		/// <summary>
		/// The time that must elapse without receiving a
		/// heartbeat for the connection to be termed dead.
		/// </summary>
		@Serialize
        public int getTimeout()
		{
			return _timeout; 
		}
		@Serialize
		public void	setTimeout(int value) 
		{ 
			_timeout = value; 
		}
		

		/// <summary>
		/// Start the heartbeat.
		/// </summary>
		public void Start()
		{
		try {			
			    _receivedHeartbeat = true;

			    _heartbeatThread.Start(true);//Start(true);
				_monitorThread.Start(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		}

		/// <summary>
		/// Stop the heartbeat.
		/// </summary>
		public void Stop()
		{
            if (_monitorThread.IsRunning())
				try {
					_monitorThread.Stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            if (_heartbeatThread.IsRunning())
				try {
					_heartbeatThread.Stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

        /// <summary>
        /// Monitor the heartbeats
        /// </summary>
        //[Category("Debug")]
        public boolean getMonitorHeartbeats()
        {
            return _monitorHeartbeats; 
        }
        
        public void setMonitorHeartbeats(boolean value)
        {
            _monitorHeartbeats = value;
        }

		/// <summary>
		/// Send the heartbeat at some frequency
		/// </summary>
        class SendHeartbeat implements Delegate
        {
        	
        	
        	
        	
		private void sendHeartbeat()
		{
			try
			{
//                if (_logger.IsDebugEnabled())
//                    _logger.DebugFormat("Sending Heartbeat to %1$2s -> Time since last send {%2$2s:F2} s", _channel.toString(), _heartbeatSentTimer.GetElapsed_s());
                
              //  _heartbeatSentTimer.Reset();
                Heartbeat beat = new Heartbeat();
                _channel.Send(beat);
               // _logger.Debug("Sending Heartbeat : " + beat.getMessageId());
			}
			catch(Exception e)
			{
				System.out.println(e);
			}

			try {
				_heartbeatThread.Sleep(_frequency);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_heartbeatThread.Signal();
		}

		@Override
		public void invoke(EventArgs args) {
			// TODO Auto-generated method stub
			sendHeartbeat();
		}
        }

		/// <summary>
		/// Monitor the heartbeat for alive or dead changes.
		/// </summary>
        class MonitorHeartbeat implements Delegate{
        	
		private void monitorHeartbeat() throws Exception
		{
            if (_monitorHeartbeats)
            {
                boolean isAlive = false;
                boolean isDead = false;

                synchronized (_syncObject) // thread-safe
                {
                    isAlive = _receivedHeartbeat;
                    isDead = !_receivedHeartbeat;
                    
                    _receivedHeartbeat = false;
                }

                if (isAlive)
                {
                    if (!_isAlive)
                    {
                        _isAlive = true;

//                        if (_logger.IsInfoEnabled())
//                            _logger.InfoFormat("Connection is Alive -> %1$2s", _channel.toString());
                    
                        if (ConnectionAlive != null)
                            ConnectionAlive.RaiseEvent();//(this, new EventArgs());
                    }
//                    else
//                    {
//                        if (_logger.IsDebugEnabled())
//                            _logger.DebugFormat("Connection is Alive -> %1$2s", _channel.toString());
//                    }
                }

                if (isDead)
                {
                    if (_isAlive)
                    {
                        _isAlive = false;

//                        if (_logger.IsInfoEnabled())
//                            _logger.InfoFormat("Connection is Dead -> %1$2s\r\nLast heartbeat was received {%2$2s:F2}(s) ago.", _channel.toString(), _heartbeatReceivedTimer.GetElapsed_s());
                    
                        if (ConnectionDead != null)
                            ConnectionDead.RaiseEvent();//(this, new EventArgs());
                    }
                    else
                    {
//                        if (_logger.IsDebugEnabled())
//                            _logger.DebugFormat("Connection is Dead -> %1$2s\r\nLast heartbeat was received {%2$2s:F2}(s) ago.", _channel.toString(), _heartbeatReceivedTimer.GetElapsed_s());
                    }
                }
            }

		
				_monitorThread.Sleep(_timeout);
		
			_monitorThread.Signal();
		}
		@Override
		public void invoke(EventArgs args) {
			try {
				monitorHeartbeat();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
        }
        /// <summary>
        /// Handle the heartbeat message.
        /// </summary>
        /// <param name="message"></param>
        public void HeartbeatReceived()
        {
//            if (_logger.IsDebugEnabled())
//                _logger.DebugFormat("Received Heartbeat from %1$2s -> Time since last read {%2$2s:F2} s", _channel.toString(), _heartbeatReceivedTimer.GetElapsed_s());

            synchronized(_syncObject) // thread-safe
            {
                _receivedHeartbeat = true;
            }

          //  _heartbeatReceivedTimer.Reset();
        }
		@Override
		public EventArgs getEventArgs() {
			// TODO Auto-generated method stub
			EventArgs args = new PropertyEventArgs();
			return args;
		}
		
        public boolean getEnabled()
        {
            return _enabled; 
        }
        
        public void setEnabled(boolean value) 
        { 
        	_enabled = value;
        }
        
	}

