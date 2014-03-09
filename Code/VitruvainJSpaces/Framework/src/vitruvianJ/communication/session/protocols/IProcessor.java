package vitruvianJ.communication.session.protocols;

import vitruvianJ.communication.session.Message;

public interface IProcessor
{
	/// <summary>
	/// Flag indicating that the processor is initialized.
	/// This is necessary, so that a single processor can be used for
	/// multiple messages.
	/// </summary>
	boolean getInitialized();

	/// <summary>
	/// Initialize the processor.
	/// </summary>
	void Init(ProtocolSession session);

	/// <summary>
	/// Handle the message.
	/// </summary>
	/// <param name="message">The message to handle.</param>
	void ProcessMessage(Message message) throws Exception;

	/// <summary>
	/// Cleanup the processor.
	/// </summary>
	void Cleanup();
}