package vitruvianJ.communication.session;

import java.util.Hashtable;

import vitruvianJ.eventargs.EventArgs;

/// <summary>
/// Event arguments that contain the Message associated with the event.
/// </summary>
public class MessageEventArgs implements EventArgs
{
	private Object _message = null;

	/// <summary>
	/// Construct the event args.
	/// </summary>
	/// <param name="message">The message associated with the event.</param>
	public MessageEventArgs(Object message)
	{
		_message = message;
	}

	/// <summary>
	/// The message associated with the event.
	/// </summary>
	public Object getMessage()
	{
		return _message;
	}

	@Override
	public void addProperty(String name, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Hashtable<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPropertyValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeProperty(String name) {
		// TODO Auto-generated method stub
		
	}
}