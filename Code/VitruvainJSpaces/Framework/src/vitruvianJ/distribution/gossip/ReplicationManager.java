package vitruvianJ.distribution.gossip;

import java.lang.reflect.Member;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import vitruvianJ.communication.IAsyncResult;
import vitruvianJ.core.SignalSyncThread;
import vitruvianJ.distribution.DistributionInfoAttribute;
import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.distribution.proxies.ISyncProxy;
import vitruvianJ.distribution.syncpatterns.SyncPatternAttribute;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.serialization.Serialize;

/// <summary>
/// An implementation of a replication manager as explained in
/// Distributed Systems : Concepts and Design.  This manager can
/// hold many replicas that are distinguished by a unique id.
/// </summary>
@DistributionInfoAttribute(getMigratable = false)
public class ReplicationManager implements vitruvianJ.services.IService
{
    private JLogger _logger = new JLogger(ReplicationManager.class);

	private JGUID _id = new JGUID();
	private String _name = "ReplicationManager";

    private int _index = -1;
    private int _size = -1;

    private Dictionary<JGUID, Replica> _replicas = new Hashtable<JGUID, Replica>();

    private SignalSyncThread _processThread = null;

    /// <summary>
    /// Construct a replication manager service.
    /// </summary>
    public ReplicationManager()
    {
        _processThread = new SignalSyncThread("Replication Manager : ProcessThread", new Process());
    }

	

	/// <summary>
	/// The unique identifier of the service.
	/// </summary>
    //[JGUIDFormatter]
    @Serialize//(getName = "get")
    public JGUID getId()
	{
		return _id; 
	}
	
    @Serialize//(getName = "set")
    public void setId(JGUID value) { 
    	_id = value; 
   }
	

	/// <summary>
	/// The name of the service.
	/// </summary>
   // [Serialize]
    @Serialize//(getName = "get")
    public String getName()
	{
    	return _name; 
	}
    
    @Serialize//(getName = "set")
	public void	setName(String value) 
	{ 
		_name = value;
	}

	/// <summary>
	/// Initialize the service.
	/// </summary>
    //[SyncPattern("Stub")]
	@SyncPatternAttribute(getSyncPatternId = "Stub")
	public void Init()
	{
        // start the processing thread
        _processThread.Start(false);
	}

	/// <summary>
	/// Cleanup the service.
	/// </summary>
    //[SyncPattern("Stub")]
	@SyncPatternAttribute(getSyncPatternId = "Stub")
    public void Cleanup()
	{
        // stop the processing thread
        try {
			_processThread.Stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

    /// <summary>
    /// The index into the vector timestamp.
    /// </summary>
    @Serialize//(getName = "get")
    public int getIndex()
    {
    	return _index; 
    }
    @Serialize//(getName = "set")
    public void  setIndex(int value) { 
    	_index = value;
    }

    /// <summary>
    /// The size of the vector timestamp.
    /// </summary>
    @Serialize//(getName = "get")
    public int getSize()
    {
        return _size; 
    }
    @Serialize//(getName = "set")
    public void setSize(int value) 
    {
    	_size = value;
    }

    /// <summary>
    /// Add a replica to the ReplicationManager
    /// </summary>
    /// <param name="replicaId"></param>
    /// <param name="initialValue"></param>
    /// <returns></returns>
    public Replica AddReplica(JGUID replicaId, Object initialValue)
    {
        if (_replicas.get(replicaId) == null)
        {
            Replica replica = new Replica(replicaId, _size, _index, initialValue);
            _replicas.put(replicaId, replica);

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Added replica %1s ", replicaId);
        }

        return _replicas.get(replicaId);
    }

    /// <summary>
    /// Begin getting the value held by the replica with the given id.
    /// </summary>
    /// <param name="replicaId">The unique identifier of the replica.</param>
    /// <param name="timestamp">The timestamp held by the requestor.</param>
    /// <param name="callback">The method to call when the result returns.</param>
    /// <returns>An object that uniquely identifies the asynchronous call.</returns>
    //[SyncPattern("ARPC")]
    @SyncPatternAttribute(getSyncPatternId = "ARPC")
    public IAsyncResult BeginGet(JGUID replicaId, VectorTimestamp timestamp, Event callback)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Begin Get {0} -> {1}", replicaId, timestamp);

        Replica replica = _replicas.get(replicaId);
        IAsyncResult result = replica.BeginGet(timestamp, callback);
        _processThread.Signal();
        return result;
    }

    /// <summary>
    /// Block the thread, until the value held by the replica with the given id is returned.
    /// </summary>
    /// <param name="replicaId">The unique identifier of the replica.</param>
    /// <param name="asyncResult">An object that uniquely identifies the asynchronous call.</param>
    //[SyncPattern("ARPC")]
    @SyncPatternAttribute(getSyncPatternId = "ARPC")
    public void EndGet(IAsyncResult asyncResult, JGUID replicaId)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Begin Get {0}", replicaId);

        Replica replica = _replicas.get(replicaId);
        replica.EndGet(asyncResult);
    }

    /// <summary>
    /// Update the replica.
    /// </summary>
    /// <param name="replicaId">The unique id of the replica.</param>
    /// <param name="updateId">The unique id of the update.</param>
    /// <param name="timestamp">The timestamp of requester.</param>
    /// <param name="update">The update value.</param>
    /// <returns></returns>
    //[SyncPattern("RPC")]
    @SyncPatternAttribute(getSyncPatternId = "RPC")
    public VectorTimestamp Update(JGUID replicaId, JGUID updateId, VectorTimestamp timestamp, Object update)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Begin Update {0} -> {1}", replicaId, timestamp);

        Replica replica = _replicas.get(replicaId);
        VectorTimestamp result = replica.Update(updateId, timestamp, update);
        _processThread.Signal();
        return result;
    }

    /// <summary>
    /// Begin getting the gossip held by the replica with the given id.
    /// </summary>
    /// <param name="replicaId">The unique identifier of the replica.</param>
    /// <param name="index">The index of the requesting replication manager.</param>
    /// <param name="timestamp">The timestamp held by the requestor.</param>
   // [SyncPattern("RPC")]
    /// this should be internal
    @SyncPatternAttribute(getSyncPatternId = "RPC")
    public void RequestGossip(JGUID replicaId, int index, VectorTimestamp timestamp)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Request Gossip %1s from %2s -> %3s", replicaId, index, timestamp);

        Replica replica = _replicas.get(replicaId);
        replica.RequestGossip(index, timestamp);
        _processThread.Signal();
    }

    /// <summary>
    /// Update the replica.
    /// </summary>
    /// <param name="replicaId">The unique id of the replica.</param>
    /// <param name="timestamp">The timestamp of requester.</param>
    /// <param name="update">The update value.</param>
    @SyncPatternAttribute(getSyncPatternId = "RPC")
    /// this should be internal
    public void UpdateGossip(JGUID replicaId, Gossip gossip)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Update Gossip %1s from %2s -> %3s", replicaId , gossip.getIndex(), gossip.getTimestamp());

        Replica replica = _replicas.get(replicaId);
        replica.UpdateGossip(gossip);
        _processThread.Signal();
    }

    /// <summary>
    /// Give each replica a chance to process gets and updates.
    /// </summary>
    
    class Process implements Delegate
    {

		@Override
		public void invoke(EventArgs args) {
			process();
			
		}
    	
    }
    private void process()
    {
    	Enumeration<Replica> values = _replicas.elements();
    	while(values.hasMoreElements())
    	{
    		Replica replica = values.nextElement();        
           replica.Process();
    	}
    }



	
}