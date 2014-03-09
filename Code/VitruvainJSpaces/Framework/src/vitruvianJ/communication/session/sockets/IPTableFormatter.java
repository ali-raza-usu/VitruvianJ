package vitruvianJ.communication.session.sockets;

import vitruvianJ.serialization.formatters.Formatter;

public class IPTableFormatter extends Formatter
{


	@Override
	public String Format(Object value) {
		// TODO Auto-generated method stub
		return IPTable.GetName((IPEndPoint) value);
	}

	@Override
	public Object Unformat(String value) {
		// TODO Auto-generated method stub
		return IPTable.GetEndPoint(value);
	}
}
