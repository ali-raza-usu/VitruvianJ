package vitruvianJ.distribution.interceptors;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import castle.dynamicproxy.Interceptor;
import castle.dynamicproxy.Invocation;
import javassist.*;



//import castle.dynamicproxy.*;

import vitruvianJ.collections.SingletonSession;
import vitruvianJ.core.ClassFactory;
import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.distribution.SyncPatternsService;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.syncpatterns.InterceptCommand;
import vitruvianJ.distribution.syncpatterns.SyncPattern;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.ServiceRegistry;

public class SyncProxyInterceptor implements Interceptor, ISyncProxy //InvocationHandler, ISyncProxy //IInterceptor, ISyncProxy
{
	JLogger _logger = new JLogger(SyncProxyInterceptor.class);
    static private Dictionary<String, Type> _localPatterns = null;
    static private Dictionary<String, Type> _remotePatterns = null;

    static private boolean _initialized = false;

    /// <summary>
    /// Initialize the sync patterns.
    /// </summary>
  public SyncProxyInterceptor() throws Exception
    {
        initializeConstructor();
    }

	private void initializeConstructor() throws Exception {
		if (_initialized)
            return;
        _initialized = true;

        SyncPatternsService service = (SyncPatternsService)ServiceRegistry.getPreferredService(SyncPatternsService.class);
        if (service == null)
            throw new Exception("The ServiceRegistry needs a SyncPatternsService to define the SyncPatterns used by SyncProxies.");

        _localPatterns = service.getLocalPatterns();
        _remotePatterns = service.getRemotePatterns();

        if (_localPatterns == null || _remotePatterns == null)
            throw new Exception("The SyncPatternsService didn't define the SyncPatterns.");
	}

    static protected ISyncPattern GetLocalPattern(String id) throws Exception
    {
        if (_localPatterns.get(id)!= null)
            return (SyncPattern)ClassFactory.CreateObject(_localPatterns.get(id));
        else
            throw new Exception(String.format("Undefined remote pattern %1s.", id));
    }

    static protected ISyncPattern GetRemotePattern(String id) throws Exception
    {
        if (_remotePatterns.get(id)!= null)
            return (SyncPattern)ClassFactory.CreateObject(_remotePatterns.get(id));
        else
            throw new Exception(String.format("Undefined local pattern %1s.", id));
    }

    protected Type _type = null;
    protected JGUID _proxyId = null;
    protected Dictionary<Member, ISyncPattern> _syncPatterns = new Hashtable<Member, ISyncPattern>();

    private Hashtable<Method, ISyncPattern> _methodsToSyncPatterns = new Hashtable<Method, ISyncPattern>();

    public SyncProxyInterceptor(Type type, JGUID proxyId)
    {
    	 try {
			initializeConstructor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        _type = type;
        _proxyId = proxyId;
    }

    public void Init(ISyncProxy proxy)
    {
    	Enumeration<Member> elements = _syncPatterns.keys();

    	while(elements.hasMoreElements())
        {        	
    		Member element = elements.nextElement();
    		ISyncPattern value = _syncPatterns.get(element);
            if (element instanceof Field)
            {
                Field fInfo = (Field)element;
                value.Init(proxy, null, fInfo);
                Method getMethod = null;//fInfo.GetGetMethod();
                Method setMethod = null;//fInfo.GetSetMethod();
                
                _methodsToSyncPatterns.put(getMethod, _syncPatterns.get(elements));
                _methodsToSyncPatterns.put(setMethod, _syncPatterns.get(elements));
            }
            else if (element instanceof Method)
            {
                Method method = (Method)element;
                value.Init(proxy, method, null);
                _methodsToSyncPatterns.put(method, value);
            }
        }
    }



    

    public JGUID getProxyId()
    {
        return _proxyId; 
    }
    
    public void StartSyncPatterns()
    {
    	Enumeration<ISyncPattern> elements = _syncPatterns.elements();
    	while(elements.hasMoreElements())        
        {
    		try{
    		ISyncPattern pattern = elements.nextElement();
            pattern.Start();
    		}catch(Exception ex)
    		{
    			_logger.Debug(ex.getStackTrace().toString());
    		}
        }
    }

    public void StopSyncPatterns()
    {
    	Enumeration<ISyncPattern> elements = _syncPatterns.elements();
    	while(elements.hasMoreElements())        
        {
            elements.nextElement().Stop();
        }
    }

	@Override
	public ISyncPattern getSyncPattern(Member memberInfo) {
		// TODO Auto-generated method stub
		return _syncPatterns.get(memberInfo);
	}

	Method getMethod(Method method)
	{
		
		Method[] methods = this.getClass().getMethods();
		for(Method myMethod : methods)
		{
			if(myMethod.getName().equals(method.getName()) )
				return myMethod;
		}
		return null;
	}
	@Override
	public boolean Intercept(Invocation invocation) {
			
		SingletonSession.getInstance(_methodsToSyncPatterns);
		if (ISyncProxy.class.isAssignableFrom(invocation.getMethod().getDeclaringClass()) )
	        {
	            try {
	            	Method method = getMethod(invocation.getMethod());
					invocation.setReturnValue(method.invoke(this, invocation.getArgs()));//invocation.getArguments()));
					invocation.setHandled(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	        }
	        else if (_methodsToSyncPatterns.get(invocation.getMethod()) != null)
	        {	        	
	            ISyncPattern syncPattern = _methodsToSyncPatterns.get(invocation.getMethod());
	
	            Object returnValue = null;
	
	            int a = 2;
//	            if(syncPattern.toString().contains("RPC"))
//	            {
//	            	a = 5;
//	              returnValue = syncPattern.HandleMethod(invocation.getArgs());//invocation.getArguments());
//	            }
//	            else{
		            if (invocation.getMethod().getName().startsWith("get"))
		                returnValue = syncPattern.HandlePropertyGet(invocation.getArgs());// invocation.getArguments());
		            else if (invocation.getMethod().getName().startsWith("set"))
		                returnValue = syncPattern.HandlePropertySet(invocation.getArgs());//invocation.getArguments());
		            else
		                returnValue = syncPattern.HandleMethod(invocation.getArgs());//invocation.getArguments());
	    //       }
	            if (returnValue instanceof InterceptCommand)
	            {
	                switch ((InterceptCommand)returnValue)
	                {
	                    case CallBase:
	                        {
	                            invocation.proceed();
	                            break;
	                        }
	                    case CallParent:
	                        {
	                            ILocalSyncProxy syncProxy = (ILocalSyncProxy)syncPattern.getProxy();
	                            try {
									invocation.setReturnValue(invocation.getMethod().invoke(syncProxy.getProxyParent(), invocation.getArgs()) );
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                            break;
	                        }
	                }
	            }
	            else
	                invocation.setReturnValue(returnValue);
	        }
	        else
	        {
	            invocation.proceed();
	        }
		return invocation.getHandled();
	}




	



}
