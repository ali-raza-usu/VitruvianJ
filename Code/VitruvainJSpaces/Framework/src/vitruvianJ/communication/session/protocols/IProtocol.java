package vitruvianJ.communication.session.protocols;

import vitruvianJ.communication.session.Message;
import vitruvianJ.delegate.Delegator;


	public interface IProtocol
	{
		public Delegator setProtocolCallbackHandler(IProtocol protocol);
		/// <summary>
		/// Time in (ms) until the next timeout of the protocol.
		/// </summary>
		/// <returns>The time in (ms) until the next timeout.</returns>
		int getTimeOut();
		
		/// <summary>
		/// Flag indicating that the protocol is finished, and should
		/// not be used for further message processing, or timeouts.
		/// </summary>
		boolean IsFinished();		

		/// <summary>
		/// Indicate to the protocol that a timeout has happened.
		/// </summary>
		void TimeExpired();

        /// <summary>
        /// Determine if the message is for the protocol.
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        boolean IsProtocolMessage(Message message);

		/// <summary>
		/// Give a message to the protocol.
		/// </summary>
		void ProcessMessage();
	}	

