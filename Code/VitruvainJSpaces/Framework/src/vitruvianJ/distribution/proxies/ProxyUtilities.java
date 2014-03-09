package vitruvianJ.distribution.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Type;

import javassist.util.proxy.ProxyObject;
import javassist.*;

import vitruvianJ.communication.session.sockets.BaseChannel;
import vitruvianJ.distribution.gossip.ReplicationManager;
import vitruvianJ.distribution.interceptors.LocalSyncProxyInterceptor;
import vitruvianJ.distribution.interceptors.RemoteSyncProxyInterceptor;
import vitruvianJ.distribution.interceptors.SyncProxyInterceptor;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.IService;
import castle.dynamicproxy.*;

public class ProxyUtilities
{
	 private static JLogger _logger = new JLogger(ProxyUtilities.class);

    /// <summary>
    /// Generate a remote sync proxy for the given type.
    /// </summary>
    /// <param name="type"></param>
    /// <returns></returns>
    static public IRemoteSyncProxy GenerateRemoteProxy(JGUID id, Type type) throws Exception
    {
    	//type = ReplicationManager.class;
        Class baseType = (Class)getNonProxyBaseType(type);
        SyncProxyInterceptor interceptor = new RemoteSyncProxyInterceptor(baseType, id);        
        IRemoteSyncProxy result = null;//(IRemoteSyncProxy)JSProxyGenerator.createClassProxy(baseType, new Class[]{IRemoteSyncProxy.class}, null,interceptor,"R");
        interceptor.Init(result);
        return result;
    }

    /// <summary>
    /// Generate a local sync proxy for the type, and attach it to the parent.
    /// </summary>
    /// <param name="type"></param>
    /// <param name="parent"></param>
    /// <returns></returns>
    static public ILocalSyncProxy GenerateLocalProxy(JGUID id, Object parent) throws Exception
    {
        Class baseType = (Class)getNonProxyBaseType(parent.getClass());
        SyncProxyInterceptor interceptor = new LocalSyncProxyInterceptor(baseType, id, parent);
        //Object obj = ProxyGenerator.createClassProxy(baseType, new Class[]{ILocalSyncProxy.class}, interceptor);
        Object obj =null;//JSProxyGenerator.createClassProxy(baseType,new Class[]{ILocalSyncProxy.class} , null,interceptor, "L");
        //Object obj = new ReplicationManager_Proxy(new Interceptor[]{interceptor});// ProxyGenerator.createClassProxy(baseType, ILocalSyncProxy.class.getInterfaces(), interceptor); 
        ILocalSyncProxy result = (ILocalSyncProxy)obj;
        interceptor.Init(result);
        return result;
    }
    
    /// <summary>
    /// Determine if the object is a sync proxy.
    /// </summary>
    /// <param name="value"></param>
    /// <returns></returns>
    static public boolean IsSyncProxy(Object value)
    {
        if (value == null)
            return false;
        else
            return IsSyncProxy(value.getClass());
    }

    /// <summary>
    /// Determine if the Object is a sync proxy.
    /// </summary>
    /// <param name="type"></param>
    /// <returns></returns>
    static public boolean IsSyncProxy(Type type)
    {
        return type instanceof ISyncProxy;
    }

    /// <summary>
    /// Find the first base-type that isn't a generated proxy.
    /// </summary>
    /// <param name="baseType"></param>
    /// <returns></returns>
    static public Type getNonProxyBaseType(Type baseType)
    {

    	
        boolean findingBaseType = true;

        while (findingBaseType)
        {
            findingBaseType = false;

            Class baseClass = (Class) baseType;
            Type[] types = baseClass.getInterfaces();//GetInterfaces();
            for (Type iType : types)
            {
                if (iType.equals(ProxyObject.class))//TargetAccessor.class))
                {
                    findingBaseType = true;
                    baseType = baseClass.getSuperclass();//BaseType;
                }
            }
        }

        return baseType;
    }
}
