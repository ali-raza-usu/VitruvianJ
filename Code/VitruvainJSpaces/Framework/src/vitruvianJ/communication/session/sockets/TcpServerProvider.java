package vitruvianJ.communication.session.sockets;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import vitruvianJ.communication.channels.IChannel;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.IEncoder;


public class TcpServerProvider extends BaseTcpServerProvider implements ISocket
{
    private static JLogger _logger = new JLogger(TcpServerProvider.class);

    /// <summary>
    /// Tcp Server that listens for a connection.
    /// The pipes send/receive heartbeats to determine if they are still alive.
    /// </summary>
    public TcpServerProvider()
    {
    }

    public TcpServerProvider(String localEndPoint, IEncoder encoder)
    {
    	super(encoder);
        String[] localAddress = localEndPoint.split(":");
        setLocalEndPoint(new IPEndPoint(localAddress[0], Integer.parseInt(localAddress[1])) );
    }

    
    /// <summary>
    /// String representation of this provider.
    /// </summary>
    /// <returns></returns>
    public String toString()
    {
        return String.format("Tcp Server Provider -> %1$2s : %2$2s", getLocalAddress(), getLocalPort());
    }

	@Override
	protected IChannel CreateChannel(SocketChannel socket) {
		
		 try {
			return new TcpChannel(socket, getHeartbeatFrequency(), getHeartbeatTimeout(), _encoder);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected ServerSocketChannel CreateSocket() {
		ServerSocketChannel sChannel;
		try {
			sChannel = ServerSocketChannel.open();
			sChannel.configureBlocking(false);
			return sChannel;		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
	}

	@Override
	public void CleanupChannel() {				
	}

	@Override
	public ServerSocketChannel getChannel() {
		// TODO Auto-generated method stub
		
		return super.getChannel();
	}
}
