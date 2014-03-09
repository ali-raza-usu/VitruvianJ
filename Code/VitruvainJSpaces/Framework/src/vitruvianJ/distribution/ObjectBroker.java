package vitruvianJ.distribution;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import vitruvianJ.communication.session.protocols.AsyncProtocol;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.sessions.ObjectSession;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;

public class ObjectBroker
{
	static JLogger _logger = new JLogger(ObjectBroker.class);
    static private JGUID _id = new JGUID();
    static private List<RemoteBroker> _remoteBrokers = new ArrayList<RemoteBroker>();

    static private Hashtable<JGUID, ISyncProxy> _proxies = new Hashtable<JGUID, ISyncProxy>();
    static private Hashtable<Object, ILocalSyncProxy> _parentToProxy = new Hashtable<Object, ILocalSyncProxy>();

    /// <summary>
    /// The id of this object broker.
    /// </summary>
    static public JGUID getId()
    {
        return _id; 
    }
    public static void setId(JGUID value) 
    {
    	_id = value;
    }

    /// <summary>
    /// Is the broker know by the system.
    /// </summary>
    /// <param name="brokerId"></param>
    /// <returns></returns>
    static public boolean isKnownBroker(JGUID brokerId)
    {
        synchronized (_remoteBrokers)
        {
            return GetBroker(brokerId) != null;
        }
    }

    /// <summary>
    /// Add a remote broker to the system.
    /// </summary>
    /// <param name="id"></param>
    static public void AddBroker(JGUID brokerId)
    {
        synchronized (_remoteBrokers)
        {
            if (GetBroker(brokerId) == null)
            {
                _remoteBrokers.add(new RemoteBroker(brokerId));
            }
        }
    }

    /// <summary>
    /// Add a session to a remote broker.
    /// </summary>
    /// <param name="brokerId"></param>
    /// <param name="session"></param>
    static public void AddSessionToBroker(JGUID brokerId, ObjectSession session)
    {
        RemoteBroker remoteBroker = GetBroker(brokerId);
        if (remoteBroker != null)
            remoteBroker.AddSession(session);
    }

    /// <summary>
    /// Remove the session from the system.
    /// This can cause remote brokers to be removed from the system.
    /// This can cause remote proxies to be orphaned.
    /// </summary>
    /// <param name="session"></param>
    static public void RemoveSession(ObjectSession session)
    {
        synchronized (_remoteBrokers)
        {
            for (int i = _remoteBrokers.size() - 1; i >= 0; i--)
            {
                _remoteBrokers.get(i).RemoveSession(session);

                // remove disconnected brokers
                if (!_remoteBrokers.get(i).isConnected() )
                {                    
                        JGUID brokerId = _remoteBrokers.get(i).getId();

                        // remove the broker from proxies
                        Enumeration<ISyncProxy> e = _proxies.elements();
                        while(e.hasMoreElements())
                        {
                        	ISyncProxy proxy = e.nextElement();
                            if (proxy instanceof IRemoteSyncProxy)
                                ((IRemoteSyncProxy)proxy).RemoveBroker(brokerId);
                        }
                    

                    _remoteBrokers.remove(i);
                }
            }
        }            
    }

    /// <summary>
    /// Add the object to the broker.
    /// </summary>
    /// <param name="brokerId"></param>
    /// <param name="proxyId"></param>
    static public void AddKnownObjectToBroker(JGUID brokerId, JGUID proxyId)
    {
        RemoteBroker remoteBroker = GetBroker(brokerId);
        if (remoteBroker != null)
            remoteBroker.AddKnownObject(proxyId);
    }

    /// <summary>
    /// Remove the object from the broker.
    /// </summary>
    /// <param name="brokerId"></param>
    /// <param name="proxyId"></param>
    static public void RemoveKnownObjectFromBroker(JGUID brokerId, JGUID proxyId)
    {
        RemoteBroker remoteBroker = GetBroker(brokerId);
        if (remoteBroker != null)
            remoteBroker.RemoveKnownObject(proxyId);
    }

    /// <summary>
    /// Determine if the broker knows about the object.
    /// </summary>
    /// <param name="brokerId"></param>
    /// <param name="proxyId"></param>
    /// <returns></returns>
    static public boolean DoesBrokerKnowObject(JGUID brokerId, JGUID proxyId)
    {
        RemoteBroker remoteBroker = GetBroker(brokerId);
        if (remoteBroker != null)
            return remoteBroker.KnowsObject(proxyId);
        else
            return false;
    }

