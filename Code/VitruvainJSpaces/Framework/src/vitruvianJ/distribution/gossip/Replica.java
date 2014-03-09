package vitruvianJ.distribution.gossip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import vitruvianJ.collections.SortedList;
import vitruvianJ.communication.IAsyncResult;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Event;
import vitruvianJ.events.IEventSubject;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.ServiceRegistry;

/// <summary>
/// A replica of an object identified by a unique id.
/// </summary>
/// <remarks>The value needs initialized.</remarks>
public class Replica implements IEventSubject
{
    private JLogger _logger = new JLogger(Replica.class);

    private JGUID _id = null;
    private int _index = -1;

    private Object _value = null;
    private VectorTimestamp _valueTimestamp;

    private SortedList _pendingUpdates = new SortedList(new VectorTimestampComparer());
    private VectorTimestamp _replicaTimestamp;

    private Dictionary<JGUID, UpdateItem> _updateHistory = new Hashtable<JGUID, UpdateItem>();
    private VectorTimestamp[] _timestamps = null;

    private List<JGUID> _appliedUpdates = new ArrayList<JGUID>();

    private List<GetRequest> _pendingGets = new ArrayList<GetRequest>();
    private List<GetGossipRequest> _pendingGossipRequests = new ArrayList<GetGossipRequest>();

    private List<Gossip> _pendingGossip = new ArrayList<Gossip>();

    public Event ValueChanged = new Event(this);

    /// <summary>
    /// Construct a replica.
    /// </summary>
    /// <param name="id">The id of this replica.</param>
    /// <param name="size">The size of the timestamp.</param>
    /// <param name="index">The index of the replica in the timestamp.</param>
    /// <param name="initialValue">The initial value of the replica.</param>
    public Replica(JGUID id, int size, int index, Object initialValue)
    {
        _id = id;
        _index = index;
        _timestamps = new VectorTimestamp[size];
        _valueTimestamp = new VectorTimestamp(size);
        _replicaTimestamp = new VectorTimestamp(size);

        for (int i = 0; i < size; i++)
            _timestamps[i] = new VectorTimestamp(size);

        _value = initialValue;
    }

    /// <summary>
    /// The id of this replica.
    /// </summary>
    public JGUID getId()
    {
        return _id; 
    }
    public void setId(JGUID value) { 
    	_id = value; 
    }

    /// <summary>
    /// The index of the replica in the timestamp.
    /// </summary>
    public int getIndex()
    {
        return _index;
    }
    public void setIndex(int value) 
    {
    	_index = value;
    }

    /// <summary>
    /// The value contained by the replica.
    /// </summary>
    public Object getValue()
    {
        return _value; 
    }
    public void setValue(Object value) 
    { 
    	_value = value;
    }

    /// <summary>
    /// The timestamp of the value contained by the replica.
    /// </summary>
    public VectorTimestamp getValueTimestamp()
    {
        return _valueTimestamp; 
    }
    public void  setValueTimestamp(VectorTimestamp value) 
    { 
    	_valueTimestamp = value; 
    }

    /// <summary>
    /// The timestamp that represents all upates that
    /// have been received, but not necessarily applied,
    /// by this replica.
    /// </summary>
    public VectorTimestamp getReplicaTimestamp()
    {
        return _replicaTimestamp; 
    }
    public  void  setReplicaTimestamp(VectorTimestamp value) 
    { 
    	_replicaTimestamp = value;
    }

    /// <summary>
    /// Begin getting the value of this replica.  Getting the value may require
    /// the replica to communicate with other replicas, so it is asynchronous.
    /// </summary>
    /// <param name="timestamp">The timestamp of the replica requesting the value.</param>
    /// <param name="callback">The method to call when the value is retrieved.</param>
    /// <returns></returns>
    public IAsyncResult BeginGet(VectorTimestamp timestamp, Event callback)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Begin Get %1s -> %2s", _id, timestamp);

        // create a get request, and add it to the pending gets
        GetRequest result = new GetRequest(timestamp, callback);
        _pendingGets.add(result);

