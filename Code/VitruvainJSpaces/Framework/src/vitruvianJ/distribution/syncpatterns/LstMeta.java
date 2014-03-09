package vitruvianJ.distribution.syncpatterns;

import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.Serialize;

public class LstMeta
{
    private LstAction _action;
    private Object _item;
    private JGUID _syncId;

    
    public LstMeta()
    {
    	
    }
    
    public LstMeta(JGUID syncId, LstAction action, Object item)
    {
        _syncId = syncId;
        _action = action;
        _item = item;
    }

    @Serialize//(getName = "get")
    public JGUID getId()
    {
        return _syncId; 
    }
    
    @Serialize//(getName = "set")
    public void setId(JGUID value) 
    {
    	_syncId = value; 
    }

    @Serialize //(getName = "get")
    public LstAction getAction()
    {
        return _action; 
    }
        
    @Serialize //(getName = "set")
    public void setAction(LstAction value) 
    {
    	_action = value; 
    }

    @Serialize //(getName = "get")
    public Object getItem()
    {
        return _item; 
    }
    
    @Serialize //(getName = "set")
    
    public void setItem(Object value)
    { 
    	_item = value; 
    }
}
