package vitruvianJ.distribution.gossip;

import java.util.List;

import vitruvianJ.serialization.Serialize;

public class Gossip
{
    private int _index;
    private VectorTimestamp _timestamp;
    private List<UpdateItem> _items;

    /// <summary>
    /// Construct a gossip object.
    /// </summary>
    /// <param name="index">The index of the replication manager creating the gossip.</param>
    /// <param name="timestamp">The timestamp of the replication manager creating the gossip.</param>
    /// <param name="items">The gossip items.</param>
    public Gossip(int index, VectorTimestamp timestamp, List<UpdateItem> items)
    {
        _index = index;
        _timestamp = timestamp;
        _items = items;
    }

    /// <summary>
    /// The index of the replication manager creating the gossip.
    /// </summary>
    @Serialize//(getName = "get")
    public int getIndex()
    {
        return _index; 
    }
    
    @Serialize//(getName = "set")
    public void setIndex(int value) 
    { 
    	_index = value;
    }

    /// <summary>
    /// The timestamp of the replication manager creating the gossip.
    /// </summary>
    @Serialize//(getName = "get")
    public VectorTimestamp getTimestamp()
    {
        return _timestamp; 
    }
    
    @Serialize//(getName = "set")
    public void setTimestamp(VectorTimestamp value) { _timestamp = value; }
    

    /// <summary>
    /// The gossip items.
    /// </summary>
    @Serialize//(getName = "get")
    public List<UpdateItem> getItems()
    {
        return _items; 
    }
    
    @Serialize//(getName = "set")
    public void setItems(List<UpdateItem> value) { _items = value; }
    
}