        // request required gossip from other replication managers
        for (int i = 0; i < timestamp.getSize(); i++)
        {
            if (i == _index)
                continue;

            if (timestamp.getThis(i) > _valueTimestamp.getThis(i) )
            {
                // request gossip
                ReplicationManager manager = GetReplicationManager(i);
                if (manager != null)
                {
                    if (_logger.IsDebugEnabled())
                        _logger.DebugFormat("Request Gossip %1s from %2s", _id, i);

                    manager.RequestGossip(_id, _index, _replicaTimestamp);
                }
            }
        }

        return result;
    }

    /// <summary>
    /// End getting the value from this replica.  This method
    /// will block until the value is received.
    /// </summary>
    /// <param name="result"></param>
    public void EndGet(IAsyncResult result)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("End Get {0} -> {1}", _id);

        ((GetRequest)result).setCompletedSynchronously(false);
        try {
			result.getAsyncWaitHandle().wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//WaitOne();
    }

    /// <summary>
    /// Update the value held by the replica.
    /// </summary>
    /// <param name="updateId">The unique identifier of the update.</param>
    /// <param name="timestamp">The timestamp of the requester.</param>
    /// <param name="update">The update value.</param>
    /// <returns>A new vector timestamp for the update.</returns>
    public VectorTimestamp Update(JGUID updateId, VectorTimestamp timestamp, Object update)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Update {0} -> {1}", _id, timestamp);

        // check if this is an old update if so, then return
        // the timestamp associated with that particular update
        if (_updateHistory.get(updateId) != null)
            return _updateHistory.get(updateId).getNewTimestamp();

        // increment the timestamp of this replica
        // indicating that an update was received
        _replicaTimestamp.getThis(_index++);

        // the resulting timestamp is the original timestamp
        // with the updates received from the requestor
        VectorTimestamp newTimestamp = new VectorTimestamp(timestamp);
        newTimestamp.setThis(_index, _replicaTimestamp.getThis(_index));

        // the update item contains := <i,ts,u.op,u.prev,u.id>
        // - index (i)
        // - update id (u.id)
        // - value (u.op)
        // - result (ts)
        // - timestamp (u.prev)
        UpdateItem item = new UpdateItem(_index, updateId, new VectorTimestamp(timestamp), newTimestamp, update);
        _pendingUpdates.put(timestamp, item);

        _updateHistory.put(updateId, item);
        return newTimestamp;
    }
    
    /// <summary>
    /// Add the request for gossip.
    /// </summary>
    /// <param name="index">The index of the replica requesting gossip.</param>
    /// <param name="timestamp">The timestamp of the replica requesting gossip.</param>
    void RequestGossip(int index, VectorTimestamp timestamp)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Gossip Request {0} from %1s  -> %2s", _id, index, timestamp);

        _pendingGossipRequests.add(new GetGossipRequest(index, timestamp));
    }

    /// <summary>
    /// Add the gossip to this replica.
    /// </summary>
    /// <param name="gossip">The gossip to use.</param>
    void UpdateGossip(Gossip gossip)
    {
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Accept Gossip Update {0} from %1s -> %2s", _id, gossip.getIndex(), gossip.getTimestamp());

        _pendingGossip.add(gossip);
    }

    /// <summary>
    /// Process any pending operations for this replica.
    /// </summary>
    public void Process()
    {
        ProcessGossip();
        ProcessUpdates();
        ProcessGets();
        CleanupHistory();
    }

    /// <summary>
    /// Process gossip requests, and received gossip.
    /// </summary>
    private void ProcessGossip()
    {
        // send out gossip
        for (int i = _pendingGossipRequests.size() - 1; i >= 0; i--)
        {
            GetGossipRequest request = _pendingGossipRequests.get(i);
            _pendingGossipRequests.remove(i);

            // update the timestamp
            _timestamps[request.getIndex()].Merge(request.getTimestamp());

            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Give Gossip Update {0} to {1} -> {2}", _id, request.getIndex(), request.getTimestamp());

            ReplicationManager manager = GetReplicationManager(request.getIndex());
            manager.UpdateGossip(_id, GetGossip(request.getTimestamp()));
        }

        // receive gossip
        for (int i = _pendingGossip.size() - 1; i >= 0; i--)
        {
            Gossip gossip = _pendingGossip.get(i);
            _pendingGossip.remove(i);

            // update the timestamp
            _timestamps[gossip.getIndex()].Merge(gossip.getTimestamp());

            // merge the updates
            MergeUpdates(gossip.getItems());

            // merge the timestamp
            _replicaTimestamp.Merge(gossip.getTimestamp());
        }
    }

    /// <summary>
    /// Merge all of the updates into this replica.
    /// </summary>
    /// <param name="items"></param>
    private void MergeUpdates(List<UpdateItem> items)
    {
        for (UpdateItem item : items)
        {
            if (_updateHistory.get(item.getId()) != null)
            {
                if (VectorTimestamp.greaterThanEqualsOperator(item.getNewTimestamp(),_replicaTimestamp))
                {
                    _pendingUpdates.put(item.getNewTimestamp(), item);
                    _updateHistory.put(item.getId(), item);
                }
            }
        }
    }

    /// <summary>
    /// Get the gossip for the given timestamp.
    /// </summary>
    /// <param name="timestamp"></param>
    /// <returns></returns>
    private Gossip GetGossip(VectorTimestamp timestamp)
    {
        List<UpdateItem> items = new ArrayList<UpdateItem>();
        Enumeration<UpdateItem> updateditems =  _updateHistory.elements();
        while(updateditems.hasMoreElements())
        {
        	UpdateItem updatedItem = updateditems.nextElement();
            if (VectorTimestamp.equalOperator(updatedItem.getOldTimestamp(), timestamp))
                items.add(updatedItem);
        }

        return new Gossip(_index, _replicaTimestamp, items);
    }

    /// <summary>
    /// Process any pending updates.
    /// </summary>
    private void ProcessUpdates()
    {
        // process updates
        Collection<Object> sortedUpdates = _pendingUpdates.values();
        for (int i = 0; i < sortedUpdates.size(); i++)
        {
            UpdateItem item = (UpdateItem)sortedUpdates.iterator();

            // check the stability condition
            if (VectorTimestamp.lessThanEqualsOperator(item.getOldTimestamp(),_valueTimestamp))
            {
                // remove the update from the list
                _pendingUpdates.remove(item.getOldTimestamp());

                // check for redundant updates
                
                if ( _appliedUpdates.contains(item.getId()))
                {
                    // apply the update 
                    ApplyUpdate(item.getUpdate());

                    // merge the update with the value
                    _valueTimestamp.Merge(item.getNewTimestamp());

                    // remember that this update was already applied
                    _appliedUpdates.add(item.getId());
                }
            }
            else
            {
                // none of the other updates will satisfy the
                // stability condition, because they are sorted in
                // less than or equal order.
                break;
            }
        }
    }

    /// <summary>
    /// Apply the update to the value.
    /// </summary>
    /// <param name="update"></param>
    protected void ApplyUpdate(Object update)
    {
        _value = update;

        if (ValueChanged != null)
        	ValueChanged.RaiseEvent();
    }

    /// <summary>
    /// Answer any pending gets.
    /// </summary>
    private void ProcessGets()
    {
        // process gets
        for (int i = _pendingGets.size() - 1; i >= 0; i--)
        {
            GetRequest asyncResult = _pendingGets.get(i);

            // check that this request can be satisfied
            if (VectorTimestamp.lessThanEqualsOperator(asyncResult.getTimestamp(), _valueTimestamp))
            {
                if (_logger.IsDebugEnabled())
                    _logger.DebugFormat("Give Value %1s -> ", _id, asyncResult.getTimestamp());

                // finish the asynchronous operation by setting the state
                asyncResult.setAsyncState(new TimestampedValue(_valueTimestamp, _value));

                // remove the get, because it has been satisfied
                _pendingGets.remove(i);
            }
        }
    }

    /// <summary>
    /// Remove any history items that are not needed anymore.
    /// </summary>
    private void CleanupHistory()
    {
    }

    /// <summary>
    /// Get the replication manager with the given vector timestamp index.
    /// </summary>
    /// <param name="index"></param>
    /// <returns></returns>
    private ReplicationManager GetReplicationManager(int index)
    {
        List<ReplicationManager> managers = (List<ReplicationManager>) ServiceRegistry.GetServices(ReplicationManager.class);

        for (int i = 0; i < managers.size(); i++)
        {
            if (managers.get(i).getIndex() == index)
                return managers.get(i);
        }

        return null;
    }

	@Override
	public EventArgs getEventArgs() {
		// TODO Auto-generated method stub
		return null;
	}
}
