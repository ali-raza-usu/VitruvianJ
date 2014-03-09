package vitruvianJ.communication.channels;

//import vitruvianJ.delegate.Delegator;
import vitruvianJ.delegate.IDelegate;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.events.IEventSubject;


	/// <summary>
	/// Provides one or more communication channels
	/// </summary>
	public class IChannelProvider implements IEventSubject
	{
		/// <summary>
		/// Event fired when a new channel is opened
		/// </summary>
		
		private Event channelAvaiable = new Event(this);
		
				
		/// <summary>
		/// Start listening for channels to open.
		/// </summary>
		/// <returns>True if started successfully, otherwise False.</returns>
		
		public Event getChannelAvailable() {
			return channelAvaiable;
		}

		public void setChannelAvailable(Event value) {
			channelAvaiable = value;
		}
		
		public boolean Start(){
			return true;
		}

		/// <summary>
		/// Stop listening for channels to open.
		/// </summary>
		public void Stop(){}

		@Override
		public EventArgs getEventArgs() {
			// TODO Auto-generated method stub
			return null;
		}
	}

