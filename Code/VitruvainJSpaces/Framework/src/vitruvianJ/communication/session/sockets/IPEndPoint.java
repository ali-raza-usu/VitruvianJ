package vitruvianJ.communication.session.sockets;

import vitruvianJ.serialization.Serialize;

public class IPEndPoint implements java.io.Serializable{
	
	private int port;
	private String ipAddress;
	
	
	public IPEndPoint(String ipaddress, int port)
	{
		this.port = port;
		this.ipAddress  = ipaddress;
	}
	
	@Serialize//(getName = "get")
	public int getPort()
	{
		return port;
	}
	
	@Serialize//(getName = "set")
	public void setPort(Integer value)
	{
		port = value;
	}
	@Serialize//(getName = "get")
	public String getIPAddress()
	{
		return ipAddress;
	}
	@Serialize//(getName = "set")
	public void setIPAddress(String value)
	{
		ipAddress = value;		
	}

}
