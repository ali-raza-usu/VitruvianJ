package vitruvianJ.communication.session.sockets;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.cert.X509Certificate;

import vitruvianJ.communication.channels.IChannel;
import vitruvianJ.delegate.IDelegate;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.Serialize;

public class SslServerProvider extends BaseTcpServerProvider implements ISocket
{
    private static JLogger _logger = new JLogger(SslServerProvider.class);

    private String _certName = "";
    private X509Certificate _certificate = null;

    /// <summary>
    /// SSL Tcp Server that listens for a connection.
    /// The pipes send/receive heartbeats to determine if they are still alive.
    /// </summary>
    public SslServerProvider()
    {
    }

    @Serialize//(getName = "get")
    public String getCertificate()
    {
        return _certName; 
    }
     
    
    @Serialize//(getName = "set")
    public void setCertificate(String value)
    {
    	_certName = value; 
    }
    

    protected ServerSocketChannel CreateSocket()
    {
        try {
			return ServerSocketChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
		return null;
    }

    

    public boolean Start()
    {
        _certificate = SslChannel.FindCertificate("Root", _certName);
        super.Start();
        return true;
    }


/*
    protected IChannel CreateChannel(Socket socket)
    {
        SslChannel channel = new SslChannel(_certificate, socket, getHeartbeatFrequency(), getHeartbeatTimeout());
        if (channel.Authenticate())
            return channel;
        else
            return null;
    }
*/
    /// <summary>
    /// String representation of this provider.
    /// </summary>
    /// <returns></returns>
    public String toString()
    {
        return String.format("SSL Server Provider -> {0}:{1}", getLocalAddress(), getLocalPort());
    }

	

	
	
	@Override
	public void CleanupChannel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServerSocketChannel getChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IChannel CreateChannel(SocketChannel socket) {
		// TODO Auto-generated method stub
		return null;
	}
}
