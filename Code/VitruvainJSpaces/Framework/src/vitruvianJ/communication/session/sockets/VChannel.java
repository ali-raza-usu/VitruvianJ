package vitruvianJ.communication.session.sockets;
import java.net.*;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;

public class VChannel {

	private DatagramChannel udpChannel;
	private Channel channel;
	
	public VChannel(DatagramChannel udpChannel, Channel channel)
	{
		this.udpChannel = udpChannel;
		this.channel = channel;
	}
	
	public VChannel(DatagramChannel udpChannel)
	{
		this.udpChannel = udpChannel;	
	}
	
	
	public VChannel()
	{
	
	}
	
	public VChannel fromUdp(DatagramChannel value)
	{
		this.udpChannel = value;
		return this;
	}

	public VChannel fromTcp(Channel value)
	{
		this.channel = value;
		return this;
	}
	
	public void setUdpSocket(DatagramChannel value)
	{
		udpChannel = value;
	}
	
	public DatagramChannel getUdpSocket()
	{
		return udpChannel;
	}
	
	public void setTcpSocket(Channel value)	{
		channel = value;
	}
	
	public Channel getTcpSocket()
	{
		return channel;
	}	
}
