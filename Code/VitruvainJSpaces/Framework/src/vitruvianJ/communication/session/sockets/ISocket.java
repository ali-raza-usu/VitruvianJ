package vitruvianJ.communication.session.sockets;

import java.net.*;
import java.nio.channels.Channel;

	public interface ISocket
	{
		/// <summary>
		/// The underlying socket.
		/// </summary>
		Channel getChannel();

		/// <summary>
		/// Data is available on the socket.
		/// </summary>
		void Read();

		/// <summary>
		/// An error occured on the socket.
		/// </summary>
		void Error();

        /// <summary>
        /// Cleanup the underlying socket.
        /// </summary>
        void CleanupChannel();
	}

