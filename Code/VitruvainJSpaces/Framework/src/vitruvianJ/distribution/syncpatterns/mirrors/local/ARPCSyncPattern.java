package vitruvianJ.distribution.syncpatterns.mirrors.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.IAsyncResult;
import vitruvianJ.core.EventWaitHandle;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.WaitHandle;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;

public class ARPCSyncPattern extends MirrorSyncPattern
{
    private JLogger _logger = new JLogger(ARPCSyncPattern.class);
    private List<EventWaitHandle> _pendingWaitHandles = new ArrayList<EventWaitHandle>();

    public void Start()
    {
        super.Start();

        if (!_method.getName().startsWith("Begin") && !_method.getName().startsWith("End"))
        {
            try {
				throw new RemoteException("Signature of method must be either; IAsyncResult Begin[MethodName](1 .. * params, IAsyncCallback); or IAsyncResult Begin[MethodName](1 .. * params); or void End[MethodName](IAsyncResult);");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public void Stop()
    {
        super.Stop();

        synchronized (_pendingWaitHandles)
        {
            for (EventWaitHandle waitHandle : _pendingWaitHandles)
            {
                // does this call set?
                waitHandle.Set();//Close();
            }
        }
    }

    /// <summary>
    /// Handle the method given to the sync pattern.
    /// </summary>
    /// <param name="args"></param>
    /// <returns></returns>
    public Object HandleMethod(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local ARPC SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

        try {
			return _method.invoke(getProxyParent(), args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

    /// <summary>
    /// Handle the property_get method given to the sync pattern.
    /// </summary>
    /// <param name="args"></param>
    /// <returns></returns>
    public Object HandlePropertyGet(Object... args)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local ARPC SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        // it is asynchronous
        return null;
	}

    /// <summary>
    /// Handle the property_set method given to the sync pattern.
    /// </summary>
    /// <param name="args"></param>
    /// <returns></returns>
    public Object HandlePropertySet(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local ARPC SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        // it is asynchronous
        return null;
	}

    /// <summary>
    /// Function called by the remote ARPC pattern.
    /// </summary>
    /// <param name="id"> The id is used to uniquely identify the async
    /// result that is pending on the other side.</param>
    /// <param name="args"></param>
    public void ExecuteAsync(JGUID brokerId, JGUID id, Object... args)
    {
        IAsyncResult result = (IAsyncResult)HandleMethod(args);
        EventWaitHandle waitHandle = result.getAsyncWaitHandle();// null;//result.getAsyncWaitHandle();

        synchronized (_pendingWaitHandles)
        {
            _pendingWaitHandles.add(waitHandle);
        }

        waitHandle.WaitOne();

        synchronized (_pendingWaitHandles)
        {
            _pendingWaitHandles.remove(waitHandle);
        }

        try {
			ObjectBroker.ExecuteRemoteSyncPatternMethod(brokerId, _proxy, _method, "AsyncExecuted", id, result.getAsyncState());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	

	
}
