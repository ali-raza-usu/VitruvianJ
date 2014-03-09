package vitruvianJ.communication.session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import vitruvianJ.communication.HeartbeatMonitor;
import vitruvianJ.communication.channels.ChannelEventArgs;
import vitruvianJ.communication.channels.IChannel;
import vitruvianJ.communication.session.MessageEventArgs;
import vitruvianJ.content.ContentManager;
import vitruvianJ.core.SignalSyncThread;
//import vitruvianJ.delegate.IDelegate;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.IEventSubject;
import vitruvianJ.events.ReturnDelegate;

import vitruvianJ.logging.*;
import vitruvianJ.serialization.IEncoder;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.services.ServiceRegistry;


	/// <summary>
	/// An object that encapsulates communication through Channels.
	/// </summary>
	public abstract class Session implements IEventSubject
	{
        /// <summary>
        /// The default timeout to use.
        /// </summary>
		private HeartbeatMonitor _monitor = null;

        public Session()
        {
           
        }

		
        static public int TIMEOUT = 1000;
		
		private static JLogger _logger = new JLogger(Session.class);

		private SignalSyncThread _processingThread = null;

		private Queue _messageQueue =  new LinkedList();
		//Queue.Synchronized Queue.Synchronized(new Queue());

		public Delegate MessageEvent;
		public MessageReceived Delegate_MessageReceived = new MessageReceived();
		ChannelClosed channelClosed = new ChannelClosed();
		protected  IChannel _channel = null;
		protected IEncoder _encoder = null;
        protected boolean _isRunning = false;

		/// <summary>
		/// Event called when the session is closed.
		/// </summary>
		public Event SessionClosed = new Event(this);

		private SignalSyncThread CreateProcessingThread()
		{
			//wrong way to build
			return new SignalSyncThread(String.format("Session Processing Thread"), new ProcessMessages() , true);
		}

		/// <summary>
		/// Configure the session given the persistence id.
		/// </summary>
		/// <param name="persistId">The persistence id to configure the session with.</param>
        public void Configure(String contentPath)
		{
            try
            {
                ContentManager manager = (ContentManager)(ServiceRegistry.getPreferredService(ContentManager.class));
                manager.Load(contentPath, this);
            }
            catch(Exception e)
            {
                if (_logger.IsWarnEnabled())
                    _logger.WarnFormat("Unable to configure the session using the content path : %1$2s", contentPath);
            }
		}

		/// <summary>
		/// The channel the session should use to communicate over.
		/// </summary>
		public IChannel getChannel()
		{
			return _channel;
		}
		
		public void setChannel(IChannel value)
		{ 
			_channel = value;
		}

		/// <summary>
		/// Start the session.  The channel should already be opened.
		/// </summary>
		public  void Start()
		{
            if (!_isRunning)
            {
            	try{
                _isRunning = true;

                _processingThread = CreateProcessingThread();
                _channel.getChannelMessageReceived().addObservers(Delegate_MessageReceived);
                _channel.getChannelClosed().addObservers(channelClosed);
                _channel.Open();

                _processingThread.Start(false);
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            }
		}

		/// <summary>
		/// Stop the session and close the channel.
		/// </summary>
		public  void Stop()
		{
            if (_isRunning)
            {
                _isRunning = false;

                _channel.getChannelMessageReceived().removeObservers(Delegate_MessageReceived);//.  = null;//_channel.MessageReceived.-= MessageReceived;
                _channel.getChannelClosed().removeObservers(channelClosed);
                _channel.Close();

                if (_processingThread.IsRunning())
					try {
						_processingThread.Stop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            }
		}

		/// <summary>
		/// Check the thread to avoid blocking the processing thread.
		/// </summary>
		protected void BeforeSend()
		{
			if (_processingThread == null)
				return;
            
			// create a new processing thread if there is a chance
			// that the main processing thread could be blocked
			if (_processingThread.IsRunThread())
			{
				synchronized (_processingThread) {									
//                    if (_logger.IsDebugEnabled())
//                        _logger.Debug("Creating new processing thread, because of potential deadlock");

					_processingThread = CreateProcessingThread();
					try {
						_processingThread.Start(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		/// <summary>
		/// Send the message.
		/// </summary>
		/// <param name="message">The message to send.</param>
		public abstract void Send(Object message);

		/// <summary>
		/// Process a message.
		/// </summary>
		/// <param name="message">The message to process.</param>
		protected abstract void Process(Object message);

		/// <summary>
		/// Allow inheritors to preview messages as they are received.
		/// </summary>
		/// <param name="message">The message to preview</param>
		/// <returns>True if the message has handled, otherwise false and
		/// the message will be handled through normal the processing thread.</returns>
		protected boolean PreviewMessage(Object message)
		{
			return false;
		}

		/// <summary>
		/// Function called when the channel receives a message.
		/// </summary>
		/// <param name="args">The message that was received.</param>
		/// <note>The message will not be processed here, but will be
		/// placed on a queue to be processed by another thread.</note>
		protected class MessageReceived implements Delegate
		{
			@Override
			public void invoke(EventArgs args) {
				MessageReceived((MessageEventArgs)args);			
			}		
		}
		
		private void MessageReceived(MessageEventArgs args)
		{
//            if (_logger.IsDebugEnabled())
//                _logger.DebugFormat("Receive : %1s ", args.getMessage().toString());

			if (!PreviewMessage(args.getMessage()))
			{
				_messageQueue.add(args.getMessage());

                synchronized (_processingThread) // thread-safe
				{
					_processingThread.Signal();
				}
			}
		}

		/// <summary>
		/// Process messages in the queue.
		/// </summary>
		//wrong way to build
		class ProcessMessages implements ReturnDelegate
		{

		
					@Override
			public boolean invoke() {
				return ProcessMessages();
			}

			@Override
			public void invoke(EventArgs args) {
				// TODO Auto-generated method stub
				
			}
			
		}
		private boolean ProcessMessages()
		{
			SignalSyncThread thread = _processingThread;
				
			while (_messageQueue.size() > 0)
			{
				Object message = _messageQueue.poll();
				Message msg = (Message)message;
            	if(msg.getMessageTypeId() == 9)
            		msg = null;

//                if (_logger.IsDebugEnabled())
//                    _logger.DebugFormat("Process : {0}", message.toString());

				try
				{
					Process(message);

					// exit this thread if the processing thread changed.
					if (thread != _processingThread)
						return true;
				}
				catch (Exception ex)
				{
					if (_logger.IsErrorEnabled())
						 _logger.ErrorFormat("Unable to configure the session using the content path : ", "");
						
				}
			}

			return false;
		}

		/// <summary>
		/// Handle the channel closed event.
		/// </summary>
		/// <param name="args">The channel that was closed.</param>
		
		class ChannelClosed implements Delegate
		{
		private void channelClosed(ChannelEventArgs args)
		{
            Stop();

			if (SessionClosed != null)
				SessionClosed.RaiseEvent();//((new SessionEventArgs(this));
		}

		@Override
		public void invoke(EventArgs args) {
			channelClosed((ChannelEventArgs)args);
			
		}
		}
		
		
		 /// <summary>
        /// The channel the session should use to communicate over.
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
        /// If heartbeats should be used.
        /// </summary>
        @Serialize
        public boolean getUseHeartbeats()
        {
            return _monitor.getEnabled(); 
        }
        
        @Serialize
        public void setUseHeartbeats(boolean value) 
        { 
        	_monitor.setEnabled(value);
        }

        
        
        /// <summary>
        /// The frequency of the heartbeat.
        /// </summary>
        @Serialize
        public int getHeartbeatFrequency()
        {
            return _monitor.getFrequency(); 
        }
        
        @Serialize
        public void setHeartbeatFrequency(int value) 
        { 
        	_monitor.setFrequency(value); 
        }

        
        /// <summary>
        /// The timeout of the heartbeat.
        /// </summary>
        @Serialize
        public int getHeartbeatTimeout()
        {
            return _monitor.getTimeout(); 
        }
            
        @Serialize
        public void setHeartbeatTimeout(int value)
        { 
        	_monitor.setTimeout(value);
        }
	}
