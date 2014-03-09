package vitruvianJ.distribution.gossip;

import vitruvianJ.serialization.Serialize;

/// <summary>
/// A vector timestamp.
/// </summary>
public class VectorTimestamp
{
    private int[] _values;

    /// <summary>
    /// Construct a VectorTimestamp.
    /// </summary>
    /// <param name="size">The size of the vector timestamp.</param>
    public VectorTimestamp(int size)
    {
        _values = new int[size];
    }

    /// <summary>
    /// Construct a VectorTimestamp from another VectorTimestamp.
    /// </summary>
    /// <param name="timestamp">The VectorTimestamp to copy.</param>
    public VectorTimestamp(VectorTimestamp timestamp)
    {
        _values = new int[timestamp._values.length];
        _values = CopyTo(timestamp._values, _values);//, 0);
    }
    
    private int[] CopyTo(int[] values1, int[] values2)
    {
    	for(int i=0; i< values1.length; i++)
    	{
    		values2[i] = values1[i];
    	}
    	return values2;
    }

    /// <summary>
    /// Get/Set values into the vectortimestamp based on an index.
    /// </summary>
    /// <param name="index">The index to get/set.</param>
    /// <returns>The value of the timestamp at the given index.</returns>
    public int getThis(int index)
    {
        return _values[index]; 
    }
    
    public void setThis(int index, int value) 
    { 
    	_values[index] = value;
    }

    
    
    /// <summary>
    /// The size of the timestamp.
    /// </summary>
    public int getSize()
    {

            if (_values == null)
                return 0;
            else
                return _values.length;
    }

    /// <summary>
    /// The values contained in the timestamp.
    /// </summary>
    @Serialize//(getName = "get")
    public int[] getValues()
    {
        return _values; 
    }
    
    @Serialize//(getName = "set")
    public void setValues(int[] value) 
    { 
    	_values = value; 
    }
   

    /// <summary>
    /// Merge the given timestamp into this one.
    /// </summary>
    /// <param name="timestamp">The timestamp to merge into this one.</param>
    public void Merge(VectorTimestamp timestamp)
    {
        if (getSize() != timestamp.getSize())
			try {
				throw new Exception("VectorTimestamps must be the same size.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        for (int i = 0; i < _values.length; i++)
        {
            if (timestamp._values[i] > _values[i])
                _values[i] = timestamp._values[i];
        }
    }

    
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        if (_values != null)
        {
            result.append("[");
            for (int i = 0; i < _values.length; i++)
            {
                if (i == 0)
                    result.append( _values[i]);
                else
                    result.append(", "+ _values[i]);
            }
            result.append("]");
        }

        return result.toString();
    }

    /// <summary>
    /// Determine if the two timestamps are equal.
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <returns></returns>
    static public boolean equalOperator(VectorTimestamp x, VectorTimestamp y)
    {
        if (x.getSize() != y.getSize())
			try {
				throw new Exception("VectorTimestamps must be the same size.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        for (int i = 0; i < x._values.length; i++)
        {
            if (x._values[i] != y._values[i])
                return false;
        }

        return true;
    }

    /// <summary>
    /// Determine if the two timestamps are not equal.
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <returns></returns>
    static public boolean notEqualOperator(VectorTimestamp x, VectorTimestamp y)
    {
        if (x.getSize() != y.getSize())
			try {
				throw new Exception("VectorTimestamps must be the same size.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        for (int i = 0; i < x._values.length; i++)
        {
            if (x._values[i] == y._values[i])
                return false;
        }

        return true;
    }

    /// <summary>
    /// Determine if the LHS is greater than or equal to the RHS.
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <returns></returns>
    static public boolean greaterThanEqualsOperator(VectorTimestamp x, VectorTimestamp y)
    {
        if (x.getSize() != y.getSize())
			try {
				throw new Exception("VectorTimestamps must be the same size.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        for (int i = 0; i < x._values.length; i++)
        {
            if (x._values[i] < y._values[i])
                return false;
        }

        return true;
    }

    /// <summary>
    /// Determine if the LHS is less than or equal to the RHS.
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <returns></returns>
    static public boolean lessThanEqualsOperator(VectorTimestamp x, VectorTimestamp y)
    {
        if (x.getSize() != y.getSize())
			try {
				throw new Exception("VectorTimestamps must be the same size.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        for (int i = 0; i < x._values.length; i++)
        {
            if (x._values[i] > y._values[i])
                return false;
        }

        return true;
    }
}
