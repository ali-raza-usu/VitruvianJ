package vitruvianJ.distribution.sessions.protocols;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.session.protocols.AsyncProtocol;
import vitruvianJ.communication.session.protocols.ProtocolSession;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.proxies.ProxyUtilities;
import vitruvianJ.distribution.sessions.ObjectSession;
import vitruvianJ.distribution.sessions.messages.ReplyExecuteSyncPatternMethod;
import vitruvianJ.distribution.sessions.messages.RequestExecuteSyncPatternMethod;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;

public class ExecuteSyncPatternMethodProtocol extends AsyncProtocol
{
    private static JLogger _logger = new JLogger(ExecuteSyncPatternMethodProtocol.class);

    public ExecuteSyncPatternMethodProtocol(ProtocolSession session)
    {	
    	super(session, ObjectSession.TIMEOUT, 1, null);
	}

    public Object ExecuteFunction(ISyncProxy proxy, Member proxyMember, String methodName, Object[] methodArgs, int timeout) throws Exception
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Executing Remote Sync Pattern Method %1s on object %2s.", methodName, proxy.toString());

        SetTimeout(timeout);
//        if((proxy.getClass()).toString().contains("Basics.SharedServices_$$_javassist_4"))
//    		_logger.Debug("XmlObjectSerializer : Basics.SharedServices_$$_javassist_4 ");
        Type type = ProxyUtilities.getNonProxyBaseType(proxy.getClass());
       // Profiler.Start(_logger, "Remote -> {0}::{1}::{2}", type.Name, proxyMember.getName(), methodName);

        RequestExecuteSyncPatternMethod request = new RequestExecuteSyncPatternMethod(proxy, proxyMember, methodName, methodArgs );
		BeginSend(request);
		EndSend();

      //  Profiler.Stop(_logger);

		if (getResponseMessage() != null)
		{
            ReplyExecuteSyncPatternMethod responseMessage = (ReplyExecuteSyncPatternMethod)getResponseMessage();
			return responseMessage.getRetValue();
		}
		else
		{
			if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Failed to Execute Remote Sync Pattern Method {0} on object {1}. Message id {2}", methodName, proxy.toString(), request.getMessageId());

            throw new Exception("Failed to Execute Remote Sync Pattern Method." + methodName);
		}
	}

    public void BeginBroadcastExecuteFunction(List<JGUID> excludeList, ISyncProxy proxy, Member proxyMember, String methodName, Object[] methodArgs, boolean updateArgs, int timeout)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Begin Broadcast Executing Remote Method {0}.", methodName);

        SetTimeout(timeout);

        RequestExecuteSyncPatternMethod request = new RequestExecuteSyncPatternMethod(proxy, proxyMember, methodName, methodArgs);
        request.setBroadcast(true);
        request.setExcludeList(excludeList);
        
        super.BeginSend(request);
    }

    public void BeginExecuteFunction(ISyncProxy proxy, Member proxyMember, String methodName, Object[] args, int timeout)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Executing Remote Sync Pattern Method {0}.", methodName);

        SetTimeout(timeout);

        RequestExecuteSyncPatternMethod request = new RequestExecuteSyncPatternMethod(proxy, proxyMember, methodName, args);
        super.BeginSend(request);
    }

    public Object EndExecuteFunction() throws Exception
    {
        EndSend();

		if (getResponseMessage() != null)
		{
            ReplyExecuteSyncPatternMethod responseMessage = (ReplyExecuteSyncPatternMethod)getResponseMessage();
			return responseMessage.getRetValue();
		}
		else
		{
			if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Failed to Execute Remote Sync Pattern Method");

            throw new Exception("Failed to Execute Remote Sync Pattern Method.");
		}
    }
}
