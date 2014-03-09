package vitruvianJ.distribution.sessions.messages;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.OptimisticSerialization;
import vitruvianJ.serialization.Serialize;
@OptimisticSerialization
public class RequestExecuteSyncPatternMethod extends RequestMessage
{
	JLogger _logger = new JLogger(RequestExecuteSyncPatternMethod.class);
    private JGUID _proxyId = new JGUID();
    private String _proxyMemberName = "";
    private int _proxyMemberIndex = -1;

    private String _syncPatternMethodName = "";
	private Object[] _syncPatternMethodArgs = null;
    private boolean _asynchronous = false;

    private boolean _broadcast = false;
    private List<JGUID> _excludeList = new ArrayList<JGUID>();

	public RequestExecuteSyncPatternMethod()
	{
		super(MessageIds.REQUEST_EXECUTE_SYNC_PATTERN_METHOD);
	}

    public RequestExecuteSyncPatternMethod(ISyncProxy proxy, Member proxyMember, String syncPatternMethodName, Object[] syncPatternMethodArgs)
	{
    	 super(MessageIds.REQUEST_EXECUTE_SYNC_PATTERN_METHOD);
        _proxyId = proxy.getProxyId();
        _proxyMemberName = proxyMember.getName();
        _proxyMemberIndex = ReflectionInfo.getReflectionInfo(proxy.getClass()).GetMemberIndex(proxyMember);

        _syncPatternMethodName = syncPatternMethodName;
        _syncPatternMethodArgs = syncPatternMethodArgs;
	}
    @Serialize
    public boolean getAsynchronous()
    {
        return _asynchronous; 
     }
    @Serialize
    public void  setAsynchronous(boolean value) 
    { 
    	_asynchronous = value; 
    }
    @Serialize
    public boolean getBroadcast()
    {
        return _broadcast; 
    }
    
    @Serialize
    public void setBroadcast(boolean value) 
    { 
    	_broadcast = value; 
    }
    
    @Serialize
    public List<JGUID> getExcludeList()
    {
        return _excludeList; 
    }
    
    @Serialize
    public  void setExcludeList(List<JGUID> value) 
    { 
    	_excludeList = value; 
    }
    
    @Serialize
	public JGUID getProxyId()
	{
		return _proxyId; 
	}
    
    @Serialize
    public void setProxyId(JGUID value) 
    { 
    	_proxyId = value;
	}
    
    @Serialize
	public String getProxyMemberName()
	{
		return _proxyMemberName; 
	}
    
    @Serialize
    public  void  setProxyMemberName(String value) 
    { 
    	_proxyMemberName = value; 
	}

    @Serialize
    public int getProxyMemberIndex()
    {
        return _proxyMemberIndex; 
    }
    
    @Serialize
    public void setProxyMemberIndex(int value) 
    { 
    	_proxyMemberIndex = value; 
    }

    @Serialize
    public String getSyncPatternMethodName()
    {
        return _syncPatternMethodName; 
    }
    
    @Serialize
    public  void setSyncPatternMethodName(String value) 
    {
    	_syncPatternMethodName = value;
    }

    @Serialize
	public Object[] getSyncPatternMethodArgs()
	{
        return _syncPatternMethodArgs; 
    }
    
    @Serialize
    public void setSyncPatternMethodArgs(Object[] value) 
    { 
    	_syncPatternMethodArgs = value;
	}

    public String toString()
    {
        ISyncProxy proxy = ObjectBroker.GetProxy(_proxyId);
        String logMsg = "";
        if (proxy != null)
        {
            Type baseType = ProxyUtilities.getNonProxyBaseType(proxy.getClass());
            logMsg = String.format("Request Execute SyncPattern Method : MessageId = %1s : Member = %2s.%3s : SyncPatternMethod = %3s ", getMessageId(), baseType.getClass().getName(), _proxyMemberName, _syncPatternMethodName);
          //  _logger.Debug(logMsg);
            return logMsg;
        }
        else
        {
        	logMsg = String.format("Request Execute SyncPattern Method : MessageId = %1s  : Error : Unknown Object , Proxy Id : %2s ", getMessageId(), _proxyId);
        	//_logger.Debug(logMsg);
            return logMsg;
        }
    }
}
