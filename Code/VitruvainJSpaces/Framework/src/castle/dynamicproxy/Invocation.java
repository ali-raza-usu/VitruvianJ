package castle.dynamicproxy;

import java.lang.reflect.*;

public class Invocation
{
    private boolean _handled = false;
    private ProxyManager _manager = null;
    private Method _method = null;
    private Object[] _args = null;
    private Object _returnValue = null;
    private int curVal = 0;

    public Invocation(ProxyManager manager, Method method, Object... args)
    {
        _manager = manager;
        _method = method;
        _args = args;
    }

    public boolean getHandled()
    {
        return _handled; 
    }
    
    public void setHandled(boolean value) { 
    	_handled = value; 
    }

    /// <summary>
    /// Holds the intercepted method.
    /// </summary>
    public Method getMethod()
    {
        return _method; 
    }
    
    /// <summary>
    /// Holds the arguments to be passed to the methods.
    /// </summary>
    public Object[] getArgs()
    {
        return _args;
    }

    /// <summary>
    /// Holds the return value.
    /// </summary>
    public Object getReturnValue()
    {
    	return _returnValue; 
    }
    public void setReturnValue(Object value) 
    {
    	_returnValue = value;
    }
    
    public void proceed()
    {
    	while (_manager.getInterceptors().size() > curVal)
        {
            boolean result = _manager.getInterceptors().get(curVal++).Intercept(this);
            if (result)
            {
                this.setHandled(true);
                break;
            }
        }
    	
    }
}
