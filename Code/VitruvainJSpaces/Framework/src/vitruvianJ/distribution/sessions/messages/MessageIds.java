package vitruvianJ.distribution.sessions.messages;

import vitruvianJ.communication.Heartbeat;

public class MessageIds
{
    public static final int HEARTBEAT = Heartbeat.MESSAGE_ID;

    public static final int REQUEST_BROKER_ID = 1;
    public static final int REPLY_BROKER_ID = 2;

    public static final int REQUEST_KNOWN_OBJECT_IDS = 3;
    public static final int REPLY_KNOWN_OBJECT_IDS = 4;

	public static final int REQUEST_SERVICES = 5;
	public static final int REPLY_SERVICES = 6;
	
    public static final int REQUEST_EXECUTE_METHOD = 7;
	public static final int REPLY_EXECUTE_METHOD = 8;

    public static final int REQUEST_EXECUTE_SYNC_PATTERN_METHOD = 9;
    public static final int REPLY_EXECUTE_SYNC_PATTERN_METHOD = 10;

    public static final int REQUEST_INIT_OBJECT = 11;
    public static final int REPLY_INIT_OBJECT = 12;

    public static final int REQUEST_ADD_SERVICE = 13;
    public static final int REQUEST_REMOVE_SERVICE = 14;
}
