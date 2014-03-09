package vitruvianJ.communication.session.sockets;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.serialization.Serialize;

public class IPEntry implements java.io.Serializable
{
	private String _name = "";
	private IPEndPoint _endPoint = new IPEndPoint("127.0.0.1",0);

	@Serialize//(getName = "get")
	public String getName()
	{
		return _name; 
	}
	
	@Serialize//(getName = "set")
	public void setName(String value) 
	{ _name = value; 
	}
	

	//[IPEndPointFormatter]
	@Serialize//(getName = "get")
    public IPEndPoint getEndPoint()
	{
		return _endPoint; 
	}

	@Serialize//(getName = "set")
	public void setEndPoint(IPEndPoint value) 
	{ 
		_endPoint = value; 
	}
}
