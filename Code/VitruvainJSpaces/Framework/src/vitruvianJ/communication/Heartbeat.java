package vitruvianJ.communication;
import vitruvianJ.communication.session.Message;
import vitruvianJ.serialization.DontSerialize;
import vitruvianJ.serialization.OptimisticSerialization;


    @OptimisticSerialization
    public class Heartbeat extends Message
	{
        //Todo : Serialization shouldn't serialize/deserialize constant values.
        @DontSerialize
        public static final int MESSAGE_ID = 0;

		public Heartbeat()		
		{
			super(MESSAGE_ID);
		}
		
		public String toString()
	     {
	         return String.format("Heartbeat : MessageId = %1s", getMessageId());
	     }
	}

