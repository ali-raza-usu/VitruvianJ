package vitruvianJ.communication.session.sockets;

import vitruvianJ.serialization.formatters.*;

public class IPEndPointFormatter extends Formatter
{
	@Override
	public String Format(Object value) {
		IPEndPoint endPoint = (IPEndPoint) value;
		return String.format("{0}:{1}", endPoint.getIPAddress(), endPoint.getPort());
	}
	
	@Override
	public Object Unformat(String value) {
		String[] splitArr = value.split(":");
		return new IPEndPoint(splitArr[0], Integer.parseInt(splitArr[1]));
	}
}
