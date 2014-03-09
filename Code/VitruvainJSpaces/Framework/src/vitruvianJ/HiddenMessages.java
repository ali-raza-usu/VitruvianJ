package vitruvianJ;

import java.util.ArrayList;

import vitruvianJ.communication.Heartbeat;
import vitruvianJ.communication.session.protocols.PendingProtocol;
import vitruvianJ.distribution.sessions.messages.ReplyBrokerId;
import vitruvianJ.distribution.sessions.messages.ReplyKnownObjectIds;
import vitruvianJ.distribution.sessions.messages.RequestBrokerId;
import vitruvianJ.distribution.sessions.messages.RequestKnownObjectIds;

public class HiddenMessages {

	private static ArrayList<Class> _msgList = new ArrayList<Class>();
	public static ArrayList<Class> getMsgList() {
		
		_msgList.add(Heartbeat.class);
		_msgList.add(RequestBrokerId.class);
		_msgList.add(ReplyBrokerId.class);
		_msgList.add(RequestKnownObjectIds.class);
		_msgList.add(ReplyKnownObjectIds.class);
		
		return _msgList;
	}

	
	static boolean contains(Class tempClass)
	{
		for (Class element : _msgList) {
			if(element.equals(tempClass))
				return true;
		}
		return false;
	}
}
