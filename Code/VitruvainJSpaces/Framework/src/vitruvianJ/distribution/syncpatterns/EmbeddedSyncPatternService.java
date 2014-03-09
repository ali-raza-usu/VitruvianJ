package vitruvianJ.distribution.syncpatterns;

import java.lang.reflect.Type;

import vitruvianJ.distribution.SyncPatternsService;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.serialization.xml.XmlFramework;

public class EmbeddedSyncPatternService extends SyncPatternsService
{
    private String _resourcePath = null;
    private Type _baseType = null;

    @Serialize//(getName = "get")
    public Type getBaseType()
    {
        return _baseType; 
    }
    
    @Serialize//(getName = "set")
    public void setBaseType(Type value) 
    { 
    	_baseType = value; 
    }

    @Serialize//(getName = "get")
    public String getResourcePath()
    {
        return _resourcePath;        
    }
    
    @Serialize//(getName = "set")
    public void setResourcePath(String value)
    {
        _resourcePath = value;
    }

    
    public  void Init()
    {
        XmlFramework.Deserialize(_baseType, _resourcePath, this);

        super.Init();
    }
}
