package vitruvianJ.communication.session.sockets;

import java.net.Socket;
import java.nio.channels.Channel;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;

import vitruvianJ.communication.Heartbeat;
import vitruvianJ.communication.session.MessageEventArgs;
import vitruvianJ.delegate.Delegator;
import vitruvianJ.events.Event;
import vitruvianJ.logging.JLogger;

public class SslChannel extends BaseChannel implements ISocket
{
    public SslChannel(int heartbeatFrequency, int heartbeatTimeout) {
		super(heartbeatFrequency, heartbeatTimeout, _encoder);
		// TODO Auto-generated constructor stub
	}

	
	static public X509Certificate FindCertificate(String storeName, String name)
    {
        X509Certificate result = null;
        String certName = String.format("CN={0}", name);
/*
        CertStore store = new CertStore(storeName, StoreLocation.LocalMachine);
        store.Open(OpenFlags.ReadOnly);
        
        foreach (X509Certificate certificate : store.Certificates)
        {
            if (certificate.getSubject == certName)
            {
                result = certificate;
                break;
            }
        }
        store.Close();

        return result;
*/
        return null; //should be removed.
    }
/*
    private enum SSLEnd
    {
        Server,
        Client,
    }

    private enum RxState
    {
        Sync,
        Length,
        Message,
    }

    private JLogger _logger = new JLogger(SslChannel.class);

    private Socket _socket = null;

    private SSLEnd _sslEnd = SSLEnd.Server;
    private SslStream _sslStream = null;
    private X509Certificate _certificate = null;
    private String _targetHost = "";

    private byte[] _buffer = new byte[0];

    private RxState _rxState = RxState.Length;
    private int _msgLength = 0;

    /// <summary>
    /// Construct the channel.
    /// </summary>
    /// <param name="socket"></param>
    /// <param name="provider"></param>
    /// <param name="heartbeatFrequency"></param>
    /// <param name="heartbeatTimeout"></param>
    public SslChannel(X509Certificate certificate, Socket socket, int heartbeatFrequency, int heartbeatTimeout)        
    {
    	super(heartbeatFrequency, heartbeatTimeout);
        _sslEnd = SSLEnd.Server;

        _certificate = certificate;
        _socket = socket;

        _sslStream = new SslStream(new NetworkStream(_socket, false), false, ValidateRemoteCertificate, SelectLocalCertificate);
        _sslStream.ReadTimeout = Session.TIMEOUT;
        _sslStream.WriteTimeout = Session.TIMEOUT;
    }

    /// <summary>
    /// Construct the channel.
    /// </summary>
    /// <param name="targetHost"></param>
    /// <param name="certificate"></param>
    /// <param name="socket"></param>
    /// <param name="provider"></param>
    /// <param name="heartbeatFrequency"></param>
    /// <param name="heartbeatTimeout"></param>
    public SslChannel(String targetHost, X509Certificate certificate, Socket socket, int heartbeatFrequency, int heartbeatTimeout)        
    {
    	super(heartbeatFrequency, heartbeatTimeout);
        _sslEnd = SSLEnd.Client;

        _targetHost = targetHost;
        _certificate = certificate;
        _socket = socket;

        _sslStream = new SslStream(new NetworkStream(_socket, false), false, ValidateRemoteCertificate, SelectLocalCertificate);
        _sslStream.ReadTimeout = Session.TIMEOUT;
        _sslStream.WriteTimeout = Session.TIMEOUT;
    }

    private boolean ValidateRemoteCertificate(Object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
    {
        if (sslPolicyErrors == SslPolicyErrors.None)
            return true;

        if (_logger.IsErrorEnabled())
            _logger.ErrorFormat("Certificate error: {0}", sslPolicyErrors);

        // Do not allow this app to communicate with unauthenticated apps.
        return false;
    }

    private X509Certificate SelectLocalCertificate(Object sender, String targetHost, X509CertificateCollection localCertificates, X509Certificate remoteCertificate, String[] acceptableIssuers)
    {
        foreach (X509Certificate certificate : localCertificates)
        {
            if (certificate.Subject == _certificate.Subject)
                return certificate;
        }

        return null;
    }

  

    public Socket getSocket()
    {
        return _socket; 
    }
    
    public void Read()
    {
        int available = _socket.Available;

        while (available > 0)
        {
            int numReceived = 0;

            try
            {
                switch (_rxState)
                {
                    case RxState.Length:
                        {
                            if (available < 4)
                                break;

                            if (_buffer.length < 4)
                            {
                                if (_logger.IsDebugEnabled())
                                    _logger.DebugFormat("Resizing receive buffer from {0} bytes to {1} bytes.", _buffer.length, 4);

                                _buffer = new byte[4];
                            }

                            synchronized (_sslStream) // thread-safe
                            {
                                _sslStream.Read(_buffer, 0, 4);
                            }

                            _msgLength = BitConverter.ToInt32(_buffer, 0);

                            if (_msgLength > _socket.getReceiveBufferSize())
                                _socket.setReceiveBufferSize(_msgLength);

                            numReceived = 4;
                            _rxState = RxState.Message;
                            break;
                        }
                    case RxState.Message:
                        {

                            if (available < _msgLength)
                                break;

                            if (_buffer.length < _msgLength)
                            {
                                if (_logger.IsDebugEnabled())
                                    _logger.DebugFormat("Resizing receive buffer from {0} bytes to {1} bytes.", _buffer.length, _msgLength);

                                _buffer = new byte[_msgLength];
                            }

                            lock (_sslStream) // thread-safe
                            {
                                _sslStream.Read(_buffer, 0, _msgLength);
                            }

                            Object message = _encoder.ToObject(_buffer);//, 0, _msgLength);

                            numReceived = _msgLength;
                            _rxState = RxState.Length;

                            if (_logger.IsDebugEnabled())
                                _logger.DebugFormat("Received message : {0}", toString());

                            if (message.getClass().equals(Heartbeat.class))
                            {
                                _monitor.HeartbeatReceived();
                            }
                            else
                            {
                                if (_messageReceived != null)
                                    setMessageReceived(new MessageEventArgs(message));
                            }
                            break;
                        }
                }
            }
            catch (Exception ex)
            {
                if (_logger.IsErrorEnabled())
                    _logger.ErrorFormat(ex, "Error while reading from {0}");

                ThreadSafeClose();
                return;
            }

            if (numReceived <= 0)
                break;

            available = _socket.Available;
        }
    }

    public void Error()
    {
    }

    public void CleanupSocket()
    {
        _socket.shutdownInput();
        _socket.shutdownOutput();
        _socket.close();
        _socket = null;
        System.gc();
    }

    
    public void Open()
    {
        synchronized (_syncObject)
        {
            if (!IsOpen())
            {
                super.Open();
                SocketSelector.Add(this);
            }
        }
    }

    public boolean Authenticate()
    {
        try
        {
            switch (_sslEnd)
            {
                case SSLEnd.Server:
                    {
                        lock (_sslStream) // thread-safe
                        {
                            _sslStream.AuthenticateAsServer(_certificate, true, SslProtocols.Tls, true);
                        }
                        break;
                    }
                case SSLEnd.Client:
                    {
                        X509CertificateCollection certificates = new X509CertificateCollection();
                        certificates.Add(_certificate);
                        lock (_sslStream) // thread-safe
                        {
                            _sslStream.AuthenticateAsClient(_targetHost, certificates, SslProtocols.Tls, true);
                        }
                        break;
                    }
            }

            return true;
        }
        catch (Exception ex)
        {
            _sslStream.Close();
            _sslStream.Dispose();
            _sslStream = null;

            return false;
        }
    }

    public void Close()
    {
        synchronized (_syncObject)
        {
            if (IsOpen())
            {
                super.Close();
                SocketSelector.Remove(this);
            }

            if (_sslStream != null)
            {
                _sslStream.Close();
                _sslStream.Dispose();
                _sslStream = null;
            }
        }
    }

    public void Send(Object message)
    {
        try
        {
            int bytesSent = 0;

            byte[] bytes = _encoder.ToBytes(message);
            int msgLength = bytes.length;

            if (_socket.getSendBufferSize() > msgLength)
                _socket.setSendBufferSize(msgLength);

            byte[] lenBytes = BitConverter.GetBytes(msgLength);

            byte[] allBytes = new byte[msgLength + 4];
            lenBytes.CopyTo(allBytes, 0);
            bytes.CopyTo(allBytes, 4);

            synchronized (_sslStream) // thread-safe
            {
                _sslStream.Write(allBytes, 0, allBytes.Length);
            }

            bytesSent += (4 + msgLength);

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Sent message : {0}", toString());
        }
        catch (Exception ex)
        {
            if (_logger.IsErrorEnabled())
                _logger.ErrorFormat(ex, "Error sending message : {0}");

            ThreadSafeClose();
        }
    }

    

    public String toString()
    {
        if (_socket != null)
        {
            String local = String.format("{0}:{1}", _socket.getLocalAddress().getHostAddress(), _socket.getLocalPort());
            String remote = String.format("{0}:{1}", _socket.getRemoteSocketAddress()., _socket.getRemoteSocketAddress().Port);
            return String.format("Local -> {0} | Remote -> {1}", local, remote);
        }
        else
            return String.format("Local -> Unknown | Remote -> Unknown");
    }

	@Override
	public Delegator getChannelMessageReceived() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMessageReceived(Delegator delegator) {
		// TODO Auto-generated method stub
		
	}
*/

	@Override
	public void Send(Object message) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void Error() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Read() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void Close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Event getChannelMessageReceived() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMessageReceived(Event value) {
		// TODO Auto-generated method stub
		
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
