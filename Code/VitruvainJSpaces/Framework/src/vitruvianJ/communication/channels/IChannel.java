package vitruvianJ.communication.channels;

import vitruvianJ.communication.session.MessageEventArgs;

import vitruvianJ.serialization.IEncoder;

import vitruvianJ.events.Event;

import java.lang.Void;

public interface IChannel
{
	/// <summary>
	/// Delegate invoked when a message is received
	/// </summary>
	//public static  Class[] MSG_OUTPUT_ARGS = {MessageEventArgs.class};
	//public  Delegator MessageReceived = new Delegator(MSG_OUTPUT_ARGS, Void.TYPE);
	//public IDelegate MessageEvent = null;
	public Event getChannelMessageReceived();// { get; set; }
	public void setMessageReceived(Event delegator);
	/*
	Delegator getMessageReceived();
	{ get; set; }
	 */
	/// <summary>
	/// Delegate invoked when a channel is closed
	/// </summary>
	//public static  Class[] CHNL_OUTPUT_ARGS = {ChannelEventArgs.class};
	//public static Delegator ChannelClosed = new Delegator(CHNL_OUTPUT_ARGS, Void.TYPE);
	//public IDelegate ChannelEvent = null;	
	
	public Event getChannelClosed();// { get; set; }
	public void setChannelClosed(Event value);

	/// <summary>
	/// The encoder/decoder to use when changing objects to bytes
	/// </summary>
	public IEncoder getEncoder();// = null;
	public void setEncoder(IEncoder encoder);

	/// <summary>
	/// Open the channel
	/// </summary>
	void Open();

	// Close the channel
	void Close();

	/// <summary>
	/// Send a message out of the channel
	/// </summary>
	/// <param name="message">The message to send</param>
	void Send(Object message);
}
