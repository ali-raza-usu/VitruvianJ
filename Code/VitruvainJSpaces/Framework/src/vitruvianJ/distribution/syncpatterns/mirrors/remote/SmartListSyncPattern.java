package vitruvianJ.distribution.syncpatterns.mirrors.remote;

import java.util.*;

import vitruvianJ.logging.*;
import vitruvianJ.collections.ISmartList;
import vitruvianJ.core.*;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.proxies.*;
import vitruvianJ.distribution.syncpatterns.LstAction;
import vitruvianJ.distribution.syncpatterns.LstMeta;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.eventargs.PropertyEventArgs;
import vitruvianJ.events.Delegate;


    public class SmartListSyncPattern extends MirrorSyncPattern
	{
        private final int SLEEP_TIME = 500;
        
        private JLogger _logger = new JLogger(SmartListSyncPattern.class);
        
        private static ISmartList _list = null;
        private List<LstMeta> _syncMeta = new ArrayList<LstMeta>();
        private SignalSyncThread _syncThread = null;
        private boolean _initialized = false;
        ItemAdded itemAdded = new ItemAdded();
        ItemRemoved itemRemoved = new ItemRemoved();
        ItemCleared itemCleared = new ItemCleared();

        public SmartListSyncPattern()
		{
            _syncThread = new SignalSyncThread("SmartListSync", new Sync());
        }

        public void Start()
        {
            super.Start();

            if (!_initialized)
            {
                try {
					_list = (ISmartList)ObjectBroker.ExecuteRemoteMethod((IRemoteSyncProxy)_proxy, _method, false, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                _initialized = true;
            }

            _syncThread.Start(false);

            if (_list != null)
            {
                _list.ItemAdded.addObservers(itemAdded);
                _list.ItemRemoved.addObservers(itemRemoved);
                _list.ItemsCleared.addObservers(itemCleared);
            }
        }

        public void Stop()
        {
            try {
				_syncThread.Stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            super.Stop();

            if (_list != null)
            {
                _list.ItemAdded.removeObservers(itemAdded);
                _list.ItemRemoved.removeObservers(itemRemoved);
                _list.ItemsCleared.removeObservers(itemCleared);
            }
        }
        
        
        class ItemAdded implements Delegate
        {

			@Override
			public void invoke(EventArgs args) {
				ItemAdded(args);				
			}        	
        }

        private void ItemAdded(Object item)
        {
        	EventArgs args = (PropertyEventArgs)item;
        	Object value = args.getPropertyValue("value");
            _syncMeta.add(new LstMeta(new JGUID(), LstAction.Add, value));
            _syncThread.Signal();            
        }

        class ItemRemoved implements Delegate
        {

			@Override
			public void invoke(EventArgs args) {
				ItemRemoved(args);				
			}        	
        }
        
        private void ItemRemoved(Object item)
        {
        	EventArgs args = (PropertyEventArgs)item;
        	Object value = args.getPropertyValue("value");
            _syncMeta.add(new LstMeta(new JGUID(), LstAction.Remove, value));
            _syncThread.Signal();            
        }

        class ItemCleared implements Delegate
        {

			@Override
			public void invoke(EventArgs args) {
				ItemsCleared(null, args);				
			}        	
        }
        
        private void ItemsCleared(Object sender, EventArgs e)
        {
            _syncMeta.add(new LstMeta(new JGUID(), LstAction.Clear, null));
            _syncThread.Signal();
        }

        public Object HandleMethod(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Local SmartList SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            //throw new NotImplementedException();
            return null;
		}

        public Object HandlePropertyGet(Object... args)
		{
            if (_logger.IsDebugEnabled())
                _logger.DebugFormat("Local SmartList SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            return _list;
		}

        public Object HandlePropertySet(Object... args)
		{
//            if (_logger.IsDebugEnabled())
//                _logger.DebugFormat("Local SmartList SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

            if (_initializing)
            {
                _list = (ISmartList)args[args.length - 1];
                _initialized = true;
                return null;
            }
            else
                //throw new NotImplementedException();
            	throw null;
        }
        
        class Sync implements Delegate
        {

			@Override
			public void invoke(EventArgs args) {
				Sync();
				
			}
        	
        }

        public void Sync()
        {
            while (_syncMeta.size() > 0)
            {
                try
                {
                    ObjectBroker.BroadcastExecuteRemoteSyncPatternMethod(new ArrayList<JGUID>(), _proxy, _method, "SyncItem", _syncMeta.get(0));
                    _syncMeta.remove(0);
                }
                catch(Exception e)
                {
                    // signal the thread and break,
                    // in case it wants to exit
                    _syncThread.Sleep(SLEEP_TIME);
                    _syncThread.Signal();
                    break;
                }
            }
        }

        public void SyncItem(LstMeta meta)
        {
            //switch (meta.getAction())
            {
                if(meta.getAction().equals(LstAction.Add)){
                    _list.ItemAdded.removeObservers(itemAdded);
                    _list.addItem(meta.getItem());
                    _list.ItemAdded.addObservers(itemAdded);                    
                }
                else if(meta.getAction().equals(LstAction.Remove)){                
                    _list.ItemRemoved.removeObservers(itemRemoved);
                    _list.removeItem(meta.getItem());
                    _list.ItemRemoved.addObservers(itemRemoved);
                }
                else if(meta.getAction().equals(LstAction.Clear)){
                    _list.ItemsCleared.removeObservers(itemCleared);
                    _list.clear();
                    _list.ItemsCleared.addObservers(itemCleared);
                }
            }
        }
	}

