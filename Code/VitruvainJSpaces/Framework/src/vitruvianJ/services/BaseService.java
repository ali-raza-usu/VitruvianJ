package vitruvianJ.services;

import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.Serialize;

abstract public class BaseService implements IService
{
    private JGUID _id = new JGUID().getJGUID();
    private String _name = "";

    public BaseService(String name)
    {
        _name = name;
    }

    public BaseService(JGUID id, String name)
    {
        _id = id;
        _name = name;
    }

    

    //[SyncPattern("Constant")]
    //[JGUIDFormatter]
    //[Serialize]
    public JGUID getId()
    {
        return _id;
    }
    
    
    public void setId(JGUID id)
    {
        _id = id;
    }

    //[SyncPattern("Constant")]
	@Serialize
    public String getName()
    {
        return _name;
    }
    
    public void setName(String name)
    {
        _name = name;
    }

    public void Init()
    { }

    public void Cleanup()
    { }
    
}
