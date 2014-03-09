package castle.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.distribution.interceptors.SyncProxyInterceptor;

public class ProxyManager implements InvocationHandler
{
    private List<Interceptor> _interceptors = new ArrayList<Interceptor>();
    private Class[] _interfaces = null;
    private Object[] _delegates = null;

    public List<Interceptor> getInterceptors()
    {
        return _interceptors; 
    }

    public Object[] getDelegates()
    {
    	return _delegates;
    }
   public ProxyManager(Class[] interfaces, List<Interceptor> delegates)
   {
	   _interfaces = interfaces;
	   _interceptors = delegates;
   }
   
	   public Invocation Invokes(Method method, Object[] args){   		
        Invocation invocation = new Invocation(this, method, args);
        invocation.proceed();
        return invocation;
    }

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO Auto-generated method stub
		Invocation invocation = new Invocation(this, method, args);
        invocation.proceed();
        return invocation.getReturnValue();		
	}
}
