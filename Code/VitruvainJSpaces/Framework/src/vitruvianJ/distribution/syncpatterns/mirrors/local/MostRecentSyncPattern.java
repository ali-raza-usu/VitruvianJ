package vitruvianJ.distribution.syncpatterns.mirrors.local;

import java.util.ArrayList;
import java.util.Date;

import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.syncpatterns.mirrors.PropertySyncPattern;
import vitruvianJ.distribution.time.GlobalTime;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.ServiceRegistry;

public class MostRecentSyncPattern extends PropertySyncPattern
{
    private JLogger _logger = new JLogger(MostRecentSyncPattern.class);

    private GlobalTime _timeService = null;
    @SuppressWarnings("deprecation")
	private Date _lastTime = new Date(0001,01,01);// DateTime.MinValue);

    private boolean _settingValueInternal = false;

    /// <summary>
    /// Start the Sync Pattern.
    /// </summary>
     public void Start()
    {
        super.Start();

        // remember the time service
        _timeService = (GlobalTime) ServiceRegistry.getPreferredService(GlobalTime.class);
    }

    /// <summary>
    /// Method that handles property changed events.  Communicate to the remote proxy
    /// that the local value changed.  Use the time service to get the changed time.
    /// </summary>
     protected void PropertyChanged()
    {
        if (!_settingValueInternal)
        {
            // grab the latest time
            _lastTime = _timeService.getUtcTime();           
            try {
            	 // get the value of the underlying object
                _field.set(getProxyParent(), null);
                Object value = _field.get(getProxyParent());
                // inform the remote Time pattern that the property changed
				ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _field, "ValueChangedRemotely", value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//, _lastTime.ToBinary());
        }
    }

    /// <summary>
    /// Get the value from the parent object.
    /// </summary>
    /// <param name="args"></param>
    /// <returns></returns>
     public Object HandlePropertyGet(Object... args)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local MostRecent SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        
        try {
        	_field.set(getProxyParent(), args);
			return _field.get(getProxyParent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
    }

    /// <summary>
    /// Set the value into the parent object.
    /// </summary>
    /// <param name="args"></param>
     public Object HandlePropertySet(Object... args)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local MostRecent SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        // this might cause the PropertyChanged event to fire
        _settingValueInternal = true;
        Object result = null;
        try {
			_field.set(getProxyParent(), args);
			 result = _field.get(getProxyParent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
       
        _settingValueInternal = false;
        return result;
    }

    /// <summary>
    /// Method called when the remote proxy reports a property changed event.
    /// Only assign the value if it was changed after the latest recorded change.
    /// </summary>
    /// <param name="value"></param>
    /// <param name="time"></param>
    public void ValueChangedRemotely(Object value, long utcTime)
    {
        Date time = new Date(utcTime);// Date.fromBinary(utcTime);

        // check if this is the latest change
        if (time.after(_lastTime) )
        {
            if (_logger.IsDebugEnabled())
                _logger.Debug("Local MostRecent SyncPatten : ValueChangedRemotely");

            // record the time
            _lastTime = time;

            // set the value into the object
            HandlePropertySet(new Object[] { value });
        }
    }
}
