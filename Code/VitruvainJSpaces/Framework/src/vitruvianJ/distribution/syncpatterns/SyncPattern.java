package vitruvianJ.distribution.syncpatterns;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.ISyncPattern;


abstract public class SyncPattern implements ISyncPattern
{
    protected ISyncProxy _proxy = null;

    protected Method _method = null;
    protected Field _field = null;
    protected Method _methodGet = null;
    protected Method _methodSet = null;

    protected  boolean _initializing = true;

    
    public void Init(ISyncProxy proxy, Method method, Field field)
	{
        _proxy = proxy;
        _method = method;
        _field = field;
//        if (_field != null)
//        {
//            _propertyGet = field.GetGetMethod();
//            _propertySet = field.GetSetMethod();
//        }
	}

    public void Start()
	{
        _initializing = false;
    }

	public void Stop()
	{
        _initializing = true;
    }

    public ISyncProxy getProxy()
    {
        return _proxy;
    }

    public boolean getIsLocal()
    {
        return _proxy instanceof ILocalSyncProxy;
    }

    protected Object getProxyParent()
    {
        return ((ILocalSyncProxy)_proxy).getProxyParent();
    }

    protected Type getNonProxyBaseType()
    {
        return ProxyUtilities.getNonProxyBaseType(_proxy.getClass());
    }

    abstract public Object HandleMethod(Object... args);

    abstract public Object HandlePropertyGet(Object... args);

    abstract public Object HandlePropertySet(Object... args);
}