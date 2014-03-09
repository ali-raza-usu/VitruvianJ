package vitruvianJ.distribution.gossip;

import java.util.Comparator;

/// <summary>
/// An object that can compare two vector timestamps.
/// </summary>
public class VectorTimestampComparer implements Comparator
{    
    /// <summary>
    /// Compare the two timestamps.
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <returns></returns>
    public int Compare(Object x, Object y)
    {
        VectorTimestamp vx = (VectorTimestamp)x;
        VectorTimestamp vy = (VectorTimestamp)y;

        int[] vx_val = vx.getValues();
        int[] vy_val = vy.getValues();
        
        int return_val = 0;
        
        for(int i =0; i<vx_val.length; i++)
        {        	
        	if(vx_val[i] < vy_val[i])
        		return_val = -1;
        	else if(vx_val[i] > vy_val[i])
        		return_val = 1;
        	else
        		return_val = 0;
        }        
        return return_val;
    }

	

    
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

    
}
