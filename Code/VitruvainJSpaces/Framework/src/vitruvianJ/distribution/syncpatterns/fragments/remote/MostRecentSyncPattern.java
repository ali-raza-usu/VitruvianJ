package vitruvianJ.distribution.syncpatterns.fragments.remote;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.IRemoteSyncProxy;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.syncpatterns.InterceptCommand;
import vitruvianJ.distribution.syncpatterns.fragments.PropertySyncPattern;
import vitruvianJ.distribution.time.GlobalTime;
import vitruvianJ.logging.JGUID;

import vitruvianJ.logging.JLogger;
import vitruvianJ.services.ServiceRegistry;

/// <summary>
/// SyncPattern that ties a property to a remote proxy
/// using the value with the latest time.  The time from
/// a global time service is used.
/// </summary>
public class MostRecentSyncPattern extends PropertySyncPattern
{
    private JLogger _logger = new JLogger(MostRecentSyncPattern.class);
    private boolean _initialized = false;
    private boolean _syncingValue = false;

    private GlobalTime _timeService = null;
    private Date _lastTime = new Date(0);

    
    public void Start()
    {
        super.Start();

        // remember the time service
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
            _logger.DebugFormat("Remote MostRecent SyncPattern : %1s : %2s", _methodGet.getDeclaringClass().getName(), _methodGet.getName());

        if (!_initializing && !_initialized)
        {
            try {
				Object value = ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _methodGet, false, args);
			
            _methodSet.invoke(_proxy, args);
            _initialized = true;
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return InterceptCommand.CallBase;
    }

    /// <summary>
    /// Set the last known value, and then set the remote object's property to this value.
    /// </summary>
    /// <param name="args"></param>
    public Object HandlePropertySet(Object... args)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Remote MostRecent SyncPattern : %1s : %2s", _methodSet.getDeclaringClass().getName(), _methodSet.getName());

        if (_initializing)
            _initialized = true;

        return InterceptCommand.CallBase;
    }
   

    protected void  PropertyChanged()
    {
        if (_initializing)
            return;

        if (_syncingValue)
            return;

        // grab the latest time
        _lastTime = _timeService.getUtcTime();

        // get the value of the underlying object
        Object value;
		try {
			value = _methodGet.invoke(_proxy, null);
		        // inform the other proxies that the property changed
        ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _field, "SyncValue", value, _lastTime);//.ToBinary());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    /// <summary>
    /// Method called when the local object reports a property changed event.
    /// Only assign the value if it was changed after the latest recorded change.
    /// </summary>
    /// <param name="value"></param>
    /// <param name="time"></param>
    public void SyncValue(Object value, long utcTime)
    {
        Date time = new Date(utcTime);// .FromBinary(utcTime);

        if (time.compareTo(_lastTime) > 0)
        {
            if (_logger.IsDebugEnabled())
                _logger.Debug("Remote MostRecent SyncPatten : SyncValue");

            _lastTime = time;

            _syncingValue = true;
            try {
				_methodSet.invoke(_proxy, new Object[] { value });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            _syncingValue = false;
        }
    }

	

	
    
}
