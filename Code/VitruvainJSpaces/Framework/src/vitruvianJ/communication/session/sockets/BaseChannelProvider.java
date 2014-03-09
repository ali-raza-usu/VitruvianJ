package vitruvianJ.communication.session.sockets;

//import chat.XmlEncoder;
import vitruvianJ.communication.channels.*;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.eventargs.PropertyEventArgs;
import vitruvianJ.events.Event;

import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
	/// <summary>
	/// Provider for channel communication.
	/// </summary>
   public abstract class BaseChannelProvider extends IChannelProvider
	{
        private static JLogger _logger = new JLogger(BaseChannelProvider.class);

        private int _heartbeatFrequency = 1000; // once a second
        private int _heartbeatTimeout = 10000; // check every 10 seconds
        private boolean _useHeartbeats = true;

        protected Event _channelAvailable = new Event(this);
        protected IEncoder _encoder = null;
        private EventArgs arg;
        public BaseChannelProvider()
        {
        }

        public EventArgs getEventArgs()
        {
        	return arg;
        }
        
        public void setEventArgs(EventArgs value)
        {
        	arg = value;
        }
        
        /// <summary>
        /// The object used to encode messages.
        /// </summary>
        @Serialize
        public IEncoder getEncoder()
        {
            return _encoder; 
        }
        
        @Serialize
        public void setEncoder(IEncoder value)
        {
            _encoder = value; 
        }

        /// <summary>
        /// The frequency of the heartbeat.
        /// </summary>
        
        @Serialize
        public int getHeartbeatFrequency()
        {
            return _heartbeatFrequency; 
        }
        
        @Serialize
        public void setHeartbeatFrequency(Integer value)
        {
        	_heartbeatFrequency = value; 
        }
        

        /// <summary>
        /// The timeout of the heartbeat.
        /// </summary>
        @Serialize
        public int getHeartbeatTimeout()
        {
            return _heartbeatTimeout; 
        }
        
        @Serialize
        public void setHeartbeatTimeout(Integer value)
        {
        	_heartbeatTimeout = value; 
        }
       

        /// <summary>
        /// If heartbeats should be used.
        /// </summary>
        @Serialize
        public boolean getUseHeartbeats()
        {
            return _useHeartbeats; 
        }
        
        @Serialize
        public void setUseHeartbeats(boolean value)
        {
        	_useHeartbeats = value; 
        }
        /// <summary>
        /// Delegate called when a new Channel is available.
        /// </summary>
        @Serialize
        public Event getChannelAvailable()
        {
            return _channelAvailable; 
        }
        
        @Serialize
        public void setChannelAvailable(Event value)
        {
        	_channelAvailable = value; 
        }
        
        public boolean Start()
        {
            return true;
        }

        public void Stop()
        {
        }

		
       

    }
