package vitruvianJ.communication.session.sockets;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public  class SocketUtilities
{
    /// <summary>
    /// Find an available port.
    /// </summary>
    /// <returns></returns>
    static public int GetAvailablePort(String address) throws Exception
    {
        int port = 0;

        Socket socket = new Socket();//AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
        IPEndPoint endPoint = new IPEndPoint(address, port);
        socket.bind(new InetSocketAddress(endPoint.getIPAddress(), endPoint.getPort()) );

        port = socket.getLocalPort();

        socket.shutdownInput();//(SocketShutdown.Both);
        socket.shutdownOutput();
        socket.close();
        socket = null;
        System.gc();
        //GC.Collect();

        return port;
    }

    /// <summary>
    /// Determine if the given port is available.
    /// </summary>
    /// <returns></returns>
    static public boolean IsPortAvailable(String address, int port) throws Exception
    {
        try
        {
            Socket socket = new Socket();//AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);
            IPEndPoint endPoint = new IPEndPoint(address, port);
            socket.bind(new InetSocketAddress(endPoint.getIPAddress(), endPoint.getPort()) );
            //socket.bind(endPoint);
            socket.shutdownInput();//utdown(SocketShutdown.Both);
            socket.shutdownOutput();
            socket.close();
            socket = null;
            System.gc();
            //GC.Collect();

            return true;
        }
        catch (Exception ex)
        {
            // Only one usage of each socket address (protocol/network address/port) is normally permitted
            if (ex.hashCode() == 0x2740)
            {
                return false;
            }
            else
                throw ex;
        }
    }
}
