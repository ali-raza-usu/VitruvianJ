package vitruvianJ.communication.session.sockets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

import vitruvianJ.HiddenMessages;
import vitruvianJ.binary.ByteConvertor;
import vitruvianJ.communication.Heartbeat;
import vitruvianJ.communication.MessageTrace;
import vitruvianJ.communication.session.Message;
import vitruvianJ.distribution.sessions.messages.RequestBrokerId;
import vitruvianJ.events.Event;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.IEncoder;
import vitruvianJ.serialization.xml.XmlFramework;

public class TcpChannel extends BaseChannel implements ISocket
{
    private static JLogger _logger = new JLogger(TcpChannel.class);

    private SocketChannel _socketChannel = null;

    private byte[] _buffer = new byte[0];

    
    private ByteBuffer workingBuffer;
    
    
    /// <summary>
    /// Construct the channel.
    /// </summary>
    /// <param name="socket"></param>
    /// <param name="heartbeatFrequency"></param>
    /// <param name="heartbeatTimeout"></param>
    public TcpChannel(SocketChannel socketChannel, int heartbeatFrequency, int heartbeatTimeout, IEncoder encoder) throws SocketException
    {
        super(heartbeatFrequency, heartbeatTimeout, encoder); 
        _socketChannel = socketChannel;
        int bufferSize = _socketChannel.socket().getReceiveBufferSize();
        workingBuffer = ByteBuffer.allocateDirect(32767);       
       
    }



    public SocketChannel getSocket()
    {
        return _socketChannel; 
    }
    
    public static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        int j = 4;
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[--j] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
    
    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
    
    int available (Socket socket) throws Exception
    {
    	InputStream stream = socket.getInputStream();
    	DataInputStream dataStream = new DataInputStream(stream);
    	return dataStream.available();
		//return 0;
    	
    }
    
/*
    public void Read()
    {
	try
    {
    	_buffer = new byte[4];
    	int numReceived = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_buffer.length);
        byteBuffer.clear();
        numReceived = _socketChannel.read(byteBuffer);
        _logger.Debug(" bytes received " + numReceived);
        byteBuffer.flip();
        
        while (numReceived > 0)
        {            
            try
            {
            	_logger.Debug(_rxState);
                switch (_rxState)
                {
                    case Length:
                        {                           
                            if(numReceived > 0)
                            	byteBuffer.get(_buffer, 0, _buffer.length);                                                        
                            _msgLength =   _buffer[0];                           

                            if (_msgLength > _socketChannel.socket().getReceiveBufferSize())
                                _socketChannel.socket().setReceiveBufferSize(_msgLength);

                            _rxState = RxState.Message;
                            break;
                        }
                    case Message:
                        {
                            if (numReceived < _msgLength || _msgLength <= 0)
                                break;

                            if (_buffer.length < _msgLength)
                            {
                                _buffer = new byte[_msgLength];
                            }
                            
                            
                            byteBuffer = ByteBuffer.allocate(_msgLength);                            
                            byteBuffer.clear();
                            numReceived = _socketChannel.read(byteBuffer);
                            byteBuffer.flip();
                            
                            if(numReceived > 0)
                            	byteBuffer.get(_buffer, 0, _buffer.length);
                            
                            _rxState = RxState.Length;

                            Object message = _encoder.ToObject(_buffer);

	
	                            if (message.getClass().equals(Heartbeat.class))
	                            {
	                                _monitor.HeartbeatReceived();
	                            }
	                            else
	                            {
	                                if (getMessageReceived() != null)
	                                	getMessageReceived().RaiseEvent();
	                            }
                            
                            break;
                        }
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
    }
*/
    
