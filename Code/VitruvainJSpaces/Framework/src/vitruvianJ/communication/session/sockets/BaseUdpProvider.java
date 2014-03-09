package vitruvianJ.communication.session.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;

import vitruvianJ.communication.Heartbeat;
import vitruvianJ.communication.channels.*;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;

import java.util.*;
import vitruvianJ.core.*;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.ReturnDelegate;


	/// <summary>
	/// Connects to a udp server, and maintains the connection through
    /// a heartbeat with the server.
	/// </summary>
    abstract public class BaseUdpProvider extends BaseChannelProvider implements ISocket    
	{
    	
        private final int INIT_SOCKET_TIME = 1000;

        private static JLogger _logger = new JLogger(BaseUdpProvider.class);

        private String _localAddress = "0";//IPAddress.Any.ToString();
        private int _localPort = 0;

        private String _remoteAddress = "0";//"0.0.0.0"; //IPAddress.Any.ToString();
		private int _remotePort = 0;

        private DatagramChannel _channel = null;
        private boolean _bindToSocket = true;

        private boolean _socketInitalized = false;
        private byte[] _buffer = new byte[0];

        private SignalSyncThread _bindSocketThread = null;

        public BaseUdpProvider()
        {
        	        	
            _bindSocketThread = new SignalSyncThread("BaseUdpProvider : Bind Socket Thread", new BindSocket(), true);
        }

        /// <summary>
        /// Bind to the local endpoint.
        /// </summary>
        @Serialize//(getName = "get")
        public boolean getBindToSocket()
        {
            return _bindToSocket; 
        }
        @Serialize//(getName = "set")
        public void setBindToSocket(boolean value){ 
        	_bindToSocket = value;         	
        }

        /// <summary>
        /// The local endpoint.  This allows IpTable initialization.
        /// </summary>
        
        //@IPTableFormatter
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
                    

      
      //[IPTableFormatter]
      @IPFormatter(getFormatter = "vitruvianJ.communication.session.sockets.IPTableFormatter")
      @Serialize//(getName = "get")
     public IPEndPoint getRemoteEndPoint()
     {
         return new IPEndPoint(_remoteAddress, _remotePort); 
     }
      
     @Serialize//(getName = "set")
     public void setRemoteEndPoint(IPEndPoint value)
     {
             _remoteAddress = value.getIPAddress();
             _remotePort = value.getPort();        
     }
            

        /// <summary>
        /// The remote address to send messages to.
        /// </summary>
       @Serialize//(getName = "get")
        public String getRemoteAddress()
        {
    	   return _remoteAddress; 
        }
       
       @Serialize//(getName = "set")
       public void setRemoteAddress(String value) 
       { 
    	   _remoteAddress = value; 
        }

        /// <summary>
        /// The remote port to send messages to.
        /// </summary>
       @Serialize//(getName = "get")
        public int getRemotePort()
        {
            return _remotePort; 
        }
        
       @Serialize//(getName = "set")
         public void setRemotePort(int value)
       {
             _remotePort = value; 
        }

        /// <summary>
        /// Send the message to the given end-point.
        /// </summary>
        /// <param name="message"></param>
        /// <param name="channel"></param>
        public void Send(IPEndPoint remoteEndPoint, UdpMessage message)
        {
        	if (!_socketInitalized)
                return;
            


            if (_channel != null)
            {
                byte[] bytes = message.ToBytes();

				try {
					InetSocketAddress address = new InetSocketAddress(remoteEndPoint.getIPAddress(), remoteEndPoint.getPort());
					//DatagramPacket sendPacket  = new DatagramPacket(bytes, bytes.length, address);
					 _logger.Debug("Send : BaseUdpProvider : hash code " + _channel.hashCode());
					 ByteBuffer bytesToSend = ByteBuffer.wrap(bytes);
					_channel.send(bytesToSend,address);// write(arg0);// socket().send(sendPacket);					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                //bytesSent = _socket.SendTo(bytes, bytes.length,0, getRemoteEndPoint());// SocketFlags.None, remoteEndPoint);

                if (_logger.IsDebugEnabled())
                    _logger.DebugFormat("Sent Message : ["+ message.getLocalChannelId() + " | " + message.getRemoteChannelId() + "]");
            }
        }

        abstract protected void MessageReceived(IPEndPoint remoteEndPoint, UdpMessage message);		

		/// <summary>
		/// Start the channel provider.
		/// </summary>
		/// <returns>True if successfully started, otherwise False.</returns>
		public boolean Start()
		{
            super.Start();
            try {
            		
				_channel = DatagramChannel.open();
				_channel.socket().bind(new InetSocketAddress(getLocalEndPoint().getIPAddress(), getLocalEndPoint().getPort() ) );
				SocketSelector.Add(this);
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}//AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);                
            try {
				_bindSocketThread.Start(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

		/// <summary>
		/// Stop the channel provider.
		/// </summary>
		public void Stop()
		{
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
				//_socket.shutdownInput();
				//_socket.shutdownOutput();//Shutdown(SocketShutdown.Both);
            	_channel.disconnect();
	            _channel.close();
	            _channel = null;
	            System.gc();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }

		

        /// <summary>
        /// The single socket for this provider.
        /// </summary>
        public VChannel getSocket()
        {
            return new VChannel().fromUdp(_channel); 
        }
         
        public void setSocket(DatagramChannel value) 
        {
        	_channel = value;
        }

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
				
				return BindSocket();
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
        
        private boolean BindSocket() throws Exception
        {
            try
            {
            	//_logger.Debug("!_socketInitialized : " + !_socketInitialized);
            	if(!_socketInitalized)
            	{
	                if ( _bindToSocket && !_channel.socket().isBound())// _channel == null)
	                {
	                	_channel = DatagramChannel.open();
	                	_logger.Debug("BindSocket : " + _channel.hashCode());
	    				_channel.socket().bind(new InetSocketAddress(getLocalEndPoint().getIPAddress(), getLocalEndPoint().getPort() ) );
	                }	
                	SocketSelector.Add(this);
                	_socketInitalized = true;
            	} 
            }
            catch(Exception e)
            {                
                if (_logger.IsDebugEnabled())
                    _logger.DebugFormat("Error initializing socket for "+ toString());           
            	_bindSocketThread.Sleep(INIT_SOCKET_TIME);
            	_bindSocketThread.Signal();

            } 
            return (_socketInitalized);
        }

        /// <summary>
        /// Read the data from the socket.
        /// </summary>
        public void Read()
        {        	
        	if(!_socketInitalized)
        		return;
            int available = -1;
			
				try {					
					available = _channel.socket().getReceiveBufferSize();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
            if (available <= 0)
                return;

            int numReceived;
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(available);
            IPEndPoint remoteEndPoint = new IPEndPoint(_remoteAddress, 0);

            try
            {
            	
                if (available > _buffer.length)
                {
                    if (_logger.IsDebugEnabled())
                        _logger.DebugFormat("Resizing receive buffer from "+_buffer.length+" bytes to "+available+" bytes.");

                    _buffer = new byte[available];
                }
                             
                byteBuffer.clear();                             
                _channel.receive(byteBuffer);                
                byteBuffer.flip();                
            }
            catch (Exception ex)
            {
                if (ex.hashCode() == 0x2746)
                { }
                else
                {
                    if (_logger.IsErrorEnabled())
                        _logger.ErrorFormat(ex, "Error while reading from "+ toString());
                }
                return;
            }
/*
            if (byteBuffer == null);// <= 0)
                return;
*/
            UdpMessage message = new UdpMessage();
                 
            int index = 0;
            while(byteBuffer.hasRemaining())
            {            	
            	_buffer[index++] =byteBuffer.get();
            }                    
        	
            byteBuffer.clear();
            message.FromBytes(_buffer,_buffer.length);// numReceived);

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Received Message : [ " + message.getRemoteChannelId()+" | "+message.getLocalChannelId()+" ]" );

            MessageReceived((IPEndPoint)remoteEndPoint, message);
        }
        
        
        
        /*public void Read()
        {
        	//_logger.Debug("TCP Channel : Read() ");
        	try
            {        
        	boolean moreData = true;	
            while (moreData)
            {
                int numReceived = 0;
                
                try
                {            	            	
                	// Read all available bytes
                	workingBuffer.clear();
                	//workingBuffer.flip();
                    numReceived = _socketChannel.read(workingBuffer);//ead(workingBuffer);
                    _logger.Debug(" Read(): TCPChannel - bytes received : " + numReceived );
                    if (numReceived==0)
                    	moreData = false;
                    else
                    {                
                		//workingBuffer.flip();
                    	while (workingBuffer.remaining()>0)
                    	{
                    		if (workingBuffer.remaining()<4)
                    			break;
                    	                         
//                    		int msgLength = workingBuffer.getInt();
//                    		if(msgLength <0)
//                    		{
//                    			msgLength = 255 - msgLength;
//                    		}
                    	//	_logger.Debug(" Message Length "+msgLength);
                    		
                    		workingBuffer.flip();
                    		byte[] headBuf = new byte[4];
                    		workingBuffer.get(headBuf, 0, headBuf.length);
                    	
                        	int msgLength = headBuf[0];// workingBuffer.getShort();	// Make sure that this reads the bytes in the right order
                        	if(msgLength <0)
                       		{
                        			msgLength = 256 + msgLength;
                       		}
                        		
                        	if (workingBuffer.remaining()<msgLength)
                        		break;
                        	
                            byte[] _buffer = new byte[msgLength];
                            
                            workingBuffer.get(_buffer, 0, _buffer.length);
                            
                            Object message = _encoder.ToObject(_buffer);
                           // _logger.Debug("Because msg length > 0 ," + message);

                            if (message.getClass().equals(Heartbeat.class))
                            {
                                _monitor.HeartbeatReceived();
                            }
                            else
                            {
                                if (getMessageReceived() != null)
                                {
                                	this.message = message;                            	
                                	getMessageReceived().RaiseEvent();
                                }
                            }
                            
                    	}
                    	if (workingBuffer.remaining()>0)
                    		workingBuffer.compact();                          		
                    }
                }
                catch (Exception ex)
                {
                    if (_logger.IsErrorEnabled())
                        _logger.ErrorFormat(ex, "Error while reading from %1$2s");

                    ThreadSafeClose();
                    return;
                }

                if (numReceived <= 0)
                    break;              
              
            }
           
            }
             catch (Exception e) {	
            	 _logger.Error("exception occured in the read method");
    		}
        }*/


        /// <summary>
        /// Handle an error on the socket.
        /// </summary>
        public void Error()
        {
        }

        public Channel getChannel(){
        	return _channel;
        	
        }

        /// <summary>
        /// String representation of this provider.
        /// </summary>
        /// <returns></returns>
        public String toString()
        {
            return String.format("Base UDP Provider -> Local {0}:{1} Remote {2}:{3}", _localAddress, _localPort, _remoteAddress, _remotePort);
        }

		public void setChannelAvailable(ChannelEventArgs channelEventArgs) {
			// TODO Auto-generated method stub
			
		}
    }

