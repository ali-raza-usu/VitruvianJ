package vitruvianJ.communication.session;

import java.util.Hashtable;

import vitruvianJ.communication.session.Session;
import vitruvianJ.eventargs.EventArgs;





public class SessionEventArgs implements EventArgs
{
	
	private Session _session = null;

	/// <summary>
	/// Construct the event args.
	/// </summary>
	/// <param name="session">The Session associated with the event.</param>
	public SessionEventArgs(Session session)
	{
		_session = session;
	}

	/// <summary>
	/// The Session associated with the event.
	/// </summary>
	public Session getSession()
	{
		return _session; 
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
	public void removeProperty(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getPropertyValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}
}
