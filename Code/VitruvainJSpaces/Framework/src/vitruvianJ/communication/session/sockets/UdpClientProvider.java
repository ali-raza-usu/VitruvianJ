package vitruvianJ.communication.session.sockets;

import java.net.*;
import java.nio.channels.Channel;

import vitruvianJ.communication.channels.*;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.EventRegistry;
import vitruvianJ.events.IEventSubject;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
import java.util.*;

	/// <summary>
	/// Connects to a udp server, and maintains the connection through
    /// a heartbeat with the server.
	/// </summary>
    public class UdpClientProvider extends BaseUdpProvider implements IEventSubject
	{
        private static JLogger _logger = new JLogger(UdpClientProvider.class);

        private String _localAddress = "0.0.0.0";//IPAddress.Any.ToString();
        private int _localPort = 0;

        private String _remoteAddress = "0.0.0.0";//IPAddress.Any.ToString();
		private int _remotePort = 0;

        private byte[] _buffer = new byte[0];

        private UdpChannel _channel = null;
        
        private ChannelClosed channelClosed = new ChannelClosed();

        /// <summary>
        /// Udp Client that provides a single socket connection.  All of the data is sent
        /// through a channel.  The channel sends/receives heartbeats to determine
        /// if it is still alive.
        /// </summary>
        public UdpClientProvider()
        {
        }

        /// <summary>
        /// Event called when the channel closes.
        /// </summary>
        /// <param name="args"></param>
        
        
        class ChannelClosed implements Delegate
        {
        
	        private void ChannelClosed(ChannelEventArgs args)
	        {
	            UdpChannel channel = (UdpChannel)args.getChannel();
	            channel.getChannelClosed().removeObservers(channelClosed);
	            CreateChannel();
	        }

			@Override
			public void invoke(EventArgs args) {
				ChannelClosed((ChannelEventArgs)args);
				
			}
        }

        
        /// <summary>
        /// Create a new channel.
        /// </summary>
        /// <returns></returns>
        private void CreateChannel()
        {
            IPEndPoint remoteEndPoint =  getRemoteEndPoint();
            UdpChannel channel = new UdpChannel(this, remoteEndPoint, getHeartbeatFrequency(), getHeartbeatTimeout());
            channel.setEncoder((IEncoder)_encoder.clone());
            channel.getChannelClosed().addObservers(channelClosed);
            _channel = channel;

            if (getChannelAvailable() != null)
                setChannelAvailable(new ChannelEventArgs(channel));
        }

        protected void MessageReceived(IPEndPoint remoteEndPoint, UdpMessage message)
        {
            _channel.Receive(message);
        }



		/// <summary>
		/// Start the channel provider.
		/// </summary>
		/// <returns>True if successfully started, otherwise False.</returns>
		public boolean Start()
		{
            super.Start();
            CreateChannel();
			return true;
		}

		/// <summary>
		/// Stop the channel provider.
		/// </summary>
		public void Stop()
		{
			//super;
			super.Stop();


            if (_channel != null)
            {
                _channel.getChannelClosed().addObservers(channelClosed);
                _channel.Close();
                _channel = null;
            }
		}



        /// <summary>
        /// String representation of this provider.
        /// </summary>
        /// <returns></returns>
        public String ToString()
        {
            return String.format("UDP Client Provider -> Local {0}:{1} Remote {2}:{3}", _localAddress, _localPort, _remoteAddress, _remotePort);
        }

		@Override
		public void CleanupChannel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Channel getChannel() {
			// TODO Auto-generated method stub
			return null;
		}

		
    }
