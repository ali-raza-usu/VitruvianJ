package castle.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;

import proxyGeneration.exp2.Delegator;

import vitruvianJ.distribution.interceptors.SyncProxyInterceptor;
import vitruvianJ.distribution.proxies.IRemoteSyncProxy;

public class ProxyGenerator {

	
	public static Object createClassProxy(Class<?> baseType, Class<?>[] interfaces, Interceptor interceptor){
		
		ArrayList<Interceptor> list = new ArrayList<Interceptor>();
		list.add(interceptor);
		ClassLoader loader = baseType.getClassLoader();
		//loader.
		return Proxy.newProxyInstance(baseType.getClassLoader(), interfaces, new ProxyManager(interfaces, list) );
	}
	
}
