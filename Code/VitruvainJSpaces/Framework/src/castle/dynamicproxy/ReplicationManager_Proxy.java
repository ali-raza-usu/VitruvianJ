package castle.dynamicproxy;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import castle.dynamicproxy.Interceptor;
import castle.dynamicproxy.Invocation;
import castle.dynamicproxy.ProxyManager;

import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.distribution.gossip.ReplicationManager;
import vitruvianJ.distribution.proxies.ILocalSyncProxy;
import vitruvianJ.logging.JGUID;

public class ReplicationManager_Proxy  extends ReplicationManager implements ILocalSyncProxy{
private ProxyManager _proxyManager = null;
	
	private List<Interceptor> _interceptors = new ArrayList<Interceptor>();
	
    //How to populate the interceptors
	//How to get the invocation
	//How to get the proxyId, would it be different from the id of ReplicationManager
	//What is memberInfo
	//How to return the ISyncPattern
	
	public ReplicationManager_Proxy(Class[] interfaces, List<Interceptor> delegates,Interceptor... interceptors)
	{
		_proxyManager = new ProxyManager(interfaces, delegates);
		for(Interceptor intercp : interceptors)
		_proxyManager.getInterceptors().add(intercp);
	}
	
	void callInterceptors()
	{
		for(int i =0; i<_interceptors.size(); i++)
		{
			Interceptor interceptor = _interceptors.get(i);
			//interceptor.intercept(invocation);
		}
	}

	@Override
	public JGUID getProxyId() {
		// TODO Auto-generated method stub
		Invocation invocation = null;
		try {
			invocation = (Invocation) _proxyManager.invoke(this, getCurrentMethod("getProxyId", null), null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(invocation.getHandled())
			return (JGUID)invocation.getReturnValue();
		else
			return null;//getProxyId();
	}

	@Override
	public ISyncPattern getSyncPattern(Member memberInfo) {
		Invocation invocation = null;
		try {
			invocation = (Invocation) _proxyManager.invoke(this, getCurrentMethod("getSyncPattern", memberInfo.getClass()), new Object[]{memberInfo} );
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(invocation.getHandled())
			return (ISyncPattern)invocation.getReturnValue();
		// TODO Auto-generated method stub
		else
			return null;//getSyncPattern(memberInfo);
	}

	@Override
	public void StartSyncPatterns() {
		
		try {
			Invocation invocation = (Invocation) _proxyManager.invoke(this, getCurrentMethod("StartSyncPatterns",null), null );
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void StopSyncPatterns() {
		try {
			Invocation invocation = (Invocation) _proxyManager.invoke(this, getCurrentMethod("StopSyncPatterns",null), null );
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Object getProxyParent() {
		// TODO Auto-generated method stub
		Invocation invocation = null;
		try {
			invocation = (Invocation) _proxyManager.invoke(this, getCurrentMethod("getProxyParent"), null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(invocation.getHandled())
			return invocation.getReturnValue();
		else			
			return null;//getProxyParent();
	}

	

   public Method getCurrentMethod(String name, Class<?>... types)
   {
	   String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
	  
		Method method = null;
		try {
			method = this.getClass().getMethod(name, types);
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return method;
	   
   }
}
