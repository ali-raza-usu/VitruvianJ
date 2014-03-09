package vitruvianJ.distribution.syncpatterns.mirrors.remote;

import java.lang.reflect.*;
import vitruvianJ.logging.*;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.sessions.*;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.distribution.proxies.*;



    public class RPCSyncPattern extends MirrorSyncPattern
	{
        private JLogger _logger = new JLogger(RPCSyncPattern.class);

        private boolean _updateArgs = false;

        public void Start()
        {
            super.Start();

            _updateArgs = false;

            if (_method != null)
            {
                for (Class<?> info : _method.getParameterTypes())
                {
                	if(!info.isPrimitive())
                	
                    //if (info.IsOut || info.ParameterType.IsByRef)
                        _updateArgs = true;
                }
            }
        }
        @Override
        public Object HandleMethod(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote RPC SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            try {
				return ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, _updateArgs, args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

        public Object HandlePropertyGet(Object... args)
		{
//            if (_logger.IsDebugEnabled())
//                _logger.DebugFormat("Remote RPC SyncPattern : %1s : %2 ", _method.getDeclaringClass().getName(), _method.getName());

            if (_initializing)
                return null;

            try {
				return ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, _updateArgs, args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

        public Object HandlePropertySet(Object... args)
		{
//            if (_logger.IsDebugEnabled())
//                _logger.DebugFormat("Remote RPC SyncPattern : %1s : %2", _method.getDeclaringClass().getName(), _method.getName());

            if (_initializing)
                return null;
			else
				try {
					return ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, _updateArgs, args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
        }
	}