    /// <summary>
    /// Initialize the object.
    /// </summary>
    /// <param name="brokerId"></param>
    /// <param name="proxy"></param>
    static public void InitializeObject(JGUID brokerId, IRemoteSyncProxy proxy) throws Exception
    {
        RemoteBroker remoteBroker = GetBroker(brokerId);
        if (remoteBroker != null)
            remoteBroker.InitializeObject(_id, proxy);
    }

    /// <summary>
    /// Execute the method in a remote process.
    /// </summary>
    /// <param name="proxy"></param>
    /// <param name="mInfo"></param>
    /// <param name="args"></param>
    /// <param name="updateArgs"></param>
    /// <returns></returns>
    static public Object ExecuteRemoteMethod(IRemoteSyncProxy proxy, Method mInfo, boolean updateArgs, Object... args) throws Exception
    {
        JGUID brokerId = proxy.getPreferredBroker();

        if (brokerId != null)
        {
            RemoteBroker remoteBroker = GetBroker(brokerId);

            if (remoteBroker != null)
            {
                return remoteBroker.ExecuteRemoteMethod(proxy, mInfo, updateArgs, args);
            }
            else
            {
                throw new Exception("The preferred broker doesn't exist.");
            }
        }
        else
        {
            throw new Exception("The proxy doesn't have a preferred broker.");
        }
    }

    /// <summary>
    /// Execute the method in a remote process.
    /// </summary>
    /// <param name="proxy"></param>
    /// <param name="mInfo"></param>
    /// <param name="args"></param>
    /// <param name="updateArgs"></param>
    /// <returns></returns>
    static public void BroadcastExecuteRemoteMethod(List<JGUID> excludeList, ISyncProxy proxy, Method mInfo, Object... args) throws Exception
    {
        List<AsyncProtocol> protocols = new ArrayList<AsyncProtocol>();

        synchronized (_remoteBrokers)
        {
            List<JGUID> newExcludeList = new ArrayList<JGUID>();
            newExcludeList.add(getId());
            newExcludeList.addAll(excludeList);

            // exclude any brokers in this list
            for(RemoteBroker remoteBroker : _remoteBrokers)
            {
                newExcludeList.add(remoteBroker.getId());
            }

            for(RemoteBroker remoteBroker : _remoteBrokers)
            {
                if (!excludeList.contains(remoteBroker.getId()))
                {
                    AsyncProtocol protocol = remoteBroker.BeginBroadcastExecuteRemoteMethod(newExcludeList, proxy, mInfo, args);
                    protocols.add(protocol);
                }
            }
        }

        // wait for all of the responses
        for (int i = 0; i < protocols.size(); i++)
        {
            protocols.get(i).EndSend();
        }
    }

    /// <summary>
    /// Execute the method in a remote process.
    /// </summary>
    /// <param name="proxy"></param>
    /// <param name="mInfo"></param>
    /// <param name="args"></param>
    /// <param name="updateArgs"></param>
    /// <returns></returns>
    public static Object BeginExecuteRemoteMethod(IRemoteSyncProxy proxy, Method mInfo, boolean updateArgs, Object... args) throws Exception
    {
        JGUID brokerId = proxy.getPreferredBroker(); //JGUID?

        if (brokerId != null)
        {
            RemoteBroker remoteBroker = GetBroker(brokerId.getJGUID());

            if (remoteBroker != null)
            {
                return remoteBroker.BeginExecuteRemoteMethod(proxy, mInfo, updateArgs, args);
            }
            else
            {
                throw new Exception("The preferred broker doesn't exist.");
            }
        }
        else
        {
            throw new Exception("The proxy doesn't have a preferred broker.");
        }
    }

