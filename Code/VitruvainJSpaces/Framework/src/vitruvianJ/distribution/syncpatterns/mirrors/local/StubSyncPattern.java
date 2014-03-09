package vitruvianJ.distribution.syncpatterns.mirrors.local;

import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.logging.JLogger;

public class StubSyncPattern extends MirrorSyncPattern
{
    private JLogger _logger = new JLogger(StubSyncPattern.class);

    public Object HandleMethod(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local Stub SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

        return null;
	}

    public Object HandlePropertyGet(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local Stub SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        return null;
	}

    public Object HandlePropertySet(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local Stub SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        return null;
    }
}
