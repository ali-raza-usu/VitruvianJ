package vitruvianJ.communication.session;

import vitruvianJ.communication.session.Session;
import vitruvianJ.eventargs.EventArgs;
	/// <summary>
	/// A session object that encodes and decodes messages
	/// and encapsulates simple processing.
	/// </summary>
	public class BasicSession extends Session
	{
		/// <summary>
		/// Send the message.
		/// </summary>
		/// <param name="message">The message to encode and send.</param>
		public void Send(Object message)
		{
			BeforeSend();
			_channel.Send(message);
		}

		/// <summary>
		/// Decode the message and process it.
		/// </summary>
		/// <param name="message">The message to decode.</param>
		protected void Process(Object message)
		{
		}

		@Override
		public EventArgs getEventArgs() {
			// TODO Auto-generated method stub
			return null;
		}
	}