    /// <summary>
    /// Execute the sync method in a remote process.
    /// </summary>
    /// <param name="proxy"></param>
    /// <param name="proxyMethod"></param>
    /// <param name="methodName"></param>
    /// <param name="methodArgs"></param>
    /// <returns></returns>
    static public Object ExecuteRemoteSyncPatternMethod(JGUID brokerId, ISyncProxy proxy, Member proxyMember, String methodName, Object... methodArgs) throws Exception
    {
        RemoteBroker remoteBroker = GetBroker(brokerId);

        if (remoteBroker != null)
        {
            return remoteBroker.ExecuteRemoteSyncPatternMethod(proxy, proxyMember, methodName, methodArgs);
        }
        else
        {
            throw new Exception(String.format("The broker {0} doesn't exist.", brokerId));
        }
    }

    /// <summary>
    /// Execute the method in a remote process.
    /// </summary>
    /// <param name="proxy"></param>
    /// <param name="mInfo"></param>
    /// <param name="args"></param>
    /// <param name="updateArgs"></param>
    /// <returns></returns>
    static public List<AsyncProtocol> BroadcastExecuteRemoteSyncPatternMethod(List<JGUID> excludeList, ISyncProxy proxy, Member proxyMethod, String methodName, Object... methodArgs) throws Exception
    {
        List<AsyncProtocol> result = new ArrayList<AsyncProtocol>();

        synchronized (_remoteBrokers)
        {
            List<JGUID> newExcludeList = new ArrayList<JGUID>();
            newExcludeList.add(getId());
            newExcludeList.addAll(excludeList);

            // exclude any brokers in this list
            for(RemoteBroker remoteBroker : _remoteBrokers)
            {
                newExcludeList.add(remoteBroker.getId());
            }

            for(RemoteBroker remoteBroker : _remoteBrokers)
            {
                if (!excludeList.contains(remoteBroker.getId()))
                {
                    AsyncProtocol protocol = remoteBroker.BeginBroadcastExecuteRemoteSyncPatternMethod(newExcludeList, proxy, proxyMethod, methodName, methodArgs);
                    result.add(protocol);
                }
            }
        }

        return result;
    }

    /// <summary>
    /// Execute the sync method in a remote process.
    /// </summary>
    /// <param name="proxy"></param>
    /// <param name="proxyMethod"></param>
    /// <param name="methodName"></param>
    /// <param name="methodArgs"></param>
    /// <returns></returns>
    static public AsyncProtocol BeginExecuteRemoteSyncPatternMethod(JGUID brokerId, ISyncProxy proxy, Method proxyMethod, String methodName, Object... methodArgs) throws Exception
    {
        RemoteBroker remoteBroker = GetBroker(brokerId);

        if (remoteBroker != null)
        {
            return remoteBroker.BeginExecuteRemoteSyncPatternMethod(proxy, proxyMethod, methodName, methodArgs);
        }
        else
        {
            throw new Exception(String.format("The broker %1s doesn't exist.", brokerId));
        }
    }

    /// <summary>
    /// Get the broker with the id.
    /// </summary>
    /// <param name="brokerId"></param>
    /// <returns></returns>
    static private RemoteBroker GetBroker(JGUID brokerId)
    {
        synchronized (_remoteBrokers)
        {
            for (int i = 0; i < _remoteBrokers.size(); i++)
            {
                if (_remoteBrokers.get(i).getId().toString().equals(brokerId.toString()))
                    return _remoteBrokers.get(i);
            }

            return null;
        }
    }

    /// <summary>
    /// Get the proxy with the id.
    /// </summary>
    /// <param name="id"></param>
    /// <returns></returns>
    static public ISyncProxy GetProxy(JGUID id)
    {
       
        synchronized (_proxies)
        {
        	Set<Entry<JGUID,ISyncProxy>> e = _proxies.entrySet();
        	Iterator itr = e.iterator();
            while (itr.hasNext())
            {
            	Entry<JGUID,ISyncProxy> element = (Entry<JGUID,ISyncProxy>)itr.next();
            	if(element.getKey().toString().equals(id.toString()))
            	{
            		return element.getValue();            		
            	}
            }
            return null;
        }
    }

    /// <summary>
    /// Get or generate a remote proxy with the id.
    /// </summary>
    /// <param name="id"></param>
    /// <param name="type"></param>
    /// <returns></returns>
    static public IRemoteSyncProxy GetRemoteProxy(JGUID brokerId, JGUID proxyId, Type type) throws Exception
    {
        synchronized (_proxies)
        {
            if (_proxies.get(proxyId) == null)
            {
            	_logger.Debug("Object Broker :  Remote Proxy Id : " + proxyId + " Type : " + type);
                IRemoteSyncProxy proxy = ProxyUtilities.GenerateRemoteProxy(proxyId, type);
                _proxies.put(proxyId, proxy);

                AddKnownObjectToBroker(brokerId, proxyId);
                proxy.AddBroker(brokerId);
            }

            return (IRemoteSyncProxy)_proxies.get(proxyId);
        }
    }

