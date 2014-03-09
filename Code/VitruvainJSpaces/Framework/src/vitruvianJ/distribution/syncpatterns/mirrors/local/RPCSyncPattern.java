package vitruvianJ.distribution.syncpatterns.mirrors.local;

import java.lang.reflect.InvocationTargetException;

import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.logging.JLogger;

public class RPCSyncPattern extends MirrorSyncPattern
{
    private JLogger _logger = new JLogger(RPCSyncPattern.class);

    public Object HandleMethod(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local RPC SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

        try {
			return _method.invoke(getProxyParent(), args);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    public Object HandlePropertyGet(Object... args)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local RPC SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        try {
			_field.set(getProxyParent(), args);
			 return _field.get(getProxyParent());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
       
	}

    public Object HandlePropertySet(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local RPC SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        try {
			_field.set(getProxyParent(), args);
			 return _field.get(getProxyParent());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
