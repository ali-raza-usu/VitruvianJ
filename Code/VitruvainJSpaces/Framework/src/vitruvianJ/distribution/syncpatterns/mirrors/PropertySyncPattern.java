package vitruvianJ.distribution.syncpatterns.mirrors;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.rmi.RemoteException;

import vitruvianJ.distribution.proxies.ProxyUtilities;
import vitruvianJ.distribution.syncpatterns.fragments.INotifyPropertyChanged;
import vitruvianJ.distribution.syncpatterns.fragments.PropertyChangedEventArgs;
import vitruvianJ.eventargs.EventArgs;
import vitruvianJ.events.Delegate;
import vitruvianJ.events.Event;

abstract public class PropertySyncPattern extends MirrorSyncPattern
{
    private String _propertyName = "";
    FilterPropertyChanges filterPropertyChanges = new FilterPropertyChanges();

    /// <summary>
    /// Start the sync pattern.
    /// </summary>
    public void Start()
    {
        super.Start();

        if (_method == null)
			try {
				throw new RemoteException("This SyncPattern is only valid on properties.");
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

        // get the property name
        _propertyName = _method.getName();

        INotifyPropertyChanged propParent = null;

        if (getIsLocal())
        {
            propParent = (INotifyPropertyChanged)getProxyParent();
        }
        else
        {
            propParent = (INotifyPropertyChanged)_proxy ;
        }

        // check that the parent object is an IPropertyChanged object
        if (propParent == null)
			try {
				throw new RemoteException("The type "+getNonProxyBaseType().getClass().getName()+" must implement INotifyPropertyChanged to use this SyncPattern.");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        if (getIsLocal())
        {
            // start listening to PropertyChanged events
            AddEvents();
        }
    }

    /// <summary>
    /// Stop the sync pattern.
    /// </summary>
    public void Stop()
    {
        super.Stop();

        if (getIsLocal())
        {
            // stop listening to PropertyChanged events
            RemoveEvents();
        }
    }

    /// <summary>
    /// Start listening to PropertyChanged events
    /// </summary>
    protected void AddEvents()
    {
        INotifyPropertyChanged propParent = (INotifyPropertyChanged)getProxyParent();
        propParent.getPropertyChanged().addObservers(filterPropertyChanges);
    }

    /// <summary>
    /// Stop watching for PropertyChanged events
    /// </summary>
    protected void RemoveEvents()
    {
        INotifyPropertyChanged propParent = (INotifyPropertyChanged)getProxyParent();
        propParent.getPropertyChanged().removeObservers(filterPropertyChanges);
    }

    /// <summary>
    /// Method called when any of the properties change.
    /// </summary>
    /// <param name="sender"></param>
    /// <param name="e"></param>
    private void FilterPropertyChanges(Object sender, PropertyChangedEventArgs e)
    {
        // check if this is the property of interest
        if (e.getPropertyValue(_propertyName)!= null)
            PropertyChanged();
    }

    class FilterPropertyChanges implements Delegate
    {

		@Override
		public void invoke(EventArgs args) {
			FilterPropertyChanges(null, (PropertyChangedEventArgs)args);
			
		}
    	
    }
    /// <summary>
    /// Method called when the property changes.
    /// </summary>
    /// <param name="sender"></param>
    /// <param name="e"></param>
    protected void PropertyChanged()
    { }

    /// <summary>
    /// This sync-pattern only works with properties.
    /// </summary>
    /// <param name="args"></param>
    /// <returns></returns>
    public Object HandleMethod(Object... args)
    {
        //throw new Exception();
    	return null;
    }

    /// <summary>
    /// Fire an event.  This is a brittle function, but there is no other way to do this.
    /// </summary>
    /// <param name="proxy"></param>
    /// <param name="handler"></param>
    
    protected void FirePropertyChangedEvent(Object value)
    {
        Type type = ProxyUtilities.getNonProxyBaseType(value.getClass());

        try {
	        Field fInfo = type.getClass().getField("PropertyChanged");//, BindingFlags.Instance | BindingFlags.NonPublic);
	        if (fInfo != null)
	        {
	            Event eventDelegate;			
					eventDelegate = (Event)fInfo.get(value);
				
	
	            if (eventDelegate != null)
	            {
	            	eventDelegate.RaiseEvent();
	//                ArrayList<Delegate> delegates = eventDelegate.getInvocationList();
	//
	//                for (Delegate dlg : delegates)
	//                    dlg.getMethod.Invoke(dlg.Target, new Object[] { value, new PropertyChangedEventArgs(_propertyName) });
	            }
	        }
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
    
//    protected void FirePropertyChangedEvent(Object value)
//    {
////        Type type = ProxyUtilities.getNonProxyBaseType(value.getClass());
////
////        Field fInfo = type.getClass().getField("PropertyChanged");//, BindingFlags.Instance | BindingFlags.NonPublic);
////        if (fInfo != null)
////        {
////            MulticastDelegate eventDelegate = (MulticastDelegate)fInfo.GetValue(value);
////
////            if (eventDelegate != null)
////            {
////                Delegate[] delegates = eventDelegate.GetInvocationList();
////
////                for(Delegate dlg : delegates)
////                    dlg.getClass().getMethod().Invoke(dlg.Target, new object[] { value, new PropertyChangedEventArgs(_propertyName) });
////            }
////        }
//    }
}
