package vitruvianJ.communication.session.sockets;


import java.rmi.Remote;
import java.util.*;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.*;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.ByteBufferConvertor;
import vitruvianJ.communication.DelayedSyncList;
import vitruvianJ.communication.ItemEventArgs;
import vitruvianJ.communication.session.protocols.IProcessor;
import vitruvianJ.core.*;
import vitruvianJ.delegate.Delegator;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
// import java.nio.ch.ServerSocketChannelImpl;



	/// <summary>
	/// Class that allows all Sockets to be on the same Select statement.
	/// </summary>
	public class SocketSelector
	{


		private enum ChannelCommand
		{
			RenewList(0), Exit(1);
			
	        private final int id;
			 
	        ChannelCommand(int id) { this.id = id;}
	 
	        public int getValue() { return id; }
		}

		private final static int ANY_PORT = 0;

        private static JLogger _logger = new JLogger(SocketSelector.class);

		private static DatagramChannel _commandSource = null;
		private static DatagramChannel _commandSink = null;
		
		private static Select select = new Select();
		private static SignalSyncThread _selectThread = new SignalSyncThread("Socket Selector", select);

        private static DelayedSyncList<ISocket> _sockets = new DelayedSyncList<ISocket>();

		private static Object _ccSyncObject = new Object();
		

		static void printSockets(DelayedSyncList<ISocket> p_sockets)
		{
			Iterator<ISocket> itr = p_sockets.iterator();
			String line = "Which socket is this : ";
			while(itr.hasNext())
			{
				ISocket socket = itr.next();
				line+= socket.getClass() + " " ;
			}
			_logger.Debug(line);
		}
		
		/// <summary>
		/// Add the socket to the list of known sockets.  This socket will be
		/// added to the group of sockets given to the Select statement.
		/// </summary>
		/// <param name="socket">The socket to add.</param>
        public static void Add(ISocket socket)
		{
//            if (_logger.IsDebugEnabled())
//                _logger.DebugFormat("Adding socket : %1$2s", socket.toString());            

            if (_sockets.Contains(socket))
                return;

            synchronized (_ccSyncObject)
            {
            	try{
                if (_sockets.getSyncCount() == 0)
                {
//                    if (_logger.IsDebugEnabled())
//                        _logger.DebugFormat("Starting Socket Selector");                    
                    
                     SocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost(),  ANY_PORT);
                     configureChannels(socketAddress);
                   
                    _sockets.Add(socket, null);
                    _selectThread.Start(true);                
                }
                else
                {
                    _sockets.Add(socket, null);
                    //_logger.Debug(" Sending Renew List ");
                    SendCommand(ChannelCommand.RenewList);
                }
            	}catch(Exception ex)
                {
            		ex.printStackTrace();
                }
            }
		}

        static void configureChannels(SocketAddress socketAddress)throws Exception
        {
        	
        	 _commandSink = DatagramChannel.open();
        	// _logger.Debug("sink is being binded to the socket address");
             _commandSink.socket().bind(socketAddress);
             //_logger.Debug("sink was binded successfully to the socket address");
             _commandSink.configureBlocking(false);                   
             _commandSource = DatagramChannel.open();
            // _logger.Debug("source is being binded to the socket address");
             _commandSource.socket().bind(socketAddress);
             //_logger.Debug("configureChannels: source was binded successfully to the socket address");
             _commandSource.connect(_commandSink.socket().getLocalSocketAddress());//LocalEndPoint);
             //source.configureBlocking(false);                         
        }
        
		/// <summary>
		/// Remove the socket from the list of known sockets.  This socket will be
		/// removed from the group of sockets given to the Select statement.
		/// </summary>
		/// <param name="socket"></param>
		public static void Remove(ISocket socket) throws Exception
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Removing socket : %1s ", socket.toString());

            if (!_sockets.Contains(socket))
                return;
			
            
            CleanSocket cleanSocket = new CleanSocket();
            _sockets.Remove(socket, cleanSocket);
            SendCommand(ChannelCommand.RenewList);

            synchronized (_ccSyncObject)
            {
                if (_sockets.getSyncCount() == 0)
				{
					if (_logger.IsDebugEnabled())
						_logger.DebugFormat("Shutting Down Socket Selector");

                    SendCommand(ChannelCommand.Exit);
                    
                    
					_selectThread.Stop();

					DisconnectChannels();
					System.gc();
				}
			}
		}

		static void DisconnectChannels() throws IOException
		{
			
			//if they are datagram channels
			_commandSource.disconnect();
			_commandSource.close();
			_commandSource = null;
			
			_commandSink.disconnect();
			_commandSink.close();
			_commandSink = null;
		}
		

		static class CleanSocket implements Delegate
		{
			public void CleanupSocket(ItemEventArgs<ISocket> e)
			{
				ISocket socket = e.getItem();
				socket.CleanupChannel();
			}

			@Override
			public void invoke(EventArgs args) {
				CleanupSocket((ItemEventArgs<ISocket>)args);			
			}
		}
		
		/// <summary>
		/// Send a command to the command socket.
		/// </summary>
		/// <param name="command">The command to send.</param>
		private static void SendCommand(ChannelCommand command) throws Exception
		{
            synchronized (_ccSyncObject)
            {
                if (_commandSource == null)
                    return;
                SendDataToChannel(command);
            }
		}

		static String printAddress(DatagramSocket socket)
		{
			DatagramSocket soc = socket;
			String localAddress = null, remoteAddress = null ;
			if(soc.getLocalAddress()!=null)
			{
				localAddress = soc.getLocalAddress() + " " + soc.getLocalPort();
			}
			if(soc.getRemoteSocketAddress()!=null)
				remoteAddress = soc.getRemoteSocketAddress().toString();
			return "Local Address : " + localAddress + " Remore address : " + remoteAddress;
		}
		
		
		static String printAddress(Socket socket)
		{
			Socket soc = socket;
			String localAddress = null, remoteAddress = null ;
			if(soc.getLocalAddress()!=null)
			{
				localAddress = soc.getLocalAddress() + " " + soc.getLocalPort();
			}
			if(soc.getRemoteSocketAddress()!=null)
				remoteAddress = soc.getRemoteSocketAddress().toString();
			return "Local Address : " + localAddress + " Remore address : " + remoteAddress;
		}
		
		static String printAddress(ServerSocket socket)
		{
			ServerSocket soc = socket;
			String localAddress = null, remoteAddress = null ;
			if(soc.getLocalSocketAddress()!=null)
			{
				localAddress = soc.getLocalSocketAddress() + " " + soc.getLocalPort();
			}			
			return "Local Address : " + localAddress ;
		}
		
		
		
		private static void SendDataToChannel(ChannelCommand command) throws IOException {
			
			//if it is a datagram channel
			byte[] commandBuffer = new byte[1];
			commandBuffer[0] = (byte)command.getValue();
			//_logger.Debug("SendDataToChannel : getting socketAddress from the _commandSink");
			SocketAddress address =  _commandSink.socket().getLocalSocketAddress();
			_commandSource.socket().send(new DatagramPacket(commandBuffer, 1,address));
		//	_logger.Debug("successfully sent the data from the commandSource at address " + printAddress(_commandSource.socket()));
		
			/*
			ByteBuffer buf = ByteBuffer.allocate(1024);
			buf.put(commandBuffer[0]);
			buf.flip();
			((SocketChannel)_commandSource).write(buf);
			//if it is a tcp channel
			*/
		}

		/// <summary>
		/// Select the list of sockets.  This causes a block on the OS, until data
		/// becomes available on sockets, or there are errors on the sockets.
		/// The block can be controlled through the command socket.
		/// </summary>
		
		static class Select implements Delegate
		{
			Select()
			{
				//_logger.Debug("Constructing an Select Delegate");
				//_logger.Debug(this.toString());
			}
			@Override
			public void invoke(EventArgs args) {
				// TODO Auto-generated method stub
				doSelect();				
			}
			
		}
		
		static void printKeys(Set<SelectionKey> keys, SelectionKey mykey)
		{
			Iterator<SelectionKey> itr =  keys.iterator();
			String line = "Keys : "  +mykey.hashCode() + " -> ";
			while(itr.hasNext())
			{
				line += "  " + itr.next().hashCode();
			}
			
			_logger.Debug(line);
		}
		
		private static void doSelect()
		{			
			int TIMEOUT = 60*1000*1000;
			ByteBuffer commandBuffer = ByteBuffer.allocate(1);
			boolean exit = false;
			Dictionary<Channel, ISocket> socketMap = new Hashtable<Channel, ISocket>();
			Selector selector;
			
			while (!exit)
			{		
			try
			{	
			_sockets.Lock();
			selector = Selector.open();
			_commandSink.register(selector, SelectionKey.OP_READ);// _commandSink.validOps());
			
				
				Iterator<ISocket> iterator = _sockets.iterator();
				while(iterator.hasNext())                
				{                	
					ISocket iSocket = iterator.next();     						
					SelectableChannel channel = (SelectableChannel) iSocket.getChannel();						
					channel.configureBlocking(false);
					channel.register(selector, channel.validOps());
					socketMap.put(channel, iSocket);						
				}
				
				int readyChannels = selector.select(TIMEOUT);
				if(readyChannels == 0) continue;
				
				Set<SelectionKey> keys = selector.selectedKeys();					
				
				for(Iterator<SelectionKey> i = keys.iterator(); i.hasNext();)
				{						
					SelectionKey key = (SelectionKey) i.next();
					//_logger.Debug(" SocketSelector found processing IO for channel : " + key.channel());
					i.remove();
					if(key == _commandSink.keyFor(selector))
					{
						if(key.isValid() && key.isReadable())
						{
							_commandSink.receive(commandBuffer);							
							byte[] array = commandBuffer.array();
							ChannelCommand command = ChannelCommand.values()[array[0]];
							if (command == ChannelCommand.Exit)
							{
								exit = true;								
							}							
						}
					}
					else
					{
						try
						{		 					
							if(key.isAcceptable() || key.isReadable())
							{
								//printKeys(keys, key);
								Channel selectableChannel = key.channel();														
								socketMap.get(selectableChannel).Read();
							}
						}
						catch (Exception ex)
						{
							if (_logger.IsErrorEnabled())
								_logger.ErrorFormat(ex, "Error in SocketSelector ISocket.Read function.");
							continue;
						}
					}				
				
			}
			}catch(Exception e)
			{
				_sockets.Unlock();
				selector = null;
				socketMap = new Hashtable<Channel, ISocket>();
					_logger.ErrorFormat(e, "Error in SocketSelector ISocket.Read function.");						
			}	
			_sockets.Unlock();
			socketMap = new Hashtable<Channel, ISocket>();
			}
		}
		
