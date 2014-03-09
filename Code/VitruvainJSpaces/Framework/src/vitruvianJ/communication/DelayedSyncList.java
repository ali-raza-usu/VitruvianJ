package vitruvianJ.communication;

import java.util.ArrayList;
import java.util.List;

import vitruvianJ.events.Delegate;



	/// <summary>
	/// A list that delays synchronizing add/removes with the inner list.
	/// </summary>
	//internal 
	public class DelayedSyncList<T> extends ArrayList<T>
	{
        private final class SyncItem<T>
        {
            private T _item;
            private Delegate _callback;//<ItemEventArgs<T>> _callback;

            public SyncItem(T item, Delegate callback)//EventHandler<ItemEventArgs<T>> callback)
            {
                _item = item;
                _callback = callback;
            }

            public T getItem()
            {
                return _item; 
            }

            //public EventHandler<ItemEventArgs<T>> getCallback()
            public Delegate getCallback()
            {
                return _callback; 
            }
        }

        private List<SyncItem<T>> _adds = new ArrayList<SyncItem<T>>();
        private List<SyncItem<T>> _removes = new ArrayList<SyncItem<T>>();

        // protects (InnerList)
        private Object _syncRoot = new Object();

        private int _lockCnt = 0;

        public int getSyncCount()
        {

                synchronized (_syncRoot)
                {
                    synchronized (_adds)
                    {
                        synchronized (_removes)
                        {
                            return super.size() + _adds.size() - _removes.size();
                        }
                    }
                }
            }
        

        //new 
        public int getCount()
        {
                synchronized (_syncRoot)
                {
                    return super.size();
                }
        }

        //new 
        public boolean Contains(T item)
        {
            synchronized (_syncRoot)
            {
                if (super.contains(item))
                    return true;

                synchronized (_adds)
                {
                    for (SyncItem<T> syncItem : _adds)
                    {
                        if (syncItem.getItem().equals(item))
                            return true;
                    }
                }

                return false;
            }
        }

        //new 
        public void Add(T item)
        {
            Add(item, null);
        }

        //public void Add(T item, EventHandler<ItemEventArgs<T>> callback)
        public void Add(T item, Delegate callback)
        {
            boolean _synced = false;

            synchronized (_syncRoot)
            {
                if (_lockCnt == 0)
                {
                    super.add(item);
                    _synced = true;
                }
                else
                {
                    _adds.add(new SyncItem<T>(item, callback));
                    _synced = false;
                }
            }

            if (_synced && callback != null)
                callback.invoke(new ItemEventArgs<T>(item));
        }

        //new 
        public void Remove(T item)
        {
            Remove(item, null);
        }

        //public void Remove(T item, EventHandler<ItemEventArgs<T>> callback)
        public void Remove(T item, Delegate callback)
        {
            boolean _synced = false;

            synchronized (_syncRoot)
            {
                if (_lockCnt == 0)
                {
                    super.remove(item);
                    _synced = true;
                }
                else
                {
                    _removes.add(new SyncItem<T>(item, callback));
                    _synced = false;
                }
            }

            if (_synced && callback != null)
                callback.invoke(new ItemEventArgs<T>(item));
        }        

        //new 
        public void Clear()
        {
            synchronized (_syncRoot)
            {
                super.clear();

                _adds.clear();
                _removes.clear();
            }
        }

        public void Lock()
        {
            List<SyncItem<T>> syncItems = new ArrayList<SyncItem<T>>();

            synchronized (_syncRoot)
            {
                if (_lockCnt == 0)
                    syncItems = Sync();

                _lockCnt++;
            }

            // do this outside of the lock
            for(SyncItem<T> syncItem : syncItems)
            {
                if (syncItem.getCallback() != null) 
                    syncItem.getCallback().invoke(new ItemEventArgs<T>(syncItem.getItem()));
            }
            syncItems.clear();
        }

        public void Unlock()
        {
            List<SyncItem<T>> syncItems = new ArrayList<SyncItem<T>>();

            synchronized (_syncRoot)
            {
                _lockCnt--;

                if (_lockCnt == 0)
                    syncItems = Sync();
            }

            // do this outside of the lock
            for (SyncItem<T> syncItem : syncItems)
            {
                if (syncItem.getCallback() != null)
                    syncItem.getCallback().invoke(new ItemEventArgs<T>(syncItem.getItem()));
            }
            syncItems.clear();
        }

        private List<SyncItem<T>> Sync()
        {
            List<SyncItem<T>> result = new ArrayList<SyncItem<T>>();

            synchronized (_adds)
            {
                for (int i = 0; i < _adds.size(); i++)
                {
                    T item = _adds.get(i).getItem();
                    result.add(_adds.get(i));
                    
                    super.add(item);
                }
                _adds.clear();
            }

            synchronized (_removes)
            {
                for (int i = 0; i < _removes.size(); i++)
                {
                    T item = _removes.get(i).getItem();
                    result.add(_removes.get(i));

                    super.remove(item);
                }
                _removes.clear();
            }            

            return result;
        }
	}
