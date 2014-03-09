package vitruvianJ.distribution.syncpatterns.mirrors.remote;

import java.lang.reflect.*;
import vitruvianJ.core.*;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.logging.JGUID;

import java.util.*;

    /// <summary>
    /// This is a sync pattern that pulls the object for the
    /// value of a property or method.  This pattern can work with
    /// methods and properties that take arguments.
    /// </summary>
    public class PullSyncPattern extends MirrorSyncPattern
	{
        private SignalSyncThread _pullThread = null;
        private int _pullTime = 1000;
        
        private Object _value = null;
        private boolean _initialized = false;

        public PullSyncPattern()
        { }

        public PullSyncPattern(int pullTime)
        {
            _pullTime = pullTime;
        }

        public void Start()
        {
            super.Start();

            boolean throwException = false;
            if (_method != null)
                throwException = true;
            else if (_method == null || _method == null)
                throwException = true;
            else if (_method.getParameterTypes().length > 0)
                throwException = true;

            if (throwException)
				try {
					throw new Exception("PullSyncPattern is only implemented on properties without any arguments.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            _pullThread = new SignalSyncThread("PullSyncPattern", new Pull());
            _pullThread.Start(true);
        }

        public void Stop()
        {
            super.Stop();

            try {
				_pullThread.Stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        class Pull implements Delegate
        {

			@Override
			public void invoke(EventArgs args) {
				Pull();
				
			}
        	
        }

        private void Pull()
        {
            PullValue();

            _pullThread.Sleep(_pullTime);
            _pullThread.Signal();
        }

        private void PullValue()
        {
            try
            {
                _value = ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, false, null);
            }
            catch (Exception e)
            { }
        }

        public Object HandleMethod(Object... args)
        {
            //throw new NotImplementedException();
            return null;
        }

        public Object HandlePropertyGet(Object... args)
        {
            if (_initializing)
                return null;

            if (!_initialized)
            {
                try {
					_value = ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, false, args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                _initialized = true;
            }               
            
            return _value;
        }

        public Object HandlePropertySet(Object... args)
        {
            if (_initializing)
            {
                _value = args[args.length - 1];
                _initialized = true;
            }
            else
            {
                try {
					ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _field, "RemoteValueChanged", args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            return null;
        }

        private void RemoteValueChanged(Object value)
        {
            _value = value;
        }
	}
