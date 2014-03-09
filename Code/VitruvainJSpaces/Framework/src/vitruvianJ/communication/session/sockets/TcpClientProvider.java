package vitruvianJ.communication.session.sockets;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import vitruvianJ.communication.channels.IChannel;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.IEncoder;

public class TcpClientProvider extends BaseTcpClientProvider
{
    private static JLogger _logger = new JLogger(TcpClientProvider.class);

    /// <summary>
    /// Tcp Client that provides a single socket connection.  All of the data is sent
    /// through a pipe that act as a channel.  The pipe sends/receives heartbeats to determine
    /// if it is still alive.
    /// </summary>
    public TcpClientProvider()
    {
    }

    public TcpClientProvider(String localEndPoint, String remoteEndPoint, IEncoder encoder)
    {
    	super(encoder);
        String[] localAddress = localEndPoint.split(":");
        setLocalEndPoint(new IPEndPoint(localAddress[0], Integer.parseInt(localAddress[1])));

        String[] remoteAddress = remoteEndPoint.split(":");
        setRemoteEndPoint(new IPEndPoint(remoteAddress[0], Integer.parseInt(remoteAddress[1])) );
    }



    /// <summary>
    /// String representation of this provider.
    /// </summary>
    /// <returns></returns>
    public String ToString()
    {
        return String.format("Tcp Client Provider -> Local %1s:%2s Remote %3s:%4s", getLocalAddress(), getLocalPort(), getRemoteAddress(), getRemotePort());
    }

	@Override
	protected IChannel CreateChannel(SocketChannel socketChannel) {
		
		try {
			
			TcpChannel _tcpChannel = new TcpChannel(socketChannel, getHeartbeatFrequency(), getHeartbeatTimeout(), _encoder);
			_logger.Debug("Channel Created : " + _tcpChannel.ToString());
			return _tcpChannel;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected SocketChannel CreateSocketChannel() {
		SocketChannel sChannel;
		try {
			sChannel = SocketChannel.open();
			//sChannel.configureBlocking(false);
			return sChannel;		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}
}
