package vitruvianJ.distribution.gossip;

import vitruvianJ.communication.AsyncResult;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;

class GetRequest extends AsyncResult
{
    private VectorTimestamp _timestamp;

    /// <summary>
    /// Construct the get request.
    /// </summary>
    /// <param name="index"></param>
    /// <param name="timestamp"></param>
    /// <param name="callback"></param>
    public GetRequest(VectorTimestamp timestamp, Event callback)
    {
    	super(callback);
        _timestamp = timestamp;
    }

    /// <summary>
    /// The timestamp of the requestor.
    /// </summary>
    public VectorTimestamp getTimestamp()
    {
        return _timestamp; 
    }
    
    public void setTimestamp(VectorTimestamp value) 
    { 
    	_timestamp = value;
    }
}
