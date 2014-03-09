package vitruvianJ.distribution;

import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.Hashtable;

import vitruvianJ.serialization.Serialize;
import vitruvianJ.services.BaseService;

abstract public class SyncPatternsService extends BaseService
{
    private Dictionary<String, Type> _localPatterns = new Hashtable<String, Type>();
    private Dictionary<String, Type> _remotePatterns = new Hashtable<String, Type>();

    public SyncPatternsService()
    {
    	super("Sync Patterns Service"); 
    }

    @Serialize//(getName = "get")
    public Dictionary<String, Type> getLocalPatterns()
    {
        return _localPatterns; 
    }
    @Serialize//(getName = "set")
    public void setLocalPatterns(Dictionary<String, Type> value) 
    { 
    	_localPatterns = value;
    }

    @Serialize//(getName = "get")
    public Dictionary<String, Type> getRemotePatterns()
    {
        return _remotePatterns; 
    }
    @Serialize//(getName = "set")
    public void setRemotePatterns(Dictionary<String, Type> value) 
    { 
    	_remotePatterns = value;
    }
}