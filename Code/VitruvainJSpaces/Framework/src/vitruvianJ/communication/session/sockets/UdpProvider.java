package vitruvianJ.communication.session.sockets;

import java.net.*;
import java.nio.channels.Channel;

import vitruvianJ.communication.channels.*;

import vitruvianJ.delegate.IDelegate;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
import java.util.*;

	/// <summary>
	/// Connects to another udp provider, and maintains the connection through
    /// a heartbeat with the other provider.
	/// </summary>
    public class UdpProvider extends BaseUdpProvider
	{
        private static JLogger _logger = new JLogger(UdpProvider.class);

        private String _localAddress = "0.0.0.0";
        private int _localPort = 0;

        private String _remoteAddress = "0.0.0.0";
		private int _remotePort = 0;

        private byte[] _buffer = new byte[0];

        private UdpChannel _channel = null;
        
        ChannelClosed channelClosed = new ChannelClosed();

        /// <summary>
        /// Udp Client that provides a single socket connection.  All of the data is sent
        /// through a channel.  The channel sends/receives heartbeats to determine
        /// if it is still alive.
        /// </summary>
        public UdpProvider()
        {}

        /// <summary>
        /// Event called when the channel closes.
        /// </summary>
        /// <param name="args"></param>
        class ChannelClosed implements Delegate
        {
	        private void ChannelClosed(ChannelEventArgs args)
	        {
	            UdpChannel channel = (UdpChannel)args.getChannel();
	            channel.getChannelClosed().removeObservers(channelClosed);// -= ChannelClosed;
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
            IPEndPoint remoteEndPoint = getRemoteEndPoint();
            UdpChannel channel = new UdpChannel(this, remoteEndPoint, getHeartbeatFrequency(), getHeartbeatTimeout());
            channel.setEncoder( (IEncoder)_encoder.clone() );
            
            
            channel.getChannelClosed().addObservers(channelClosed);

            _channel = channel;

            if (getChannelAvailable() != null)
            {	
            	setEventArgs(new ChannelEventArgs(channel));
            	getChannelAvailable().RaiseEvent();
            }
                //setChannelAvailable(new ChannelEventArgs(channel));
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
            super.Stop();

            if (_channel != null)
            {
                _channel.getChannelClosed().removeObservers(channelClosed);
                _channel.Close();
                _channel = null;
            }
		}



        /// <summary>
        /// String representation of this provider.
        /// </summary>
        /// <returns></returns>
        public String toString()
        {
            return String.format("UDP Provider -> Local %1$2s %2$2s Remote %3$2s : %4$2s", _localAddress, _localPort, _remoteAddress, _remotePort);
        }

		@Override
		public void setChannelAvailable(ChannelEventArgs channelEventArgs) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void CleanupChannel() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Channel getChannel() {
			// TODO Auto-generated method stub
			return super.getChannel();
		}

	
    }
