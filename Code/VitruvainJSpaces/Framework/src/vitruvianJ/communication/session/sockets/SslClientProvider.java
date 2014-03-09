package vitruvianJ.communication.session.sockets;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.cert.X509Certificate;

import vitruvianJ.communication.channels.IChannel;
import vitruvianJ.delegate.IDelegate;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.Serialize;

/// <summary>
/// Connects to a SSL tcp server, and maintains the connection through
/// a heartbeat with the server.
/// </summary>
public class SslClientProvider extends BaseTcpClientProvider
{
    private static JLogger _logger = new JLogger(SslClientProvider.class);

    private String _targetHost = "";
    private String _certName = "";
    private X509Certificate _certificate = null;

    /// <summary>
    /// SSL Tcp Client that provides a single socket connection.  All of the data is sent
    /// through a pipe that act as a channel.  The pipe sends/receives heartbeats to determine
    /// if it is still alive.
    /// </summary>
    public SslClientProvider()
    { }

    @Serialize//(getName = "get")
    public String getCertificate()
    {
        return _certName; 
    }
    @Serialize//(getName = "get")
    public void setClientProvider(String value) 
    { 
    	_certName = value; 
    }

    protected SocketChannel CreateSocketChannel()
    {
        return null;//new Socket();//AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
    }
/*
    protected IChannel CreateChannel(Socket socket)
    {
        SslChannel channel = new SslChannel(_certName, _certificate, socket, getHeartbeatFrequency(), getHeartbeatTimeout());
        if (channel.Authenticate())
            return channel;
        else
            return null;
    }
*/
    

    public boolean Start()
    {
        _certificate = SslChannel.FindCertificate("My", _certName);
        super.Start();
        return true;
    }

    
    
    /// <summary>
    /// String representation of this provider.
    /// </summary>
    /// <returns></returns>
    public String toString()
    {
        return String.format("SSL Client Provider -> Local {0}:{1} Remote {2}:{3}", getLocalAddress(), getLocalPort(), getRemoteAddress(), getRemotePort());
    }
	
	
	

	

	@Override
	protected IChannel CreateChannel(SocketChannel socketChannel) {
		// TODO Auto-generated method stub
		return null;
	}
}
