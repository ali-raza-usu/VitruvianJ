package vitruvianJ.distribution.session.processors;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import vitruvianJ.communication.session.Message;
import vitruvianJ.communication.session.protocols.IProcessor;
import vitruvianJ.communication.session.protocols.ProtocolSession;
import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.proxies.ReflectionInfo;
import vitruvianJ.distribution.sessions.messages.ReplyExecuteSyncPatternMethod;
import vitruvianJ.distribution.sessions.messages.RequestExecuteSyncPatternMethod;
import vitruvianJ.logging.JLogger;

public class ExecuteSyncPatternMethodProcessor implements IProcessor
{
    static private JLogger _logger = new JLogger(ExecuteSyncPatternMethodProcessor.class);

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
		if (!(message instanceof RequestExecuteSyncPatternMethod))
			throw new Exception("Incorrect message for processor.");

        RequestExecuteSyncPatternMethod emMessage = (RequestExecuteSyncPatternMethod)message;
        
        ISyncProxy proxy = ObjectBroker.GetProxy(emMessage.getProxyId());

        if (proxy != null)
        {
            Member proxyMember = ReflectionInfo.getReflectionInfo(proxy.getClass()).GetMember(emMessage.getProxyMemberIndex());
            ISyncPattern syncPattern = proxy.getSyncPattern(proxyMember);

            Message msg;
            if(emMessage.getSyncPatternMethodName().contains("SyncItem"))
	         {    	        	
	        	// obj = _deserializer.Deserialize(doc, null);    
	        	 msg = null;    	        	
	         }
            //Profiler.Start(_logger, "Local -> {0}::{1}", syncPattern.GetType().Name, emMessage.SyncPatternMethodName);

            // find the method, by convention only 1 method should exist by this name
            Method[] methods = syncPattern.getClass().getMethods();
            Object retValue = null;
            for (Method method : methods) {
            	if(method.getName().equals(emMessage.getSyncPatternMethodName()))
            	{
            		//Method mInfo = syncPattern.getClass().getMethod(,emMessage.getSyncPatternMethodArgs().getClass());//, BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static);
            		retValue = method.invoke(syncPattern, emMessage.getSyncPatternMethodArgs());
            	}
            }
            //Profiler.Stop(_logger);

            if (!emMessage.getAsynchronous())
                _session.Send(new ReplyExecuteSyncPatternMethod(emMessage.getMessageId(), retValue));

            if (emMessage.getBroadcast())
            {
                ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(emMessage.getExcludeList(), proxy, proxyMember, emMessage.getSyncPatternMethodName(), emMessage.getSyncPatternMethodArgs());
            }
        }
        else
        {
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Unknown proxy : %1s", emMessage.getProxyId());
        }
	}

	public void Cleanup()
	{
		_session = null;
	}

	
}
