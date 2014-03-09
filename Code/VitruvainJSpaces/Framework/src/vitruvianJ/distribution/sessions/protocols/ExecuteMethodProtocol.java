package vitruvianJ.distribution.sessions.protocols;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import vitruvianJ.communication.session.protocols.AsyncProtocol;
import vitruvianJ.communication.session.protocols.ProtocolSession;
import vitruvianJ.distribution.proxies.ProxyUtilities;
import vitruvianJ.distribution.sessions.ObjectSession;
import vitruvianJ.distribution.sessions.messages.*;
import vitruvianJ.events.Event;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;

public class ExecuteMethodProtocol extends AsyncProtocol
{
    private static JLogger _logger = new JLogger(ExecuteMethodProtocol.class);

	public ExecuteMethodProtocol(ProtocolSession session)
	{
		super(session, ObjectSession.TIMEOUT, 1, null);
	}

    public Object ExecuteFunction(Object value, Method mInfo, Object[] args, int timeout) throws Exception
    {
        return ExecuteFunction(value, mInfo, args, false, timeout);
    }

	public Object ExecuteFunction(Object value, Method mInfo, Object[] args, boolean updateArgs, int timeout) throws Exception
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Executing Remote Method %1s on Object %2s.", mInfo.getName(), value.toString());

        SetTimeout(timeout);

        Type type = ProxyUtilities.getNonProxyBaseType(value.getClass());
       // Profiler.Start(_logger, "Remote -> {0}::{1}", type.getClass().getName(), mInfo.getName());

        RequestExecuteMethod request = new RequestExecuteMethod(value, mInfo, updateArgs, args);

		BeginSend(request);
		EndSend();

       // Profiler.Stop(_logger);

		Thread.sleep(1000);
		if (getResponseMessage() != null)
		{
			ReplyExecuteMethod responseMessage = (ReplyExecuteMethod)getResponseMessage();

            // throw the remote exception
            if (responseMessage.getRetValue() instanceof Exception)
                throw (Exception)responseMessage.getRetValue();

            if (request.getUpdateArgs())
            {
                Object[] updatedArgs = responseMessage.getUpdatedArgs();
                if (args != null && updatedArgs != null)
                {
                    for (int i = 0; i < args.length; i++)
                        args[i] = updatedArgs[i];
                }
            }

			return responseMessage.getRetValue();
		}
		else
		{
			if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Failed to Execute Remote Method %1s on Object %2s. Message id %3s", mInfo.getName(), value.toString(), request.getMessageId());

            throw new Exception("Failed to Execute Remote Method %1s "+mInfo.getName()+" on Object %2s "+ value.toString());
		}
	}

    public Event getCallback()// ProtocolCallbackHandler getCallback()
    {
        	return _callback; 
    }
    public void setProtocolCallbackHandler(Event value)//ProtocolCallbackHandler value) 
    {
    	_callback = value;
    }

    public Object getReturnValue() throws Exception
    {

            if (getResponseMessage() != null)
            {
                ReplyExecuteMethod responseMessage = (ReplyExecuteMethod)getResponseMessage();
                return responseMessage.getRetValue();
            }
            else
            {
                if (_logger.IsDebugEnabled())
                    _logger.DebugFormat("The ReturnValue is invalid, because the remote method didn't respond.");

                throw new Exception("The ReturnValue is invalid, because the remote method didn't respond.");
            }
    }

    public void BeginBroadcastExecuteFunction(List<JGUID> excludeList, Object value, Method mInfo, Object[] args, int timeout)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Begin Broadcast Executing Remote Method {0} on Object {1}.", mInfo.getName(), value.toString());

        SetTimeout(timeout);

        RequestExecuteMethod request = new RequestExecuteMethod(value, mInfo, args);
        request.setBroadcast(true);
        request.setExcludeList(excludeList);
        super.BeginSend(request);
    }

    public void BeginExecuteFunction(Object value, Method mInfo, Object[] args, boolean updateArgs, int timeout)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Begin Executing Remote Method %1s on Object %2s.", mInfo.getName(), value.toString());

        SetTimeout(timeout);

        super.BeginSend(new RequestExecuteMethod(value, mInfo, updateArgs, args));
    }

    public void EndExecuteFunction() throws InterruptedException
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("End Executing Remote Method.");

        super.EndSend();
    }
}
