package vitruvianJ.distribution.interceptors;


import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.Enumeration;

import castle.dynamicproxy.Invocation;
import javassist.*;


//import castle.dynamicproxy.Invocation;

import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.distribution.proxies.ILocalSyncProxy;
import vitruvianJ.distribution.proxies.ReflectionInfo;
import vitruvianJ.logging.JGUID;

public class LocalSyncProxyInterceptor extends SyncProxyInterceptor implements ILocalSyncProxy
{
    private Object _proxyParent = null;

    public LocalSyncProxyInterceptor(Type type, JGUID proxyId, Object proxyParent) throws Exception
    {
    	super(type, proxyId);
        _proxyParent = proxyParent;

        ReflectionInfo info = ReflectionInfo.getReflectionInfo(type);
        Dictionary<Member, String> keyValuePairs = info.getMembersToPatterns();
        Enumeration<Member> entries = keyValuePairs.keys();
        
        while(entries.hasMoreElements())
        {
        	Member entry = entries.nextElement();
            ISyncPattern syncPattern = GetLocalPattern(keyValuePairs.get(entry));
            _syncPatterns.put(entry, syncPattern);
        }
    }

    public boolean Intercept(Invocation invocation)
    {
        if (ILocalSyncProxy.class.equals(invocation.getMethod().getDeclaringClass()) )
        {
            try {
            	Object value = invocation.getMethod().invoke(this, invocation.getArgs() );
				invocation.setReturnValue(value);
				return invocation.getHandled();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        else
        {
            return super.Intercept(invocation);
        }
		return false;
    }

    @Override
    public JGUID getProxyId()
    {
        return _proxyId; 
    }

    
    @Override
    public Object getProxyParent()
    {
        return _proxyParent; 
    }

    
}
