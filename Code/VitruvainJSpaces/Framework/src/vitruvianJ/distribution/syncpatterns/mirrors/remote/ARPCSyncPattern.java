package vitruvianJ.distribution.syncpatterns.mirrors.remote;

import java.lang.reflect.*;
import vitruvianJ.logging.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import vitruvianJ.communication.*;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;


    /// <summary>
    /// An asynchronous remote-procedure call sync pattern.
    /// This requires a method of the form 
    /// IAsyncResult BeginMethod(someParam, ...);
    /// or
    /// IAsyncResult BeginMethod(someParam, ..., IAsyncCallback);
    /// or
    /// void EndMethod(IAsyncResult);
    /// or
    /// void EndMethod(IAsyncResult, int);
    /// or
    /// void EndMethod(IAsyncResult, TimeSpan);
    /// </summary>
	public class ARPCSyncPattern extends MirrorSyncPattern
	{
        private JLogger _logger = new JLogger(ARPCSyncPattern.class);
        private HashMap<JGUID, AsyncResult> _pendingResults = new HashMap<JGUID, AsyncResult>();

        public void Start()
        {
            super.Start();

            boolean throwException = false;

            if (_method != null)
            {
                if (_method.getName().startsWith("Begin"))
                {
                    if (!IAsyncResult.class.isAssignableFrom(_method.getReturnType()))
                        throwException = true;
                }
                else if (_method.getName().startsWith("End"))
                {
                    if (!_method.getReturnType().equals(void.class))
                        throwException = true;
                    else if (_method.getParameterTypes().length < 1)
                        throwException = true;
                    else if (!IAsyncResult.class.isAssignableFrom(_method.getParameterTypes()[0].getClass()))//ParameterType))
                        throwException = true;
                }
                else
                {
                    throwException = true;
                }
            }
            else
            {
                throwException = true;
            }

            if (throwException)
				try {
		//			throw new Exception("Signature of method must be either; IAsyncResult Begin[MethodName](1 .. * params, IAsyncCallback); or IAsyncResult Begin[MethodName](1 .. * params); or void End[MethodName](IAsyncResult);");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }

        public void Stop()
        {
            super.Stop();

            _pendingResults.clear();
        }

        /// <summary>
        /// Handle the method given to the sync pattern.
        /// </summary>
        /// <param name="args"></param>
        /// <returns></returns>
		public Object HandleMethod(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote ARPC SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (_method.getName().startsWith("Begin"))
            {
                //AsyncCallback 
                Event callback = null;

                // the callback must be arg[n-1], given n args, if it exists
                if (HasCallback(args))
                {
                    //callback = (AsyncCallback)args[args.length - 1];
                	callback = (Event)args[args.length - 1];

                    // set it to null, so that it isn't serialized
                    args[args.length - 1] = null;
                }

                // create a new JGUID that uniquely identifies this interaction
                JGUID resultId = new JGUID();
                AsyncResult result = new AsyncResult(callback);
                
                // remember the AsyncResult associated with this id
                _pendingResults.put(resultId, result);

                // call the ExecuteAsync method on the local ARPC pattern
                IRemoteSyncProxy _remoteProxy = (IRemoteSyncProxy)_proxy;
                JGUID brokerId = new JGUID(_remoteProxy.getPreferredBroker());//.getJGUID();
                try {
					ObjectBroker.BeginExecuteRemoteSyncPatternMethod(brokerId, _proxy, _method, "ExecuteAsync", ObjectBroker.getId(), resultId, args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                // return the AsyncResult
                return result;
            }
            else if (_method.getName().startsWith("End"))
            {
                // the AsyncResult must be arg[0]
                IAsyncResult result = (IAsyncResult)args[0];
                try{  
                // wait for the protocol to finish
                if( (args.length == 2) && (args[1] instanceof Integer))// int) )
                {
                	long val = Integer.parseInt(args[1].toString());
                    result.getAsyncWaitHandle().WaitOne(val); //   WaitOne((int)args[1]);
                }
                else if (args.length == 2 && args[1] instanceof  Timestamp)//  TimeSpan)
                    result.getAsyncWaitHandle().WaitOne(((Timestamp)args[1]).getTime() );//((TimeSpan)args[1]);//  WaitOne((TimeSpan)args[1]);
                else
                    result.getAsyncWaitHandle().WaitOne();//.WaitOne();
                }catch(Exception ex)
                {ex.printStackTrace();}
                // the return is void
                return null;
            }
            else
            {
            	try{
            		throw new Exception("Signature of method must be either; IAsyncResult Begin[MethodName](1 .. * params, IAsyncCallback); or IAsyncResult Begin[MethodName](1 .. * params); or void End[MethodName](IAsyncResult);");
                	//return null;
            	}catch(Exception e){
            		return null;
            	}
            }
		}

        private boolean HasCallback(Object... args)
        {
            if (args == null)
                return false;
            else if (args.length == 0)
                return false;
            else
                return args[args.length - 1] instanceof Event;// AsyncCallback;
        }

        /// <summary>
        /// Handle the property_get method given to the sync pattern.
        /// </summary>
        /// <param name="args"></param>
        /// <returns></returns>
        public Object HandlePropertyGet(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote ARPC SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            // it is asynchronous
           // throw new Exception();
            return null;
		}

        /// <summary>
        /// Handle the property_set method given to the sync pattern.
        /// </summary>
        /// <param name="args"></param>
        /// <returns></returns>
        public Object HandlePropertySet(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Remote ARPC SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            // it is asynchronous
           // throw new Exception();
            return null;
		}

        /// <summary>
        /// Function called by the local ARPC pattern.
        /// </summary>
        /// <param name="id"></param>
        /// <param name="asyncState"></param>
        public void AsyncExecuted(JGUID id, Object asyncState)
        {
        	_logger.Debug("AsyncExecuted(Guid id, object asyncState)"); 
            // the pending results should always contain this key, but check just in case
        	for (Entry<JGUID, AsyncResult> element : _pendingResults.entrySet()) {
				JGUID key = element.getKey();
			
            if (key.toString().equals(id.toString()) )
            {
            	_logger.Debug("AsyncExecuted: Yes it contains key");
                // get the result tied to this unique id
                AsyncResult asyncResult = element.getValue();
                // remove the result from the list
                _pendingResults.remove(element);
                // set the return value of the ARPC method
                asyncResult.setAsyncState(asyncState);
                _logger.Debug("AsyncExecuted: it contains key and its ASyncState is " + asyncState);
            }
        	}
        }
	}
