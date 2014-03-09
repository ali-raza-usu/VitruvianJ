package vitruvianJ.distribution.syncpatterns.mirrors.local;

import java.lang.reflect.InvocationTargetException;

import vitruvianJ.distribution.gossip.FrontEnd;
import vitruvianJ.distribution.gossip.Replica;
import vitruvianJ.distribution.gossip.ReplicationManager;
import vitruvianJ.distribution.syncpatterns.mirrors.PropertySyncPattern;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.ServiceRegistry;

public class GossipSyncPattern extends PropertySyncPattern
{
    private JLogger _logger = new JLogger(GossipSyncPattern.class);
    private FrontEnd _frontEnd = null;
    private Replica _replica = null;

    private boolean _replicaValueChanged = false;

    public GossipSyncPattern()
    {}
    public void Start()
    {
        super.Start();

        try {
	        ReplicationManager manager = (ReplicationManager)ServiceRegistry.getPreferredService(ReplicationManager.class);
	        if (manager == null)
	            throw new Exception("The Gossip pattern requires a replication manager to be in the service registry.");
	
	        // use the proxy id as the replica id
	        JGUID replicaId = _proxy.getProxyId();
	
	        _frontEnd = new FrontEnd(replicaId, manager.getSize());
	        Object initialValue = null;
	        if(_method.getName().contains("get")){
				initialValue = _method.invoke(getProxyParent(), null);
	        }
				_replica = manager.AddReplica(replicaId, initialValue);
				_replica.ValueChanged.addObservers(new ReplicaValueChanged());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

    }

    public void Stop()
    {
        _replica.ValueChanged.removeObservers(new ReplicaValueChanged());
        super.Stop();
    }

    class ReplicaValueChanged implements Delegate
    {

		@Override
		public void invoke(EventArgs args) {
			ReplicaValueChanged(null, args);
			
		}
    	
    }
    
    private void ReplicaValueChanged(Object sender, EventArgs e)
    {
        _replicaValueChanged = true;

        try {
			_field.set(getProxyParent(), new Object[] { _replica.getValue() });
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

        _replicaValueChanged = false;
    }

    public Object HandleMethod(Object... args) 
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local Gossip SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

        //throw new Exception();
        return null;
	}

    public Object HandlePropertyGet(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local Gossip SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        //return _frontEnd.Get();
        return null;
	}

    public Object HandlePropertySet(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local Gossip SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        try {
			_frontEnd.Update(args[args.length - 1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    protected void PropertyChanged()
    {
        if (!_replicaValueChanged)
        {
            try {
				_field.set(getProxyParent(), null);
			
            Object value = _field.get(getProxyParent());
            _frontEnd.Update(value);
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			}
        }
    }
}
