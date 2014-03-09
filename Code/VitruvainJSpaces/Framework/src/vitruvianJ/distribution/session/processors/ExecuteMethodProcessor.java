package vitruvianJ.distribution.session.processors;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.protocols.IProcessor;
import vitruvianJ.communication.session.protocols.ProtocolSession;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.proxies.ReflectionInfo;
import vitruvianJ.distribution.sessions.messages.ReplyExecuteMethod;
import vitruvianJ.distribution.sessions.messages.RequestExecuteMethod;
import vitruvianJ.logging.JLogger;

public class ExecuteMethodProcessor implements IProcessor
{
    static private JLogger _logger = new JLogger(ExecuteMethodProcessor.class);

	private ProtocolSession _session = null;

    public boolean getInitialized()
	{
		return _session != null;
	}

	public void Init(ProtocolSession session)
	{
		_session = session;
	}

    public void ProcessMessage(Message message) throws Exception
	{
		if (!(message instanceof RequestExecuteMethod))
			throw new Exception("Incorrect message for processor.");

		RequestExecuteMethod emMessage = (RequestExecuteMethod)message;
        Method mInfo = (Method)ReflectionInfo.getReflectionInfo(emMessage.getValue().getClass()).GetMember(emMessage.getMethodIndex());

        //Profiler.Start("Local -> {0}::{1}", value.GetType().Name, mInfo.Name);

        Object retValue = null;

        try
        {
            retValue = mInfo.invoke(emMessage.getValue(), emMessage.getArgs());
        }
        catch (Exception ex)
        {
            // assign the exception as the return value
            // the remote side will throw the exception.
            retValue = ex.getLocalizedMessage();//InnerException;
        }

        //Profiler.Stop();

        if (!emMessage.getAsynchronous())
        {
            if (emMessage.getUpdateArgs())
                _session.Send(new ReplyExecuteMethod(emMessage.getMessageId(), emMessage.getArgs(), retValue));
            else
                _session.Send(new ReplyExecuteMethod(emMessage.getMessageId(), retValue));
        }

        if (emMessage.getBroadcast() && emMessage.getValue() instanceof ISyncProxy)
        {
            ObjectBroker.BroadcastExecuteRemoteMethod(emMessage.getExcludeList(), (ISyncProxy)emMessage.getValue(), mInfo, emMessage.getArgs());
        }
	}

	/// <summary>
	/// Get the types of the given args.
	/// </summary>
	/// <param name="args"></param>
	/// <returns></returns>
	private static Type[] GetTypes(Object... args)
	{
		if (args == null)
			return null;//Type.EmptyTypes;

		Type[] types = new Type[args.length];
		for (int i = 0; i < args.length; i++)
			types[i] = args[i].getClass();
		return types;
	}

	public void Cleanup()
	{
		_session = null;
	}

	
}