package vitruvianJ.distribution.syncpatterns.mirrors.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import vitruvianJ.core.SignalSyncThread;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.logging.JGUID;

public class PushSyncPattern extends MirrorSyncPattern
{
    private SignalSyncThread _pushThread = null;
    private Object _lastValue = null;

    private int _pushTime = 1000;

    public PushSyncPattern()
    {
        _pushThread = new SignalSyncThread("PushSyncPattern", new Push());
    }

    public PushSyncPattern(int pushTime)
    {
        _pushTime = pushTime;
        _pushThread = new SignalSyncThread("PushSyncPattern", new Push());
    }

    public void Start()
    {
        super.Start();

        boolean throwException = false;
        if (_method != null)
            throwException = true;
        else if (_method == null)// || _methodGet == null)
            throwException = true;
        else if ( (_method.getParameterTypes()).length > 0)
            throwException = true;

//        if (throwException)
//			try {
//				throw new Exception("PushSyncPattern is only implemented on properties without any arguments.");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}            

        // set the last value to the current value
			try {
				Object obj = getProxyParent();
				if(_method.getName().contains("get"))
					_lastValue = _method.invoke(obj, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
//        _field.set(getProxyParent(), null);
//        _lastValue = _field.get(getProxyParent());
       
        _pushThread.Start(true);
    }

    public void Stop()
    {
        super.Stop();
        try {
			_pushThread.Stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    Method getMethod(Method method, Object obj)
	{
		
		Method[] methods = obj.getClass().getMethods();
		for(Method myMethod : methods)
		{
			if(myMethod.getName().equals(method.getName()) )
				return myMethod;
		}
		return null;
	}
    
    class Push implements Delegate
    {

		@Override
		public void invoke(EventArgs args) {
			Push();
			
		}
    	
    }

    private void Push()
    {
    	Object value = null;
    	try {
    	//_field.set(getProxyParent(), null);
    		if(_method.getName().contains("get")){
    			value = _method.invoke(getProxyParent(), null);                
    		}
        boolean changed = !IsEqual(_lastValue, value);
        
        _lastValue = value;

        if (changed)
			
				ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _field, "RemoteValueChanged", value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        _pushThread.Sleep(_pushTime);
        _pushThread.Signal();
        
    }

    private boolean IsEqual(Object oldValue, Object newValue)
    {
        if (oldValue == null && newValue == null)
            return true;
        else if (oldValue == null || newValue == null)
            return false;
        else
            return oldValue.equals(newValue);
    }

    public Object HandleMethod(Object... args)
    {
       // throw new Exception();
    	return null;
    }

    public Object HandlePropertyGet(Object... args)
    {
    	try {
			_field.set(getProxyParent(), args);
			return _field.get(getProxyParent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        
    }

    private void RemoteValueChanged(Object value)
    {
        HandlePropertySet(value);
    }
}