package vitruvianJ.communication.session.sockets;

import java.io.IOException;
import java.net.*;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import vitruvianJ.communication.channels.*;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
import vitruvianJ.serialization.IPFormatter;

import java.util.*;
import vitruvianJ.core.*;
import vitruvianJ.delegate.Delegator;
import vitruvianJ.delegate.IDelegate;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Event;
import vitruvianJ.events.IEventSubject;
import vitruvianJ.events.ReturnDelegate;


	/// <summary>
	/// Provider for IP communication.
	/// </summary>
    public abstract class BaseTcpServerProvider extends BaseChannelProvider implements ISocket, IEventSubject
	{
        private static JLogger _logger = new JLogger(BaseTcpServerProvider.class);
        private static String IPAddress = "0.0.0.0";

        private final int BIND_SOCKET_TIME = 1000;

        private SignalSyncThread _bindSocketThread = null;
        private ServerSocketChannel _serverchannel = null;

        private String _localAddress = IPAddress;
        private int _localPort = 0;
        
        IChannel channel = null;

        //Delegator bindSocket = new Delegator(null, Void.TYPE, this, "BindSocket");
        Event bindSocket = new Event(this);//null, Void.TYPE, this, "BindSocket");
        
        public BaseTcpServerProvider()
        {
        	        	
            _bindSocketThread = new SignalSyncThread("BaseTcpServerProvider : Bind Socket Thread", new BindSocket(), true);
        }

        public BaseTcpServerProvider(IEncoder encoder)
        {
        	_bindSocketThread = new SignalSyncThread("BaseTcpServerProvider : Bind Socket Thread", new BindSocket(), true);        	
            _encoder = encoder;
        }

        public ServerSocketChannel getChannel()
        {
        	return _serverchannel;
        }
        
        /// <summary>
        /// The local endpoint.  This allows IpTable initialization.
        /// </summary>
        
        //[IPTableFormatter]
        @IPFormatter(getFormatter = "vitruvianJ.communication.session.sockets.IPTableFormatter")
        @Serialize//(getName = "get")        
       public IPEndPoint getLocalEndPoint()
       {
           return new IPEndPoint(_localAddress, _localPort); 
       }
        
       @Serialize//(getName = "set")
       public void setLocalEndPoint(IPEndPoint value)
       {
               _localAddress = value.getIPAddress();
               _localPort = value.getPort();        
       }
              

        /// <summary>
        /// The local address to bind the socket to.
        /// </summary>
       @Serialize//(getName = "get")
       public String getLocalAddress()
       {
           return _localAddress;
       }
       @Serialize//(getName = "set")
       public void setLocalAddress(String value)
       {
           _localAddress = value;         
       }
              

        /// <summary>
        /// The local port to bind the socket to.
        /// </summary>
       @Serialize//(getName = "get")
       public int getLocalPort()
       {
          return _localPort; 
       }
       
       @Serialize//(getName = "set")
       public void  setLocalPort(int value)
       { 
       	_localPort = value; 
       }
       
        

        abstract protected ServerSocketChannel CreateSocket();




        /// <summary>
        /// Initialize the socket.  If initialization fails, then it will try again later.
        /// This could happen if the network isn't present when trying to bind.
        /// </summary>
        

        class BindSocket implements ReturnDelegate
        {

			@Override
			public boolean invoke() {
				// TODO Auto-generated method stub
				try{
				return bindSocket();
				}catch(Exception e)
				{
					return false;
				}
			}

			@Override
			public void invoke(EventArgs args) {
				// TODO Auto-generated method stub
				
			}
        	
        }
        	
        	private boolean bindSocket() throws Exception
            {
                try
                {
                	_serverchannel = ServerSocketChannel.open();
                	SocketAddress socketAddress = new InetSocketAddress(getLocalEndPoint().getIPAddress(), getLocalEndPoint().getPort());
                    _serverchannel.socket().bind(socketAddress);
                    _serverchannel.configureBlocking(false);
                    _serverchannel.open();// accept(); //accept is equivalent to listen in ServerSocketChaneel
                    // Find the corred
                  //  _socket.Listen(Int32.MaxValue);
                    SocketSelector.Add(this);
                    return true;
                }
                catch (Exception ex)
                {
                    if (_logger.IsDebugEnabled())
                        _logger.DebugFormat("Error initializing socket for  : %1$2s .\r\n ", ex);

                    _bindSocketThread.Sleep(BIND_SOCKET_TIME);
                    _bindSocketThread.Signal();
                    return false;
                }
            }
        
        

        abstract protected IChannel CreateChannel(SocketChannel socket);

        public static String captureThreadDump()
    	{
	    	Map allThreads = Thread.getAllStackTraces();
	    	Iterator iterator = allThreads.keySet().iterator();
	    	StringBuffer stringBuffer = new StringBuffer();
		    	while(iterator.hasNext())
		    	{
			    	Thread key = (Thread)iterator.next();
			    	StackTraceElement[] trace = (StackTraceElement[])allThreads.get(key);
			    	stringBuffer.append(key+"\r\n");
		    	for(int i = 0; i < trace.length; i++)
		    	{
		    		stringBuffer.append(" "+trace[i]+"\r\n");
		    	}
		    		stringBuffer.append("");
		    	}
	    	return stringBuffer.toString();
    	}
        
        public void Read()
        {
        //	_logger.Debug("Base Server Provider: Read() ");
        //	_logger.Debug(captureThreadDump());
            SocketChannel socket = null;
            

            try{
          //  _logger.Debug("BaseTcpServerProvider : LocalPort" + _serverchannel.socket().getLocalPort() + "SocketChannel Parameter : Remote Address : " + socket.socket().getRemoteSocketAddress());
            }catch(Exception e){}
            try {
            	
				socket = _serverchannel.accept();
				//_logger.Debug("BaseTcpServerProvider:Read() After : Local Port : " + socket.socket().getLocalPort() + " Remote Port : " + socket.socket().getRemoteSocketAddress());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if (_logger.IsErrorEnabled())
					_logger.ErrorFormat(e, "_serverchannel.accept() error.");
			}

            channel = CreateChannel(socket);
            if (channel == null)
                return;

      //      channel.setEncoder((IEncoder)getEncoder().clone());

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Channel Created :  %1$2s", channel.toString());

            if (_channelAvailable != null)
            	getChannelAvailable().RaiseEvent();
                //setChannelAvailable(bindSocket);
        }

        public void Error()
        {
        }


        public boolean Start()
        {
            super.Start();

            _serverchannel = CreateSocket();
            try {
				_bindSocketThread.Start(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            return true;
        }

        public void Stop()
        {
            super.Stop();

            if (_bindSocketThread.IsRunning())
				try {
					_bindSocketThread.Stop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            try {
				SocketSelector.Remove(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        public void CleanupSocket()
        {
            try {
				_serverchannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				 _logger.Debug("BaseTcpServerProvider : CleanupSocket() : Exception Occured ");
				e.printStackTrace();
			}
            _serverchannel = null;
            //_logger.Debug("BaseTcpServerProvider : CleanupSocket");
            System.gc();
        }

        public EventArgs getEventArgs(){
        	return new ChannelEventArgs(channel);
        }
    }
