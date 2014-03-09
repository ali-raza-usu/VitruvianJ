package vitruvianJ.distribution.syncpatterns.common.local;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.util.ArrayList;

import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.syncpatterns.InterceptCommand;
import vitruvianJ.distribution.syncpatterns.SyncPattern;
import vitruvianJ.distribution.syncpatterns.fragments.INotifyMethodInvoked;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;

public class BroadcastRPCSyncPattern extends SyncPattern
{
    private JLogger _logger = new JLogger(BroadcastRPCSyncPattern.class);

    private String _methodName = null;
    private MethodInvoked methodInvoked = new MethodInvoked();

    public void Start()
    {
        super.Start();

        try{
        if (_method == null)
            throw new RemoteException("Broadcast RPC SyncPatterns can only be assigned to methods.");

        // check that the object is an INotifyMethodInvoked object
        INotifyMethodInvoked parent = (INotifyMethodInvoked)super.getProxy();
        if (parent == null)
        {
            Type type = super.getNonProxyBaseType();
            throw new RemoteException("The type "+type.getClass().getCanonicalName()+" must implement INotifyMethodInvoked to use Broadcast RPC SyncPatterns.");
        }

        }catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        _methodName = _method.getName();

        // start listening to MethodInvoked events
        AddEvents();
    }

    /// <summary>
    /// Stop the sync pattern.
    /// </summary>
    public void Stop()
    {
        super.Stop();

        // stop listening to MethodInvoked events
        RemoveEvents();
    }

    /// <summary>
    /// Start listening to MethodInvoked events
    /// </summary>
    protected void AddEvents()
    {
        INotifyMethodInvoked parent = (INotifyMethodInvoked)super.getProxyParent();
        if (parent != null)
            parent.getMethodInvoked().addObservers(methodInvoked);
    }

    /// <summary>
    /// Stop watching for MethodInvoked events
    /// </summary>
    protected void RemoveEvents()
    {
        INotifyMethodInvoked parent = (INotifyMethodInvoked)super.getProxyParent();
        if (parent != null)
            parent.getMethodInvoked().removeObservers(methodInvoked);
    }

     class MethodInvoked implements Delegate
    {

		@Override
		public void invoke(EventArgs args) {
			MethodInvoked(null, args);//Clear it later that what would be the args
			
		}
    	
    }
    protected void MethodInvoked(String name, Object... args)
    {
        // check if this is the method of interest
        if (name == _methodName)
        {
            // inform remote patterns that the method was called
            try {
				ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _method, "InvokeMethod", args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	public Object HandleMethod(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local Broadcast RPC SyncPattern : %1s : %2s", _method.getClass().getClass().getName(), _method.getName());

        return InterceptCommand.CallParent;
	}

    public Object HandlePropertyGet(Object... args)
    {
        //throw new Exception();
    	return null;
	}

    public Object HandlePropertySet(Object... args)
	{
      //  throw new Exception();
    	return null;
	}

    public void InvokeMethod(Object[] args)
    {
        RemoveEvents();
        HandleMethod(args);
        AddEvents();
    }

	
}
