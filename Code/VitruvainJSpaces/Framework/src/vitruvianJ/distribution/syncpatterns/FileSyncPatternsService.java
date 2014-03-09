package vitruvianJ.distribution.syncpatterns;

import vitruvianJ.distribution.SyncPatternsService;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.serialization.xml.XmlFramework;

public class FileSyncPatternsService extends SyncPatternsService
{
    private String _filePath = null;

    @Serialize //(getName = "get")
   // [AppPathFormatter]
    public String getFilePath()
    {
        return _filePath; 
    }
        
    @Serialize//(getName = "set")
    public void setFilePath(String value) 
    { 
    	_filePath = value;
    }

    public void Init()
    {
        XmlFramework.Deserialize(_filePath, this);

        super.Init();
    }
}
