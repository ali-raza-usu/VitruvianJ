package vitruvianJ.distribution.time;

import java.util.Date;

import vitruvianJ.logging.JGUID;
import vitruvianJ.services.*;

abstract public class GlobalTime implements IService
{
    /// <summary>
    /// Is the time valid?
    /// </summary>
    abstract public boolean isTimeValid();
    

    /// <summary>
    /// Get the UTCTime
    /// </summary>
  //  [SyncPattern("RPC")]
    abstract public Date getUtcTime();
    
    
    private JGUID _id = new JGUID();

    public JGUID getId()
    {
        return _id; 
    }

    public String getName()
    {
        return "Global Time";
    }

    public void Init()
    {
    }

    public void Cleanup()
    {
    }

    
}
