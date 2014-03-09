package vitruvianJ.distribution.syncpatterns.mirrors.local;

import java.lang.reflect.InvocationTargetException;

import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;

public class PullSyncPattern extends MirrorSyncPattern
{
    public Object HandleMethod(Object... args)
    {
        try {
			return _method.invoke(getProxyParent(), args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

    public Object HandlePropertyGet(Object... args)
    {        
        try {
			_field.set(getProxyParent(), args);
			 return _field.get(getProxyParent());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    public Object HandlePropertySet(Object... args)
    {
    	 try {
 			_field.set(getProxyParent(), args);
 			 return _field.get(getProxyParent());
 		} catch (Exception e) {
 			e.printStackTrace();
 			return null;
 		}
    }

    private void RemoteValueChanged(Object value)
    {
        HandlePropertySet(value);
    }
}