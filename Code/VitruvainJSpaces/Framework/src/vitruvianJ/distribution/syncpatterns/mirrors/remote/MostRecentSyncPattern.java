package vitruvianJ.distribution.syncpatterns.mirrors.remote;

import java.lang.reflect.*;
import vitruvianJ.services.*;
import vitruvianJ.logging.*;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.syncpatterns.mirrors.PropertySyncPattern;
import vitruvianJ.distribution.time.*;
import vitruvianJ.distribution.proxies.*;
import java.util.*;


    /// <summary>
    /// SyncPattern that ties a property to a remote proxy
    /// using the value with the latest time.  The time from
    /// a global time service is used.
    /// </summary>
    public class MostRecentSyncPattern extends PropertySyncPattern
    {
        private JLogger _logger = new JLogger(MostRecentSyncPattern.class);

        private GlobalTime _timeService = null;
        private Date _lastTime = new Date(0);
        private Object _lastValue = null;

        private String _propertyName = "";
        private boolean _initialized = false;

        /// <summary>
        /// Start the sync pattern.
        /// </summary>
        public void Start()
        {
            super.Start();

            _propertyName = _field.getName();
            _timeService = (GlobalTime)ServiceRegistry.getPreferredService(GlobalTime.class);
        }

        /// <summary>
        /// This sync pattern is for properties only.
        /// </summary>
        /// <param name="args"></param>
        /// <returns></returns>
        public Object HandleMethod(Object... args)
        {
            //throw new NotImplementedException();
        	return null;
        }

        /// <summary>
        /// Initialize the value of this property by communicating with the remote object.
        /// If the value is already initialized, then just return the last known value.
        /// </summary>
        /// <param name="args"></param>
        /// <returns></returns>
        public Object HandlePropertyGet(Object... args)
        {
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote MostRecent SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (!_initializing && !_initialized)
            {
                try {
					_lastValue = ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, false, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                _initialized = true;
            }

            return _lastValue;
        }

        /// <summary>
        /// Set the last known value, and then set the remote object's property to this value.
        /// </summary>
        /// <param name="args"></param>
        public Object HandlePropertySet(Object... args)
        {
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote MostRecent SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (_initializing)
            {
                _lastValue = args[args.length - 1];
                _initialized = true;
            }
            else
            {
                _lastTime = _timeService.getUtcTime();

                // value is the last arg
                _lastValue = args[args.length - 1];

                try {
					ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _field, "ValueChangedRemotely", _lastValue, _lastTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//.ToBinary());
                FirePropertyChangedEvent(_proxy);
            }

            return null;
        }

        /// <summary>
        /// Method called when the local object reports a property changed event.
        /// Only assign the value if it was changed after the latest recorded change.
        /// </summary>
        /// <param name="value"></param>
        /// <param name="time"></param>
        public void ValueChangedRemotely(Object value, long utcTime)
        {
            Date time =  new Date(utcTime);

            if (time.compareTo(_lastTime) > 0)
            {
                if (_logger.IsDebugEnabled())
                    _logger.Debug("Remote MostRecent SyncPatten : ValueChangedRemotely");

                _lastTime = time;
                _lastValue = value;

                FirePropertyChangedEvent(_proxy);
            }
        }
    }