//		private static void doSelect()
//		{
//			// seconds -> milliseconds -> microseconds
//			int TIMEOUT = 60*1000*1000;
//
//			ByteBuffer commandBuffer = ByteBuffer.allocate(1);
//			boolean exit = false;
//
//
//			while (!exit)
//			{
//
//				Dictionary<Channel, ISocket> socketMap = new Hashtable<Channel, ISocket>();
//				// where r u adding _commandSink
//				Selector selector;
//				try
//				{
//					_sockets.Lock();
//					selector = Selector.open();
//					_commandSink.register(selector,  SelectionKey.OP_READ);//_commandSink.validOps());
//					printSockets(_sockets);
//					Iterator<ISocket> iterator = _sockets.iterator();
//					//_logger.Debug("Selector opened");
//					while(iterator.hasNext())                
//					{                	
//						ISocket iSocket = iterator.next();     						
//						SelectableChannel channel = (SelectableChannel) iSocket.getChannel();
//						
////						if(channel.getClass().getName().contains("ServerSocketChannel"))
////							_logger.Debug(printAddress(((ServerSocketChannel)channel).socket()) );							
////						else
////							_logger.Debug(printAddress(((SocketChannel)channel).socket()) );
//						channel.configureBlocking(false);
//						channel.register(selector, channel.validOps());
//						socketMap.put(channel, iSocket);						
//					}
//
//					//_logger.Debug("doSelect(): before Timeout : Keys : " + selector.keys().size());
//					selector.select(TIMEOUT);
//					
//				
//					Set<SelectionKey> keys = selector.selectedKeys();
//					_logger.Debug("doSelect() : Timeout: Keys : " + keys.size());
//					printKeys(keys,  _commandSink.keyFor(selector));
//					for(Iterator<SelectionKey> i = keys.iterator(); i.hasNext();)
//					{
//						
//						SelectionKey key = (SelectionKey) i.next();
//						//key.
//						i.remove();
//	
//						if(key == _commandSink.keyFor(selector))
//						{
//							//_logger.Debug("Keys matched ");
//							if(key.isValid() && key.isReadable()){
//						//	_logger.Debug("Selector received data from _commandSink : Keys : "+ selector.keys()+" Key " + key.toString());
//							_commandSink.receive(commandBuffer);
//							//((DatagramChannel)_commandSink).
//					//		_logger.Debug("Back from receive " + printAddress(_commandSink.socket()));												
//							//if (commandBuffer.remaining() >= 1)
//							{
//								byte[] array = commandBuffer.array();
//								ChannelCommand command = ChannelCommand.values()[array[0]];
//
//							//	_logger.Debug("Selector checking command type.  Keys : " + selector.keys());
//								if (command == ChannelCommand.Exit)
//								{
//									exit = true;
//								//	_logger.Debug("doSelect() : exit = true");
//								}
////								else if (command == ChannelCommand.RenewList)
////									_logger.Debug("doSelect(): renewList ");// + selector.keys());
//							}
//							}
//						}
//						else
//						{
//							//
//							try
//							{		 
//								if(key.isValid() && key.isReadable()){
//								Channel selectableChannel = key.channel();
//								//_logger.Debug("Keys not matched ");
////								if(selectableChannel.getClass().getName().contains("ServerSocketChannel"))
////									_logger.Debug(printAddress(((ServerSocketChannel)selectableChannel).socket()) );							
////								else
////									_logger.Debug(printAddress(((SocketChannel)selectableChannel).socket()) );
//								
//								socketMap.get(selectableChannel).Read();
//								}
//							}
//							catch (Exception ex)
//							{
//								if (_logger.IsErrorEnabled())
//									_logger.ErrorFormat(ex, "Error in SocketSelector ISocket.Read function.");
//								continue;
//							}
//						}
//					}		
//					selector.close();
//					//_logger.Debug("Selector closed. ");
//				}catch(Exception e)
//				{
//					_sockets.Unlock();
//					selector = null;
//					socketMap = new Hashtable<Channel, ISocket>();
//						_logger.ErrorFormat(e, "Error in SocketSelector ISocket.Read function.");						
//				}	
//				_sockets.Unlock();
//				socketMap = new Hashtable<Channel, ISocket>();
//			}
//		}		
	}
