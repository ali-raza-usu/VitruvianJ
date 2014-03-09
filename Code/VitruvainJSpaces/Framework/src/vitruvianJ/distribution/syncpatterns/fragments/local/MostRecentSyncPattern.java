package vitruvianJ.distribution.syncpatterns.fragments.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.syncpatterns.InterceptCommand;
import vitruvianJ.distribution.syncpatterns.fragments.PropertySyncPattern;
import vitruvianJ.distribution.time.GlobalTime;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.ServiceRegistry;

/// <summary>
/// SyncPattern that ties a property to a remote proxy
/// using the value with the latest time.
/// </summary>
public class MostRecentSyncPattern extends PropertySyncPattern
{
    private JLogger _logger = new JLogger(MostRecentSyncPattern.class);

    private GlobalTime _timeService = null;
    private Date _lastTime = new Date(0);
    private boolean _syncingValue = false;

   
    /// <summary>
    /// Start the Sync Pattern.
    /// </summary>
    public void Start()
    {
        super.Start();

        // remember the time service
        _timeService = (GlobalTime)ServiceRegistry.getPreferredService(GlobalTime.class);
    }

   
    public Object HandleMethod(Object... args)
    {
      //  throw new Exception("The method or operation is not implemented.");
    	return null;
    }

    /// <summary>
    /// Get the value from the parent object.
    /// </summary>
    /// <param name="args"></param>
    /// <returns></returns>
    public Object HandlePropertyGet(Object... args)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local MostRecent SyncPattern : %1s : %2s", _methodGet.getDeclaringClass().getName(), _methodGet.getName());

        return InterceptCommand.CallParent;
    }

    /// <summary>
    /// Set the value into the parent object.
    /// </summary>
    /// <param name="args"></param>
    public Object HandlePropertySet(Object... args)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local MostRecent SyncPattern : %1s : %2s", _methodSet.getDeclaringClass().getName(), _methodSet.getName());

        return InterceptCommand.CallParent;
    }

    
    /// <summary>
    /// Method that handles property changed events.  Communicate to the remote proxy
    /// that the local value changed.  Use the time service to get the changed time.
    /// </summary>
    protected void  PropertyChanged()
    {
        if (_syncingValue)
            return;

        // grab the latest time
        _lastTime = _timeService.getUtcTime();

        // get the value of the underlying object
        try {
        Object value = _method.invoke(_proxy, null);

        // inform the other proxies that the property changed
       
			ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _field, "SyncValue", value, _lastTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//.ToBinary());
    }

    /// <summary>
    /// Method called when the remote proxy reports a property changed event.
    /// Only assign the value if it was changed after the latest recorded change.
    /// </summary>
    /// <param name="value"></param>
    /// <param name="time"></param>
    public void SyncValue(Object value, long utcTime)
    {
      //  DateTime time = DateTime.FromBinary(utcTime);
    	Date time = new Date();

        // check if this is the latest change
        if (time.compareTo(_lastTime) > 0)
        {
            if (_logger.IsDebugEnabled())
                _logger.Debug("Local MostRecent SyncPatten : SyncValue");

            // record the time
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
