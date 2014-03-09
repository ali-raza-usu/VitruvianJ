package vitruvianJ.distribution.encoders;

import java.lang.reflect.Type;

import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.OptimisticSerialization;
import vitruvianJ.serialization.Serialize;

	@OptimisticSerialization
	public class ObjectMarker
	{
		private JGUID _id = null;
		private Type _objectType = null;

		/// <summary>
		/// Default constructor.
		/// </summary>
		public ObjectMarker()
		{}

		/// <summary>
		/// The unique identifier for the object.
		/// </summary>
		
		//@JGUIDFormatter()
		@Serialize//(getName = "get")
		public JGUID getId()
		{
			return _id;
		}
		
		@Serialize//(getName = "set")
		public void setId(JGUID value) 
		{ 
			_id = value;
		}

		/// <summary>
		/// The Type of the object.
		/// </summary>
		//@TypeFormatter()
		
		@Serialize//(getName = "get")
		public Type getObjectType()
		{
			return _objectType; 
		}
		@Serialize//(getName = "set")
		public void setObjectType(Type value)
		{ 
			_objectType = value;
		}
	}