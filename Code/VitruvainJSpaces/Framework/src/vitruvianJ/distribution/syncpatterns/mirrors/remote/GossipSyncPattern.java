package vitruvianJ.distribution.syncpatterns.mirrors.remote;

import java.lang.reflect.*;
import vitruvianJ.logging.*;
import vitruvianJ.services.*;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.gossip.*;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.syncpatterns.mirrors.PropertySyncPattern;

    public class GossipSyncPattern extends PropertySyncPattern
	{
        private JLogger _logger = new JLogger(GossipSyncPattern.class);
        private FrontEnd _frontEnd = null;
        private Object _initialValue = null;
        private boolean _initialized = false;

        public void Start()
        {
            super.Start();

            ReplicationManager manager = (ReplicationManager)ServiceRegistry.getPreferredService(ReplicationManager.class);

            if (manager == null)
				try {
					throw new Exception("The Gossip pattern requires a replication manager to be in the service registry.");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

            if (!_initialized)
            {
                try {
					_initialValue = ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, false, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                _initialized = true;
            }

            // use the proxy id as the replica id
            JGUID replicaId = _proxy.getProxyId();

            manager.AddReplica(replicaId, _initialValue);
            _frontEnd = new FrontEnd(replicaId, manager.getSize());
        }

        public Object HandleMethod(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote Gossip SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            //throw new NotImplementedException();
            return null;
		}

        public Object HandlePropertyGet(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote Gossip SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (_initializing)
                return null;
            
            Object result = null;
            if (_frontEnd != null)
				try {
					result = _frontEnd.Get();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            return result;
		}

        public Object HandlePropertySet(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote Gossip SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (_initializing)
            {
                _initialValue = args[args.length - 1];
                _initialized = true;
            } else
				try {
					_frontEnd.Update(args[args.length - 1]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            FirePropertyChangedEvent(_proxy);

            return null;
        }
	}

