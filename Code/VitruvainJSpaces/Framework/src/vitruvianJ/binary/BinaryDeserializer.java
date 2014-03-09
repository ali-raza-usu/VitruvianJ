package vitruvianJ.binary;
import java.lang.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;

import vitruvianJ.serialization.SerializationUtilities;
import vitruvianJ.serialization.formatters.Formatter;
import vitruvianJ.core.ClassFactory;

import vitruvianJ.logging.*;
	public class BinaryDeserializer
	{
		private static JLogger _logger = new JLogger(BinaryDeserializer.class);
		private HashMap<String, Type> _typeChange = new HashMap<String, Type>();
	//	int index;
		/// <summary>
		/// The reference to the index position of an object.
		/// </summary>
		protected class References extends HashMap<Integer, Object>
		{}
		
		/// <summary>
		/// The dictionary is used for type changes.
		/// The dictionary is used to
		/// <list>
		/// <item>standardize serialization across platforms</item>
		/// <item>standardize serialization across applications</item>
		/// <item>backwards compatibility on type or namespace changes</item>
		/// </list>
		/// <remarks>Key = name to use in serialization</remarks>
		/// <remarks>Value = type to change for the name</remarks>
		/// </summary>
		public HashMap<String, Type> getTypeChange()
		{
		return _typeChange; 
		}
		
		public void setTypeChange(HashMap<String, Type> value)
		{
			_typeChange = value; 
		}
		
		/// <summary>
		/// Deserialize an object from bytes.
		/// </summary>
		/// <param name="bytes">The bytes that define the object.</param>
		/// <returns>The object represented by the stream.</returns>
		public Object Deserialize(byte[] bytes)
		{
            BinaryStream stream = new BinaryStream(bytes);
			References references = new References();

			try
			{
                int index = 0;
                return Deserialize(stream, index, null, null, null, references);
			}
			finally
			{
				references.clear();
                stream.Clear();
            }
		}
		
		/// <summary>
		/// Deserialize the bytes into the given object.
		/// </summary>
		/// <param name="bytes">The bytes that define the object.</param>
		/// <param name="value">The object represented by the stream.</param>
		public void Deserialize(byte[] bytes, Object value)
		{
            BinaryStream stream = new BinaryStream(bytes);
			References references = new References();
			try
			{
				int index = 0;
				Deserialize(stream, index, value.getClass(), value, null, references);
			}
			finally
			{
				references.clear();
                stream.Clear();
            }
		}
		
		/// <summary>
		/// Deserialize the node into the given object.
		/// If the given object is null an object will be created.
		/// </summary>
		/// <param name="node">The node defining the object.</param>
		/// <param name="type">The type of the object.</param>
		/// <param name="curValue">The current value of the object; possibly null.</param>
		/// <param name="formatter">The formatter to use when deserializing this object; possibly null.</param>
		/// <returns>The object that was deserialized.</returns>
		/// <param name="references">The references object that allows for deserialization by reference.</param>
		protected Object Deserialize(BinaryStream stream, int index, Type type, Object curValue, Formatter formatter, References references)
		{
            type = stream.GetType(type, index);
            try{
	            if (type.equals(String.class))
				{
					if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetString(index);
				}
				else if (type.getClass().isEnum())
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = Enum.valueOf(((Enum)type).getDeclaringClass(),stream.GetString(index));
				}
				else if (type.equals(boolean.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetBool(index);
				}
				else if (type.equals(byte.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetByte(index);
				}			
				else if (type.equals(char.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetChar(index);
				}
				
				else if (type.equals(double.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetDouble(index);
				}
				else if (type.equals(float.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetFloat(index);
				}
				else if (type.equals(int.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetInt(index);
				}			
				else if (type.equals(long.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetLong(index);
				}			
				else if (type.equals(short.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = stream.GetShort(index);
				}
				else if (type.equals(Type.class))
				{
	                if (formatter != null)
						curValue = formatter.Unformat(stream.GetString(index));
					else
						curValue = ClassFactory.FindType(stream.GetString(index));
				}
				else
				{
					Object refObj = DeserializeReference(stream,index, references);
					if (refObj != null)
						return refObj;
	
					if (type.getClass().isArray())
					{
						curValue = ClassFactory.CreateObject(type, GetRanks(stream,index));
	
						DeserializeObjectId(stream, index, curValue, references);
						Deserialize(stream, index, (Array)curValue, formatter, references);
					}
					else if (List.class.isAssignableFrom(type.getClass()))
					{
						if (curValue == null)
							curValue = ClassFactory.CreateObject(type);
	
						DeserializeObjectId(stream, index, curValue, references);
						Deserialize(stream, index, (List)curValue, formatter, references);
					}
					else if (Dictionary.class.isAssignableFrom(type.getClass()))
					{
						if (curValue == null)
							curValue = ClassFactory.CreateObject(type);
	
						DeserializeObjectId(stream, index, curValue, references);
						Deserialize(stream, index, (HashMap)curValue, formatter, references);
					}
					else
					{
						if (formatter != null)
							curValue = formatter.Unformat(stream.GetString(index));
						else
						{
							if (curValue == null)
								curValue = ClassFactory.CreateObject(type);
						
							DeserializeObjectId(stream, index, curValue, references);
							Deserialize(stream, index, curValue, references);
						}
					}
				}
	
				return curValue;
            }catch(Exception ex)
            {
            	return null;
            }
		}
		
		/// <summary>
		/// Deserialize the node into the given object.
		/// </summary>
		/// <param name="node">The node defining the object.</param>
		/// <param name="value">The object to deserialize into.  The object is 
		/// deserialized using reflection.</param>
		/// <param name="references">The references object that allows for deserialization by reference.</param>
		protected void Deserialize(BinaryStream stream, int index, Object value, References references)
		{
			//ProperInfo[] properties =
				//value.GetType().GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static |
				  //                            BindingFlags.Instance);
			Field[] fields =
				value.getClass().getFields();// BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance);
			
            boolean isOptimistic = SerializationUtilities.IsOptimistic(value.getClass());

			
/*
			for (int i = 0; i < properties.Length; i++)
			{
				if (!SerializationUtilities.IsDeserializable(properties[i], isOptimistic))
					continue;

				if (_logger.IsDebugEnabled())
					_logger.DebugFormat("Deserializing Property : {0}", properties[i].Name);

				Object childValue = null; 
				Type childType = properties[i].PropertyType;
				
				if (childType.IsClass && properties[i].CanRead)
					childValue = properties[i].GetValue(value, null);
				if (childValue != null)
					childType = childValue.getClass();

				childValue = Deserialize(stream, index, childType, childValue, SerializationUtilities.GetFormatter(properties[i]), references);
				properties[i].SetValue(value, childValue, null);
			}

*/			
			for (int i = 0; i < fields.length; i++)
			{
                if (!SerializationUtilities.IsDeserializable(fields[i], isOptimistic))
					continue;

				if (_logger.IsDebugEnabled())
					_logger.DebugFormat("Deserializing Field : {0}", fields[i].getName());
				try {
						Object childValue = null; 
						Type childType = fields[i].getType();			
						if (isClass(childType))
							
								childValue = fields[i].get(value);
							
						if (childValue != null)
							childType = childValue.getClass();
		
						childValue = Deserialize(stream, index, childType, childValue, SerializationUtilities.GetFormatter(fields[i]), references);
						fields[i].set(value, childValue);
				
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}

			
		}
		
		boolean isClass(Object obj)
		{
			if(!obj.getClass().isArray() && !obj.getClass().isInterface() && !obj.getClass().isPrimitive())
				return true;
			return false;
		}
		/// <summary>
		/// Deserialize the node into the given list.
		/// </summary>
		/// <param name="node">The node defining the list.</param>
		/// <param name="value">The list to deserialize into.</param>
		/// <param name="formatter">The formatter to use for contained items.</param>
		/// <param name="references">The references object that allows for deserialization by reference.</param>
		protected void Deserialize(BinaryStream stream, int index, List value, Formatter formatter, References references)
		{
			value.clear();

			Type elementType = Object.class;
			Type[] genericArgs = value.getClass().getGenericInterfaces();
			if (genericArgs.length > 0)
				elementType = genericArgs[0];

			int length = stream.GetInt(index);
			for (int i = 0; i < length; i++)
			{
				// check for null
                if (stream.GetBool(index))
                {
                    value.add(null);
                    continue;
                }
				
				Object childValue = Deserialize(stream, index, elementType, null, formatter, references);
                value.add(childValue);
			}
		}
		
		/// <summary>
		/// Deserialize the node into the given dictionary.
		/// </summary>
		/// <param name="node">The node defining the dictionary.</param>
		/// <param name="value">The dictionary to deserialize into.</param>
		/// <param name="formatter">The formatter to use for contained items.</param>
		/// <note>The formatter needs to be changed to allow for key / value formatting.</note>
		/// <param name="references">The references object that allows for deserialization by reference.</param>
		protected void Deserialize(BinaryStream stream, int index, HashMap value, Formatter formatter, References references)
		{
			value.clear();

			Type keyType = Object.class;
			Type valueType = Object.class;

			Type[] genericKeyArgs = value.keySet().getClass().getGenericInterfaces();
			Type[] genericValueArgs = value.values().getClass().getGenericInterfaces();

			if (genericKeyArgs != null)
				keyType = genericKeyArgs[0];
			if (genericValueArgs != null)
				valueType = genericValueArgs[1];

			int length = stream.GetInt(index);
			
			for (int i = 0; i < length; i++)
			{
				Object entryKey = null;
				Object entryValue = null;
				
				// check for null
				if (!stream.GetBool(index))
					entryKey = Deserialize(stream, index, keyType, null, formatter, references);
				
				// check for null
				if(!stream.GetBool(index))
					entryValue = Deserialize(stream, index, valueType, null, formatter, references);

				value.put(entryKey, entryValue);
			}
		}
		
		/// <summary>
		/// Deserialize the node into the given array.
		/// </summary>
		/// <param name="node">The node defining the array.</param>
		/// <param name="value">The array to deserialize into.</param>
		/// <param name="formatter">The formatter to use for contained items.</param>
		/// <param name="references">The references object that allows for deserialization by reference.</param>
		protected void Deserialize(BinaryStream stream, int index, Array value, Formatter formatter, References references)
		{
            Type elementType = value.getClass().getComponentType();

            for (int i = 0; i < Array.getLength(value); i++)
            {
                int rank = stream.GetInt(index);
                int[] childIndex = new int[rank];
                for (int j = 0; j < rank; j++)
                    childIndex[j] = (int)stream.GetInt(index);

                
                Object childValue = Deserialize(stream, index, elementType, null, formatter, references);
                value = (Array)setArrayVal(value, childIndex, childValue);                
            }
		}

		static Object setArrayVal(Object array, int[] index, Object val)
	    {
	    	Object element = new Object();
	    	element = array;
	    	for(int i =0; i<index.length-1; i++)
	        {        	        
	        	element = Array.get(element, index[i]);	        	        
	        }     
	        Array.set(element, index[index.length-1], val);       
	    	return array;
	    }
		
		static private void DeserializeObjectId(BinaryStream stream, int index, Object value, References references)
		{
            references.put((int)references.size(), value);
		}

        static private Object DeserializeReference(BinaryStream stream, int offset, References references)
		{
            boolean isRef = stream.GetBool(offset);
            if (isRef)
            {
                int index = stream.GetInt(offset);
                return references.get(index);
            }
            else
                return null;
		}

        private static int[] GetRanks(BinaryStream stream, int offset)
		{
			int ranks = stream.GetInt(offset);

            int[] result = new int[ranks];
			for (int i = 0; i < ranks; i++)
				result[i] = stream.GetInt(offset);
			return result;
		}
	}


