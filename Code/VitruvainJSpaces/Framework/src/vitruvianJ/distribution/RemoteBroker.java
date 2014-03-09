package vitruvianJ.distribution;

import vitruvianJ.distribution.session.processors.*;
import vitruvianJ.distribution.sessions.*;
import vitruvianJ.distribution.sessions.messages.*;
import vitruvianJ.distribution.sessions.protocols.ExecuteMethodProtocol;
import vitruvianJ.distribution.sessions.protocols.ExecuteSyncPatternMethodProtocol;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.session.protocols.*;
import vitruvianJ.distribution.proxies.IRemoteSyncProxy;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.logging.JGUID;


    class RemoteBroker
    {
        private JGUID _id = new JGUID();
        private List<ObjectSession> _sessions = new ArrayList<ObjectSession>();
        private List<JGUID> _knownObjectsIds = new ArrayList<JGUID>();

        public RemoteBroker(JGUID id)
        {
            _id = id;
        }

        public JGUID getId()
        {
            return _id; 
        }

        public void AddSession(ObjectSession session)
        {
            synchronized (_sessions)
            {
                _sessions.add(session);
            }
        }

        public void RemoveSession(ObjectSession session)
        {
            synchronized (_sessions)
            {
                _sessions.remove(session);
            }
        }

        public void InitializeObject(JGUID brokerId, IRemoteSyncProxy proxy) throws Exception
        {
            ObjectSession session = GetPreferredSession();

            if (session != null)
            {
                AsyncProtocol protocol = new AsyncProtocol(session, session.getMessageTimeout(), 1, null);
                protocol.BeginSend(new RequestInitializeObject(brokerId, proxy.getProxyId(), proxy));
                protocol.EndSend();

                if (!(protocol.getResponseMessage() instanceof ReplyInitializeObject))
                    throw new Exception(String.format("Failed to initialize the proxy {0}.", proxy.getProxyId()));
            }
            else
            {
                throw new Exception("Broker doesn't have a preferred session.");
            }
        }

        public Object ExecuteRemoteMethod(IRemoteSyncProxy proxy, Method mInfo, boolean updateArgs, Object[] args) throws Exception
        {
            ObjectSession session = GetPreferredSession();

            if (session != null)
            {
                ExecuteMethodProtocol protocol = new ExecuteMethodProtocol(session);
                return protocol.ExecuteFunction(proxy, mInfo, args, updateArgs, session.getMessageTimeout());
            }
            else
            {
                throw new Exception("Broker doesn't have a preferred session.");
            }
        }

        public AsyncProtocol BeginBroadcastExecuteRemoteMethod(List<JGUID> excludeList, ISyncProxy proxy, Method mInfo, Object[] args) throws Exception
        {
            ObjectSession session = GetPreferredSession();

            if (session != null)
            {
                ExecuteMethodProtocol protocol = new ExecuteMethodProtocol(session);
                protocol.BeginBroadcastExecuteFunction(excludeList, proxy, mInfo, args, session.getMessageTimeout());
                return protocol;
            }
            else
            {
                throw new Exception("Broker doesn't have a preferred session.");
            }
        }

        public AsyncProtocol BeginExecuteRemoteMethod(IRemoteSyncProxy proxy, Method mInfo, boolean updateArgs, Object[] args) throws Exception
        {
            ObjectSession session = GetPreferredSession();

            if (session != null)
            {
                ExecuteMethodProtocol protocol = new ExecuteMethodProtocol(session);
                protocol.BeginExecuteFunction(proxy, mInfo, args, updateArgs, session.getMessageTimeout());
                return protocol;
            }
            else
            {
                throw new Exception("Broker doesn't have a preferred session.");
            }
        }

        public Object ExecuteRemoteSyncPatternMethod(ISyncProxy proxy, Member proxyMember, String methodName, Object[] methodArgs) throws Exception
        {
            ObjectSession session = GetPreferredSession();

            if (session != null)
            {
                ExecuteSyncPatternMethodProtocol protocol = new ExecuteSyncPatternMethodProtocol(session);
                return protocol.ExecuteFunction(proxy, proxyMember, methodName, methodArgs, session.getMessageTimeout());
            }
            else
            {
                throw new Exception("Broker doesn't have a preferred session.");
            }
        }

        public AsyncProtocol BeginBroadcastExecuteRemoteSyncPatternMethod(List<JGUID> excludeList, ISyncProxy proxy, Member proxyMember, String methodName, Object[] methodArgs) throws Exception
        {
            ObjectSession session = GetPreferredSession();

            if (session != null)
            {
                ExecuteSyncPatternMethodProtocol protocol = new ExecuteSyncPatternMethodProtocol(session);
                protocol.BeginExecuteFunction(proxy, proxyMember, methodName, methodArgs, session.getMessageTimeout());
                return protocol;
            }
            else
            {
                throw new Exception("Broker doesn't have a preferred session.");
            }
        }

        public AsyncProtocol BeginExecuteRemoteSyncPatternMethod(ISyncProxy proxy, Member proxyMember, String methodName, Object[] methodArgs) throws Exception
        {
            ObjectSession session = GetPreferredSession();

            if (session != null)
            {
                ExecuteSyncPatternMethodProtocol protocol = new ExecuteSyncPatternMethodProtocol(session);
                protocol.BeginExecuteFunction(proxy, proxyMember, methodName, methodArgs, session.getMessageTimeout());
                return protocol;
            }
            else
            {
                throw new Exception("Broker doesn't have a preferred session.");
            }
        }

        private ObjectSession GetPreferredSession()
        {
            synchronized (_sessions)
            {
                if (_sessions.size() > 0)
                    return _sessions.get(0);
                else
                    return null;
            }
        }

        public void AddKnownObject(JGUID id)
        {
        	synchronized (_knownObjectsIds)
            {
                if (!_knownObjectsIds.contains(id))
                    _knownObjectsIds.add(id);
            }
        }

        public void RemoveKnownObject(JGUID id)
        {
        	synchronized (_knownObjectsIds)
            {
                if (_knownObjectsIds.contains(id))
                    _knownObjectsIds.remove(id);
            }
        }

        public boolean KnowsObject(JGUID id)
        {
        	synchronized (_knownObjectsIds)
            {
                return _knownObjectsIds.contains(id);
            }
        }

        public boolean isConnected()
        {
              synchronized (_sessions)
                {
                    return _sessions.size() > 0;
                }
        }
    }

