package vitruvianJ.distribution.gossip;

import vitruvianJ.serialization.Serialize;

/// <summary>
/// Object that holds a value, and it's related timestamp.
/// </summary>
public class TimestampedValue
{
    private VectorTimestamp _timestamp;
    private Object _value;

    /// <summary>
    /// COnstruct a timestamped value object.
    /// </summary>
    /// <param name="timestamp">The timestamp related to the value.</param>
    /// <param name="value">The value.</param>
    public TimestampedValue(VectorTimestamp timestamp, Object value)
    {
        _timestamp = timestamp;
        _value = value;
    }

    /// <summary>
    /// The timestamp related to the value.
    /// </summary>
    @Serialize//(getName ="get")
    public VectorTimestamp getTimestamp()
    {
        return _timestamp; 
    }
    
    @Serialize//(getName ="set")
    public void setTimestamp(VectorTimestamp value) { _timestamp = value; }
    

    /// <summary>
    /// The value.
    /// </summary>
    @Serialize//(getName = "get")
    public Object getValue()
    {
        return _value; 
    }
    @Serialize//(getName = "set")
    public void setValue(Object value) 
    {
    	_value = value; 
    }
}
