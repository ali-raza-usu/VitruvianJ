package vitruvianJ.distribution.syncpatterns.mirrors.remote;


import java.lang.reflect.*;
import vitruvianJ.distribution.*;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.sessions.protocols.*;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.logging.*;


    public class ConstantSyncPattern extends MirrorSyncPattern
	{
        private JLogger _logger = new JLogger(ConstantSyncPattern.class);
        private static boolean _initialized = false;
        private static Object _value = null;

        public Object HandleMethod(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote Constant SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

			return _value;
		}

        public Object HandlePropertyGet(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote Constant SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (!_initializing && !_initialized)
            {
                try {
					_value = ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, false, args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                _initialized = true;
            }

			return _value;
		}

        public Object HandlePropertySet(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote Constant SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (_initializing)
            {
                _value = args[args.length - 1];
                _initialized = true;
                return null;
            }
            else
            {
                // it is constant!
                try {
					throw new Exception();					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
            }
		}
	}
