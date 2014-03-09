package vitruvianJ.distribution.syncpatterns.mirrors.remote;

import java.lang.reflect.*;

import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.logging.JGUID;

import java.util.*;


    /// <summary>
    /// This is a sync pattern that pushes the value of a
    /// a property.  This pattern can only work with properties that
    /// do not have any arguments.
    /// </summary>
    public class PushSyncPattern extends MirrorSyncPattern
	{
        private Object _value = null;
        private boolean _initialized = false;

        public Object HandleMethod(Object... args)
		{
            //throw new NotImplementedException();
            return null;
		}

        public Object HandlePropertyGet(Object... args)
		{
            if (_initializing)
                return null;

            if (!_initialized)
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
            if (_initializing)
            {
                _value = args[args.length - 1];
                _initialized = true;
            }
            else
            {
                try {
					ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _field, "RemoteValueChanged", args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            return null;
        }

        private void RemoteValueChanged(Object value)
        {
            _value = value;
        }
	}

