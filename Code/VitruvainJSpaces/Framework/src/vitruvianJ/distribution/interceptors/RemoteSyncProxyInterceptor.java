package vitruvianJ.distribution.interceptors;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import castle.dynamicproxy.Invocation;
import javassist.*;

//import castle.dynamicproxy.Invocation;



import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.distribution.proxies.ILocalSyncProxy;
import vitruvianJ.distribution.proxies.IRemoteSyncProxy;
import vitruvianJ.distribution.proxies.ReflectionInfo;
import vitruvianJ.logging.JGUID;

public class RemoteSyncProxyInterceptor extends SyncProxyInterceptor implements IRemoteSyncProxy
{
    private List<JGUID> _brokers = new ArrayList<JGUID>();
    private boolean _isInitialized = false;

    public RemoteSyncProxyInterceptor(Type type, JGUID proxyId) throws Exception
    {
    	super(type, proxyId);
        ReflectionInfo info = ReflectionInfo.getReflectionInfo(type);
        Dictionary<Member, String> keyValuePairs = info.getMembersToPatterns();
        Enumeration<Member> entries = keyValuePairs.keys();
        while(entries.hasMoreElements())
        //for(Member entry : info.getMembersToPatterns().keys() )
        {
        	Member entry = entries.nextElement();
            ISyncPattern syncPattern = GetRemotePattern(keyValuePairs.get(entry));
            _syncPatterns.put(entry, syncPattern);
        }
    }

    public boolean Intercept(Invocation invocation)
    {
        if (invocation.getMethod().getDeclaringClass().equals(IRemoteSyncProxy.class))
        {
            try {
				invocation.setReturnValue(invocation.getMethod().invoke(this, invocation.getArgs()) );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        else
        {
            super.Intercept(invocation);
        }
        return false;
    }
    
    public boolean getIsInitialized()
    {
        return _isInitialized; 
    }
    public void setIsInitialized(boolean value) 
    { 
    	_isInitialized = value;
    }

    public JGUID getPreferredBroker()
    {
        synchronized(_brokers)
            {
                if (_brokers.size() > 0)
                    return (JGUID)_brokers.get(0);
                else
                    return null;
            }        
    }

    public int getNumBrokers()
    {
        return _brokers.size();
    }

    public void AddBroker(JGUID brokerId)
    {
        synchronized (_brokers)
        {
            if (!_brokers.contains(brokerId))
                _brokers.add(brokerId);
        }
    }

    public void RemoveBroker(JGUID brokerId)
    {
        synchronized (_brokers)
        {
            if (_brokers.contains(brokerId))
                _brokers.remove(brokerId);
        }
    }

	@Override
	public boolean IsInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<JGUID> getBrokers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInitialized(boolean value) {
		// TODO Auto-generated method stub
		_isInitialized = value;
		
	}

	 @Override
	    public JGUID getProxyId()
	    {
	        return _proxyId; 
	    }

	    
	    
}
