package vitruvianJ.distribution.gossip;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.IAsyncResult;
import vitruvianJ.core.EventWaitHandle;
import vitruvianJ.events.Event;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;
import vitruvianJ.services.ServiceRegistry;

/// <summary>
/// The front-end of the gossip architecture.
/// </summary>
public class FrontEnd
{
    private JLogger _logger = new JLogger(FrontEnd.class);

    private JGUID _replicaId = null;
    private VectorTimestamp _timestamp;

    private List<IAsyncResult> _asyncGetResults = new ArrayList<IAsyncResult>();
    private EventWaitHandle _asyncGetWait = null;
    private TimestampedValue _asyncGetResult;
    private boolean _receivedAsyncValue = false;

    private Object _syncObject = new Object();

    /// <summary>
    /// Construct a front end.
    /// </summary>
    /// <param name="replicaId">The unique id of this replica.</param>
    /// <param name="size">The size of the vector timestamps.</param>
    public FrontEnd(JGUID replicaId, int size)
    {
        _replicaId = replicaId;
        _timestamp = new VectorTimestamp(size);
        _asyncGetWait = new EventWaitHandle();//false, EventResetMode.AutoReset);
    }

    /// <summary>
    /// The id of the replica.
    /// </summary>
    public JGUID getReplicaId()
    {
        return _replicaId; 
    }

    /// <summary>
    /// Get the value from the replication managers.
    /// </summary>
    /// <returns></returns>
    public Object Get() throws Exception
    {
        // query replication managers, and wait for a single response
        List<ReplicationManager> managers = (List<ReplicationManager>) ServiceRegistry.GetServices(ReplicationManager.class);

        // there can only be a single get in operation for now
        // deadlock?
        //lock (_syncObject)
        {
            // clear the list of any 'get' operations still hanging around
            _asyncGetResults.clear();
            // reset the wait handle
            _asyncGetWait.Set();

            // clear the value
            _receivedAsyncValue = false;

            synchronized (_asyncGetResults)
            {
                // request a get from all managers, and store the result
                for(ReplicationManager manager : managers)
                {
                    IAsyncResult asyncResult = manager.BeginGet(_replicaId, _timestamp, GetCallback(null));
                    _asyncGetResults.add(asyncResult);
                }
            }
            
            // wait for a valid get operation to return, or for the list to go empty
            _asyncGetWait.WaitOne();

            // this happens when the list goes empty when all gets never successfully return
            if (!_receivedAsyncValue)
                throw new Exception("Unable to communicate with Replication Managers.");

            // merge the returned timestamp with _timestamp
            _timestamp.Merge(_asyncGetResult.getTimestamp());

            // return the value
            return _asyncGetResult.getValue();
        }
    }

    /// <summary>
    /// Method called by asynchronous get operation.
    /// </summary>
    /// <param name="result"></param>
    private Event GetCallback(IAsyncResult result)
    {
        // deadlock
        //lock (_asyncGetResults)
        {
            // a response could be really delayed, so
            // check that it comes from a valid request
            if (_asyncGetResults.contains(result))
            {
                // Remove the result from the list, so we
                // know when all of the results are finished.
                _asyncGetResults.remove(result);

                try
                {
                    // getting the state can cause an exception when the
                    // communication with the replication manager timed out
                    if (!_receivedAsyncValue)
                    {
                        _receivedAsyncValue = true;
                        _asyncGetResult = (TimestampedValue)result.getAsyncState();

                        // signal the Get method to continue,
                        // because it has a valid result
                        _asyncGetWait.Set();
                    }
                }
                catch (Exception e)
                { }

                // There aren't any other pending async results.
                // The async-state of all results were bad.
                // Signal the wait, so the Get method exits.
                if (_asyncGetResults.size() == 0 &&  !_receivedAsyncValue)
                    _asyncGetWait.Set();
            }
        }
		return null;
    }

    /// <summary>
    /// Set the value into the replication managers.
    /// </summary>
    /// <param name="value"></param>
    public void Update(Object update) throws Exception
    {
        List<ReplicationManager> managers = (List<ReplicationManager>) ServiceRegistry.GetServices(ReplicationManager.class);

        // send update to a single replication manager,
        // but keep trying them until one of them responds
        for(ReplicationManager manager : managers)
        {
            try
            {
                // update the value in the replication manager
                VectorTimestamp timestamp = manager.Update(_replicaId, new JGUID(), _timestamp, update);

                // merge the returned timestamp with _timestamp
                _timestamp.Merge(timestamp);

                // exit the function, the update was a success
                return;
            }
            catch(Exception e)
            { }
        }

        throw new Exception("Unable to communicate with Replication Managers.");
    }
}
