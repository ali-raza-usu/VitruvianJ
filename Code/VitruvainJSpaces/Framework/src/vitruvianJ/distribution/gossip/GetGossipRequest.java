package vitruvianJ.distribution.gossip;

/// <summary>
/// Object used by replicas to track get gossip requests.
/// </summary>
public class GetGossipRequest
{
    private int _index;
    private VectorTimestamp _timestamp;

    /// <summary>
    /// Construct a get gossip request.
    /// </summary>
    /// <param name="index">The index of the replication manager making the request.</param>
    /// <param name="timestamp">The timestamp of the replication manager making the request.</param>
    public GetGossipRequest(int index, VectorTimestamp timestamp)
    {
        _index = index;
        _timestamp = timestamp;
    }

    /// <summary>
    /// The index of the replication manager making the request.
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
    /// The timestamp of the replication manager making the request.
    /// </summary>
    public VectorTimestamp getTimestamp()
    {
        return _timestamp; 
    }
    public void  setTimestamp(VectorTimestamp value) 
    { 
    	_timestamp = value; 
    }
}
