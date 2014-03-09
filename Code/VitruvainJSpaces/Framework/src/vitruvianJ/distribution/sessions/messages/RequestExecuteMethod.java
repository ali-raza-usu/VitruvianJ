package vitruvianJ.distribution.sessions.messages;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vitruvianJ.communication.session.RequestMessage;
import vitruvianJ.distribution.proxies.ProxyUtilities;
import vitruvianJ.distribution.proxies.ReflectionInfo;
import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.OptimisticSerialization;

@OptimisticSerialization

 public class RequestExecuteMethod extends RequestMessage
	{
		private Object _value = null;
		private String _methodName = "";
		private int _methodIndex = -1;
		private Object[] _args = null;
		private boolean _asynchronous = false;
		private boolean _updateArgs = false;

		private boolean _broadcast = false;
		private List<JGUID> _excludeList = new ArrayList<JGUID>();

		public RequestExecuteMethod()
		{
			super(MessageIds.REQUEST_EXECUTE_METHOD);
		}

		public RequestExecuteMethod(Object value, Method mInfo, Object... args)
		{
			super(MessageIds.REQUEST_EXECUTE_METHOD);
			_value = value;
			_methodName = mInfo.getName();
			_methodIndex = ReflectionInfo.getReflectionInfo(_value.getClass()).GetMemberIndex(mInfo);
			_args = args;
		}

	     public RequestExecuteMethod(Object value, Method mInfo, boolean updateArgs, Object... args)
	     {
	    	 super(MessageIds.REQUEST_EXECUTE_METHOD);
	         _value = value;
	         _methodName = mInfo.getName();
	         _methodIndex = ReflectionInfo.getReflectionInfo(_value.getClass()).GetMemberIndex(mInfo);
	
	         _updateArgs = updateArgs;
	         _args = args;
	     }

     public boolean getAsynchronous()
     {
         return _asynchronous; 
     }
     public void setAsynchronous(boolean value) 
     { 
    	 _asynchronous = value; 
     }

     public boolean getBroadcast()
     {
         return _broadcast; 
     }
         
     public void setBroadcast(boolean value) 
     { 
    	 _broadcast = value; 
     }

     public List<JGUID> getExcludeList()
     {
         return _excludeList; 
     }
     public void setExcludeList(List<JGUID> value) 
     { 
    	 _excludeList = value;
     }

		public Object getValue()
		{
			return _value; 
		}
		public	void setValue(Object value) 
		{ 
			_value = value; 
		}

		public String getMethodName()
		{
			return _methodName; 
		}
		public	void setMethodName(String value) 
		{ 
			_methodName = value; 
		}

     public int getMethodIndex()
     {
         return _methodIndex; 
     }
     public void setMethodIndex(int value) 
     { 
    	 _methodIndex = value; 
     }

     public boolean getUpdateArgs()
     {
         return _updateArgs; 
     }
     public void  setUpdateArgs(boolean value) 
     { 
    	 _updateArgs = value; 
     }

		public Object[] getArgs()
		{
			return _args; 
		}
		public	void setArgs(Object[] value) 
		{ 
			_args = value;
		}

		
     public String toString()
     {
         if (_value != null)
         {
             Type baseType = ProxyUtilities.getNonProxyBaseType(_value.getClass());
             return String.format("Request Execute Method : MessageId = %1s : Method = %2s.%3s", getMessageId(), baseType.getClass().getName(), _methodName);
         }
         else
         {
             return String.format("Request Execute Method : MessageId = %1s : Error : Unknown Object", getMessageId());
         }
     }
	}
