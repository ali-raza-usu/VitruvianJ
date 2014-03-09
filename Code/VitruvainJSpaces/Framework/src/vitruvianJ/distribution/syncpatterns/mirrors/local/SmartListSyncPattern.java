package vitruvianJ.distribution.syncpatterns.mirrors.local;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.collections.ISmartList;
import vitruvianJ.core.SignalSyncThread;
import vitruvianJ.distribution.ObjectBroker;
import vitruvianJ.distribution.syncpatterns.LstAction;
import vitruvianJ.distribution.syncpatterns.LstMeta;
import vitruvianJ.distribution.syncpatterns.mirrors.MirrorSyncPattern;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.eventargs.PropertyEventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.logging.JGUID;
import vitruvianJ.logging.JLogger;

public class SmartListSyncPattern extends MirrorSyncPattern
{
    private final int SLEEP_TIME = 500;

    private JLogger _logger = new JLogger(SmartListSyncPattern.class);

    private static ISmartList _list = null;
    private List<LstMeta> _syncMeta = new ArrayList<LstMeta>();
    private SignalSyncThread _syncThread = null;
    ItemAdded itemAdded = new ItemAdded();
    ItemRemoved itemRemoved = new ItemRemoved();
    ItemsCleared itemCleared = new ItemsCleared();

    public SmartListSyncPattern()
	{
        _syncThread = new SignalSyncThread("SmartListSync", new SyncClass());
    }

    public void Start()
    {
        super.Start();
        try{        	
	        _list = (ISmartList)_method.invoke(getProxyParent(), null);
	        //_list = (ISmartList)_field.get(getProxyParent());
        }catch(Exception e)
        {
        	e.printStackTrace();
        }
        if (_list != null)
        {
            _list.ItemAdded.addObservers(itemAdded);
            _list.ItemRemoved.addObservers(itemRemoved);
            _list.ItemsCleared.addObservers(itemCleared);
        }

        _syncThread.Start(false);
    }

    public void Stop()
    {
        if (_list != null)
        {
            _list.ItemAdded.removeObservers(itemAdded);
            _list.ItemRemoved.removeObservers(itemRemoved);
            _list.ItemsCleared.removeObservers(itemCleared);
        }
        
        super.Stop();

        try {
			_syncThread.Stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    

    class ItemsCleared implements Delegate
    {
		@Override
		public void invoke(EventArgs args) {
			ItemsCleared(null, args);			
		}
    }
    
    void ItemsCleared(Object sender, EventArgs e)
    {
        _syncMeta.add(new LstMeta(new JGUID(), LstAction.Clear, null));
        _syncThread.Signal();
    }

    public Object HandleMethod(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local SmartList SyncPattern : %1s : %2s", _method.getDeclaringClass().getName(), _method.getName());

       // throw new Exception();
        return null;
	}

    public Object HandlePropertyGet(Object... args)
	{
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local SmartList SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

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
        if (_logger.IsDebugEnabled())
            _logger.DebugFormat("Local SmartList SyncPattern : %1s : %2s", _field.getDeclaringClass().getName(), _field.getName());

        //throw new Exception();
        return null;
    }

    class SyncClass implements Delegate
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
            catch(Exception ex)
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
        switch (meta.getAction())
        {
            case Add:
                _list.ItemAdded.removeObservers(itemAdded);
                _list.addItem(meta.getItem());
                _list.ItemAdded.addObservers(itemAdded);
                break;
            case Remove:
                _list.ItemRemoved.removeObservers(itemRemoved);
                _list.removeItem(meta.getItem());
                _list.ItemRemoved.addObservers(itemRemoved);
                break;
            case Clear:
                _list.ItemsCleared.removeObservers(itemCleared);
                _list.clear();
                _list.ItemsCleared.addObservers(itemCleared);
                break;
        }
    }
}
