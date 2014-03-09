package vitruvianJ.distribution.gossip;

import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.Serialize;

/// <summary>
/// An update added to logs.
/// </summary>
public class UpdateItem
{
    private int _index;
    private JGUID _id;
    private VectorTimestamp _newTimestamp;
    private VectorTimestamp _oldTimestamp;
    private Object _update;

    /// <summary>
    /// Construct an update item.
    /// </summary>
    /// <param name="index">The index of the replica.</param>
    /// <param name="id">The id of the update.</param>
    /// <param name="oldTimestamp">The timestamp from the requestor.</param>
    /// <param name="newTimestamp">The timestamp returned to the requestor.</param>
    /// <param name="update">The update value.</param>
    public UpdateItem(int index, JGUID id, VectorTimestamp oldTimestamp, VectorTimestamp newTimestamp, Object update)
    {
        _index = index;
        _id = id;
        _update = update;
        _oldTimestamp = oldTimestamp;
        _newTimestamp = newTimestamp;
    }

    /// <summary>
    /// The index of the replica.
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
    /// The id of the update.
    /// </summary>
    @Serialize//(getName = "get")
    public JGUID getId()
    {
        return _id; 
    }
    
    @Serialize//(getName = "set")
    public void setId(JGUID value) 
    { 
    	_id = value;
    }

    /// <summary>
    /// The update value.
    /// </summary>
    @Serialize//(getName = "get")    
    public Object getUpdate()
    {
        return _update; 
    }
    
    @Serialize//(getName = "set")
    public void setUpdate(Object value) 
    { 
    	_update = value; 
    }

    /// <summary>
    /// The timestamp returned to the requestor.
    /// </summary>
    @Serialize//(getName = "get")
    public VectorTimestamp getNewTimestamp()
    {
        return _newTimestamp; 
    }
    
    @Serialize//(getName = "set") 
    public void setNewTimestamp(VectorTimestamp value) 
    { 
    	_newTimestamp = value;
    }

    /// <summary>
    /// The timestamp from the requestor.
    /// </summary>
    @Serialize //(getName = "get")    
    public VectorTimestamp getOldTimestamp()
    {
        return _oldTimestamp; 
    }
    
    @Serialize //(getName = "set")
    public void setVectorTimestamp(VectorTimestamp value) 
    {
    	_oldTimestamp = value; 
    }
}