    /// <summary>
    /// Get or generate a local proxy with the id.
    /// </summary>
    /// <param name="value"></param>
    /// <returns></returns>
    static public ILocalSyncProxy GetLocalProxy(Object value) throws Exception
    {
        synchronized (_parentToProxy)
        {
            if (_parentToProxy.get(value) == null)
            {
                ILocalSyncProxy proxy = ProxyUtilities.GenerateLocalProxy(new JGUID(), value);
                JGUID proxy_value = proxy.getProxyId();
                _logger.Debug("Object Broker :  Local Proxy Id : " + proxy_value + " for " + value.getClass());
                synchronized(_proxies)
                {
                	_proxies.put(proxy_value, proxy);
                }
                _parentToProxy.put(value, proxy);

                proxy.StartSyncPatterns();
            }

            ILocalSyncProxy _proxy =  _parentToProxy.get(value);
            return _proxy;//(ILocalSyncProxy)_parentToProxy.get(value);
        }
    }

    /// <summary>
    /// Determine if the id is to a local proxy.
    /// </summary>
    /// <param name="id"></param>
    /// <returns></returns>
    static public boolean IsLocalProxy(JGUID id)
    {
        synchronized (_proxies)
        {
        	Set<Entry<JGUID,ISyncProxy>> e = _proxies.entrySet();
        	Iterator itr = e.iterator();
            while (itr.hasNext())
            {
            	Entry<JGUID,ISyncProxy> element = (Entry<JGUID,ISyncProxy>)itr.next();
            	if(element.getKey().toString().equals(id.toString()))
            	{
            		return element.getValue() instanceof ILocalSyncProxy;            		
            	}
            }
            return false;        	
        }
    }

    
    
    /// <summary>
    /// Determine if the id is to a remote proxy.
    /// </summary>
    /// <param name="id"></param>
    /// <returns></returns>
    static public boolean IsRemoteProxy(JGUID id)
    {
        synchronized (_proxies)
        {
        	Set<Entry<JGUID,ISyncProxy>> e = _proxies.entrySet();
        	Iterator itr = e.iterator();
            while (itr.hasNext())
            {
            	Entry<JGUID,ISyncProxy> element = (Entry<JGUID,ISyncProxy>)itr.next();
            	if(element.getKey().toString().equals(id.toString()))
            	{
            		return element.getValue() instanceof IRemoteSyncProxy;            		
            	}
            }
            return false;        	
        }
    }

    /// <summary>
    /// Get the list of known objects for this broker.
    /// </summary>
    /// <returns></returns>
    static public List<JGUID> GetKnownObjectIds()
    {
        List<JGUID> result = new ArrayList<JGUID>();

        synchronized (_proxies)
        {
        	Enumeration<JGUID> keys = _proxies.keys();
            while(keys.hasMoreElements())
            {
            	JGUID proxyId = keys.nextElement();
                result.add(proxyId);
            }
        }

        return result;
    }

    /// <summary>
    /// Get the list of orphaned proxies for this broker.
    /// </summary>
    /// <returns></returns>
    static public List<IRemoteSyncProxy> GetOrphanedProxies()
    {
        List<IRemoteSyncProxy> result = new ArrayList<IRemoteSyncProxy>();

        synchronized (_proxies)
        {
        	Enumeration<ISyncProxy> values = _proxies.elements();
            while(values.hasMoreElements())
            {
            	ISyncProxy proxy = values.nextElement();
                IRemoteSyncProxy remoteProxy = (IRemoteSyncProxy)proxy ;
                if (remoteProxy != null)
                {
                    if (remoteProxy.getBrokers().size() == 0)
                    {
                        if (!ReflectionInfo.getReflectionInfo(remoteProxy.getClass()).getIsMigratable())
                            result.add(remoteProxy);
                    }
                }
            }
        }

        return result;
    }
}