    public void Read()
    {
    //	_logger.Debug("TCP Channel : Read() ");
    	
    	try
        {        
    	boolean moreData = true;	
        while (moreData)
        {
            int numReceived = 0;
            
            try
            {            	            	
            	// Read all available bytes
           // 	_logger.Debug("TCP Channel : Read all available bytes ");
            	workingBuffer.clear();
            	//workingBuffer.flip();
            //	
            		//_logger.Debug("TCP Channel : Socket Channel is  " + _socketChannel);
            	//try{
                numReceived = _socketChannel.read(workingBuffer);
            	//}catch(Exception e)
            	//{
            	//	_logger.Debug("Error in Socket Channel : " + e.getMessage());            	     
            	//}
            //    _logger.Debug(" Read(): TCPChannel - bytes received : " + numReceived );
                if (numReceived==0)
                	moreData = false;
                else
                {                
            		workingBuffer.flip();
                	while (workingBuffer.remaining()>0)
                	{
                		if (workingBuffer.remaining()<4)
                			break;
                	                         
               		
                		//workingBuffer.flip();
          //      		_logger.Debug(" Remaining bytes : " + workingBuffer.remaining() );
                		byte[] headBuf = new byte[4];
                		workingBuffer.get(headBuf, 0, headBuf.length);
                	    
                		
                    	int msgLength = headBuf[0];// workingBuffer.getShort();	// Make sure that this reads the bytes in the right order
                    	
                    	int[] lengthBuf = new int[headBuf.length];
                    	
                    	for(int i = 0; i < headBuf.length; i++)
                    	{
                    		if(headBuf[i] < 0)
                    		lengthBuf[i] = 256 + headBuf[i];
                    		else
                    			lengthBuf[i] = headBuf[i];
                    	}
//                    	if(msgLength <0)
//                   		{
//                    			msgLength = 256 + msgLength;
//                   		}
                    		
                    	msgLength = lengthBuf[0];
                    	if(msgLength == 194)
                    		msgLength = 194;
                    	headBuf = ByteConvertor.shuffleBytes(headBuf);
                    	msgLength = ByteConvertor.byteArrayToInt(headBuf,0, headBuf.length);
                    	if (workingBuffer.remaining()<msgLength)
                    		break;
                    	
                        byte[] _buffer = new byte[msgLength];
                        
                        if(_buffer.length  ==  706)
                        	msgLength = msgLength;
                        workingBuffer.get(_buffer, 0, _buffer.length);
                        
                        Object message = _encoder.ToObject(_buffer);
                        try{
                        	Message msg = (Message)message;
                        	 if (_logger.IsDebugEnabled() && !HiddenMessages.getMsgList().contains(message.getClass()))
                        		 _logger.Debug("Message Received : " + msg + " Local Socket " + _socketChannel.socket().getLocalPort() + " Remote Socket " + _socketChannel.socket().getRemoteSocketAddress());
                        }catch(Exception e)
                        {
                        	_logger.Debug("Error in type casting of message in TCPChannel.Read() ");
                        }
                      // _logger.Debug("Because msg length > 0 ," + message);

                        if (message.getClass().equals(Heartbeat.class))
                        {
                        	Heartbeat heartBeat = (Heartbeat)message;
                       // 	_logger.Debug("Receiving Heatbeat : Message ID " + heartBeat.getMessageId() + " Message Type Id " + heartBeat.getMessageTypeId());	
                            _monitor.HeartbeatReceived();
                        }
                        else
                        {
                    //    	_logger.Debug(" if (getMessageReceived() != null)");
                        	
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
             //   if (_logger.IsErrorEnabled())
            	_logger.Debug(" Error in TcpChannel.Read() ");
            	ex.printStackTrace();
                    _logger.Debug(ex.getStackTrace().toString());

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
    }

    public void Error()
    {
    }

    public void CleanupSocket()
    {
        try {
			_socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Shutdown(SocketShutdown.Both);
        //_socket.Close();
        _socketChannel = null;
        System.gc();//GC.Collect();
    }

   

   public void Open()
    {
        //lock (_syncObject)
        {
            if (!IsOpen())
            {
                super.Open();
                SocketSelector.Add(this);
            }
        }
    }

    public void Close()
    {
        //lock (_syncObject)
        {
            if (IsOpen())
            {                
                try {
                	close();
					SocketSelector.Remove(this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }

    public void Send(Object message)
    {
        try
        {
            int bytesSent = 0;
            Message msg = (Message)message;
            if(msg.getMessageTypeId() == 11){         
	               _logger.Debug("Message Sent : " + msg + " bytes sent : " + bytesSent );
			}

            if(message!= null && (message.getClass().getName().equals(RequestBrokerId.class.getName())))
            	bytesSent = 0;
            byte[] bytes = _encoder.ToBytes(message);
            int msgLength = bytes.length;
           // _logger.Debug(message + " is the message : length :  " +msgLength );
            if (_socketChannel.socket().getSendBufferSize() > msgLength)
                _socketChannel.socket().setSendBufferSize(msgLength);

            byte[] lenBytes = intToByteArray(msgLength);
            byte[] allBytes = new byte[msgLength + 4];
            
            System.arraycopy(lenBytes, 0, allBytes, 0, lenBytes.length);
            System.arraycopy(bytes, 0, allBytes, 4, bytes.length);            

            ByteBuffer bytesToSend = ByteBuffer.wrap(allBytes);
            
           
            
			bytesSent = _socketChannel.write(bytesToSend);
			if(msg.getMessageTypeId() == 9){         
	               _logger.Debug("Message Sent : " + msg + " bytes sent : " + bytesSent );
			}

//            if (_logger.IsDebugEnabled())
//                _logger.DebugFormat("Sent message : {0}", ToString());
//
//            if (MessageTrace.getLogger().IsDebugEnabled())
//                MessageTrace.getLogger().DebugFormat("Sent : {0}", message);
        }
        catch (Exception ex)
        {
            if (_logger.IsErrorEnabled())
                _logger.ErrorFormat(ex, "Error sending message ");

            ThreadSafeClose();
        }
    }



    public String ToString()
    {

		if (_socketChannel != null)
        {
            String local = String.format("%1s:%2s", ((InetSocketAddress)_socketChannel.socket().getLocalSocketAddress()).getAddress().getHostAddress(), ((InetSocketAddress)_socketChannel.socket().getLocalSocketAddress()).getPort());
            String remote = String.format("%1s:%2s", ((InetSocketAddress)_socketChannel.socket().getRemoteSocketAddress()).getAddress().getHostAddress(), ((InetSocketAddress)_socketChannel.socket().getRemoteSocketAddress()).getPort());
            return String.format("Local -> %1s | Remote -> %2s", local, remote);
        }
        else
            return String.format("Local -> Unknown | Remote -> Unknown");
    }



	@Override
	public void CleanupChannel() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Channel getChannel() {
		// TODO Auto-generated method stub
		return _socketChannel;
	}



	@Override
	public Event getChannelMessageReceived() {
		// TODO Auto-generated method stub
		return getMessageReceived();
		//return null;
	}
}