package vitruvianJ.communication.channels;


import java.util.Hashtable;

import vitruvianJ.eventargs.EventArgs;

/// <summary>
/// Event arguments that contain the Channel associated with the event.
/// </summary>
public class ChannelEventArgs implements EventArgs
{
	private IChannel _channel = null;

	/// <summary>
	/// Construct the event args.
	/// </summary>
	/// <param name="channel">The Channel associated with the event.</param>
	public ChannelEventArgs(IChannel channel)
	{
		_channel = channel;
	}

	/// <summary>
	/// The Channel associated with the event.
	/// </summary>
	public IChannel getChannel()
	{
		return _channel;
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