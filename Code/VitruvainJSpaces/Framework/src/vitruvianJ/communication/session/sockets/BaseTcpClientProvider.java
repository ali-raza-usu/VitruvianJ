package vitruvianJ.communication.session.sockets;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import vitruvianJ.communication.channels.*;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
import vitruvianJ.core.*;
import java.lang.Void;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.EventRegistry;
import vitruvianJ.events.IEventSubject;

	/// <summary>
	/// Provider for IP communication.
	/// </summary>
    public abstract class BaseTcpClientProvider extends BaseChannelProvider implements IEventSubject
	{
    	private static String IPAddress = "0.0.0.0";
        private static JLogger _logger = new JLogger(BaseTcpClientProvider.class);

        private final int CONNECT_SOCKET_TIME = 1000;

        private String _localAddress = "127.0.0.1";//IPAddress.Any.ToString();
        private int _localPort = 2600;

        private String _remoteAddress = "127.0.0.1";//"0.0.0.0"; //IPAddress.Any.ToString();
		private int _remotePort = 2600;
		/*
        private String _localAddress = IPAddress;//.Any.ToString();
        private int _localPort = 0;

        private String _remoteAddress = IPAddress;//.Any.ToString();
        private int _remotePort = 0;
		 */
        private SignalSyncThread _connectThread = null;
        private IChannel _channel = null;
        
        Event ChannelHandler = new Event(this);
        ChannelClosed channelClosed = new ChannelClosed();
        

        public BaseTcpClientProvider()
        {
        	EventRegistry.getInstance().addEvent("ChannelHandler", ChannelHandler);
            _connectThread = new SignalSyncThread("BaseTcpClientProvider : Connect Thread", new Connect());
        }

        
        public BaseTcpClientProvider(IEncoder encoder)
        {
            _connectThread = new SignalSyncThread("BaseTcpClientProvider : Connect Thread", new Connect());
            _encoder = encoder;
        }

        /// <summary>
        /// The local endpoint.  This allows IpTable initialization.
        /// </summary>
       // [IPTableFormatter]
        @IPFormatter(getFormatter = "vitruvianJ.communication.session.sockets.IPTableFormatter")
         @Serialize
        public IPEndPoint getLocalEndPoint()
        {
            return new IPEndPoint(_localAddress, _localPort); 
        }
        @Serialize
        public void setLocalEndPoint(IPEndPoint value)
        {
                _localAddress = value.getIPAddress();
                _localPort = value.getPort();        
        }

        /// <summary>
        /// The local address to bind the socket to.
        /// </summary>
        @Serialize
        public String getLocalAddress()
        {
            return _localAddress;
        }
        @Serialize
        public void setLocalAddress(String value)
        {
            _localAddress = value;         
        }

        /// <summary>
        /// The local port to bind the socket to.
        /// </summary>
        @Serialize
        public int getLocalPort()
        {
           return _localPort; 
        }
        
        @Serialize
        public void  setLocalPort(int value)
        { 
        	_localPort = value; 
        }

        /// <summary>
        /// The remote endpoint.  This allows IpTable initialization.
        /// </summary>
       // [IPTableFormatter]
        @IPFormatter(getFormatter = "vitruvianJ.communication.session.sockets.IPTableFormatter")
         @Serialize
        public IPEndPoint getRemoteEndPoint()
        {
            return new IPEndPoint(_remoteAddress, _remotePort); 
        }
        
        @Serialize
        public void setRemoteEndPoint(IPEndPoint value)
        {
                _remoteAddress = value.getIPAddress();
                _remotePort = value.getPort();        
        }

        /// <summary>
        /// The remote address to send messages to.
        /// </summary>
        @Serialize
        public String getRemoteAddress()
        {
            return _remoteAddress; 
        }
        
        @Serialize
        public void setRemoteAddress(String value) 
        { _remoteAddress = value; 
        }

        /// <summary>
        /// The remote port to send messages to.
        /// </summary>
        @Serialize
        public int getRemotePort()
        {
            return _remotePort; 
        }
        
        @Serialize
        public void  setRemotePort(int value) 
        {
        	_remotePort = value; 
        }        

        /// <summary>
        /// Event called when a pipe closes.
        /// </summary>
        /// <param name="args"></param>
        class ChannelClosed implements Delegate
        {
        private void getChannelClosed(ChannelEventArgs args)
        {
            _channel.getChannelClosed().removeObservers(channelClosed);// -= ChannelClosed;
            _channel = null;
            _connectThread.Signal();
        }

		@Override
		public void invoke(EventArgs args) {
			getChannelClosed((ChannelEventArgs)args);
			
		}
        }

        abstract protected SocketChannel CreateSocketChannel();

        abstract protected IChannel CreateChannel(SocketChannel socketChannel);

        /// <summary>
        /// Connect to the server.
        /// </summary>
        
        class Connect  implements Delegate
        {
        	public void invoke(EventArgs args)
        	{
        		try {
					getConnected();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        	}
        	
        	private void getConnected() throws Exception
        	{
        		SocketChannel socketChannel = null;

                try
                {
                    socketChannel = CreateSocketChannel();
                    SocketAddress socketAddress = new InetSocketAddress(getRemoteEndPoint().getIPAddress(), getRemoteEndPoint().getPort());
                    socketChannel.connect(socketAddress);                
                }
                catch(Exception e)
                {
                    _connectThread.Sleep(CONNECT_SOCKET_TIME);
                    _connectThread.Signal();
                    return;
                }

                try
                {
                    _channel = CreateChannel(socketChannel);
                    try{
                    _channel.setEncoder((IEncoder)getEncoder().clone());
                    }catch(Exception e){}
                    //ChannelHandler  = new Delegator(new Class[]{ChannelEventArgs.class}, Void.TYPE, this, "ChannelClosed");
                    _channel.getChannelClosed().addObservers(channelClosed);

                    if (_logger.IsDebugEnabled())
                        _logger.DebugFormat("Channel Created : %1s", _channel.toString());

                    if (_channelAvailable != null)                    	
                        getChannelAvailable().RaiseEvent();// setChannelAvailable(ChannelHandler);

                    // sleep now, so that channel open/closing
                    // can't happen at high frequencies                    
                    //_connectThread.Sleep(CONNECT_SOCKET_TIME);
                }
                catch(Exception e)
                {
                    socketChannel.close();
                    socketChannel = null;
                    System.gc();

                    _connectThread.Sleep(CONNECT_SOCKET_TIME);
                    _connectThread.Signal();
                    return;
                }
            }

        		
        	
        }
        


        public boolean Start()
        {
            super.Start();
            try{
            _connectThread.Start(true);
            }catch(Exception e){}
            return true;
        }

        public void Stop()
        {
            super.Stop();

            try {
				_connectThread.Stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            if (_channel != null)
            {
                _channel.getChannelClosed().removeObservers(channelClosed);// -= ChannelClosed;
                _channel.Close();
                _channel = null;
            }
        }

        public EventArgs getEventArgs(){        	
        	return  new ChannelEventArgs(_channel);
        }
    }
