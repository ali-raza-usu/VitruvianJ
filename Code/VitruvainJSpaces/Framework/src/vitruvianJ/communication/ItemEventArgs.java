package vitruvianJ.communication;
import java.util.Hashtable;

import vitruvianJ.eventargs.EventArgs;


	/// <summary>
	/// Event arguments that are used by generic collections.
	/// </summary>
	/// <typeparam name="T">The type of the contained item.</typeparam>
	//internal

	
	public class ItemEventArgs<T> implements EventArgs
	{
		private T _item = null;//default(T);

		/// <summary>
		/// Construct an ItemEventArgs.
		/// </summary>
		/// <param name="item">The item associated with the event.</param>
		public ItemEventArgs(T item)
		{
			_item = item;
		}

		
		/// <summary>
		/// The item associated with the event.
		/// </summary>
		public T getItem()
		{
			return _item;
		}


		@Override
		public void addProperty(String name, Object value) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public Hashtable<String, Object> getProperties() {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public Object getPropertyValue(String key) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public void removeProperty(String name) {
			// TODO Auto-generated method stub
			
		}
	}
