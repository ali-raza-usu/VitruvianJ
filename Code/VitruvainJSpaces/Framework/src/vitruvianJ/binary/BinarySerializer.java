package vitruvianJ.binary;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import vitruvianJ.logging.*;
import vitruvianJ.serialization.*;
import vitruvianJ.serialization.formatters.Formatter;


	public class BinarySerializer
	{
		private static JLogger _logger = new JLogger(BinarySerializer.class);
		private HashMap<Type, String> _typeChange = new HashMap<Type, String>();

		/// <summary>
		/// References to objects, and their object index.
		/// </summary>
		protected class References extends HashMap<Object, Integer>
		{}
		
		/// <summary>
		/// The dictionary is used for type changes.
		/// The dictionary is used to
		/// <list>
		/// <item>standardize serialization across platforms</item>
		/// <item>standardize serialization across applications</item>
		/// <item>backwards compatibility on type or namespace changes</item>
		/// </list> 
		/// </summary>
		public HashMap<Type, String> getTypeChange()
		{
			return _typeChange; 
		}
		
		public void setTypeChange(HashMap<Type, String> value)
		{
			_typeChange = value; 
		}
		
		/// <summary>
		/// Serialize an object into bytes.
		/// </summary>
		/// <param name="value">The object to serialize.</param>
		/// <returns>The bytes representing the object.</returns>
		public byte[] Serialize(Object value)
		{
			BinaryStream stream = new BinaryStream();
			References references = new References();

			try
			{
				Serialize(stream, true, value, null, references);
				return stream.ToBytes();
			}
			finally
			{
				references.clear();
                stream.Clear();
			}
		}

		/// <summary>
		/// Serialize the object into the stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The object to serialize.</param>
		/// <param name="formatter">The formatter to use when serializing the object; possibly null.</param>
		/// <param name="references">The references object that allows for serialization by reference.</param>
        protected void Serialize(BinaryStream stream, boolean requireType, Object value, Formatter formatter, References references)
		{			
			Type type = value.getClass();

            stream.AddType(type, requireType);

			if (type.equals(String.class))
			{
				if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (String) value);
			}
			else if (type.getClass().isEnum())
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Enum) value);
			}
			else if (type.equals(boolean.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Boolean) value);
			}
			else if (type.equals(byte.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Byte) value);
			}
			else if (type.equals(char.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Character) value);
			}
			else if (type.equals(double.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Double) value);
			}
			else if (type.equals(float.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Float) value);
			}
			else if (type.equals(int.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Integer) value);
			}
			else if (type.equals(long.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Long) value);
			}
			else if (type.equals(short.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Short) value);
			}
			else if (value.equals(Type.class))
			{
                if (formatter != null)
					stream.Add(formatter.Format(value));
				else
					Serialize(stream, (Type) value);
			}
			else
			{
				if (SerializeReference(stream, value, references))
					return;

				if (type.getClass().isArray())
					Serialize(stream, (Array) value, formatter, references);
				else if (value.equals(List.class))
					Serialize(stream, (List) value, formatter, references);
				else if (value.equals(HashMap.class))
					Serialize(stream, (HashMap) value, formatter, references);
				else
				{
					if (formatter != null)
						stream.Add(formatter.Format(value));
					else
						Serialize(stream, value, references);
				}
			}
		}

		/// <summary>
		/// Serialize the object into the stream using reflection.
		/// </summary>
		/// <param name="document">The binary stream.</param>
		/// <param name="value">The object to serialize.</param>
		/// <param name="references">The references object that allows for serialization by reference.</param>
		protected void Serialize(BinaryStream stream, Object value, References references)
		{
			
			Field[] fields =
				value.getClass().getFields();//GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance);

            boolean isOptimistic = SerializationUtilities.IsOptimistic(value.getClass());

			

			for (int i = 0; i < fields.length; i++)
			{
                if (!SerializationUtilities.IsSerializable(fields[i], isOptimistic))
					continue;

//				if (_logger.IsDebugEnabled())
//					_logger.DebugFormat("Serializing Field : %1s", fields[i].getName());

				Object item =null;
				try {
					item = fields[i].get(value);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (item == null)
					continue;

				Serialize(stream, fields[i], item, references);
			}
			
		}

		
		/// <summary>
		/// Serialize the field from the object.
		/// </summary>
		/// <param name="document">The document to serialize into.</param>
		/// <param name="fInfo">The field to serialize.</param>
		/// <param name="value">The object that the field is from.</param>
		/// <param name="references">The references object that allows for serialization by reference.</param>
		protected void Serialize(BinaryStream stream, Field fInfo, Object value, References references)
		{
			Serialize(stream,
                      SerializationUtilities.RequireType(fInfo.getType(), value.getClass()),
                      value,
                      SerializationUtilities.GetFormatter(fInfo),
                      references);
		}

		/// <summary>
		/// Serialize the list into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The list to serialize.</param>
		/// <param name="formatter">The formatter to use when serializing the items in the list; possibly null.</param>
		/// <param name="references">The references object that allows for serialization by reference.</param>
		protected void Serialize(BinaryStream stream, List value, Formatter formatter, References references)
		{
		
			Type elementType = (Object.class);
			Type[] genericArgs = value.getClass().getGenericInterfaces();
			if (genericArgs.length > 0)
				elementType = genericArgs[0];

			int length = value.size();
			stream.Add((int)length);
			
			for (int i = 0; i < length; i++)
			{
				if (value.get(i) == null)
					stream.Add(true);
				else
				{					
					stream.Add(false);
					Serialize(stream,
                              SerializationUtilities.RequireType(elementType, value.get(i).getClass()),
                              value.get(i),
                              formatter,
                              references);
				}
			}
			
		}

		/// <summary>
		/// Serialize the dictionary into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The dictionary to serialize.</param>
		/// <param name="formatter">The formatter to use when serializing the items in the list; possibly null.</param>
		/// <param name="references">The references object that allows for serialization by reference.</param>
		protected void Serialize(BinaryStream stream, HashMap value, Formatter formatter, References references)
		{		
			Type keyType = Object.class;
			Type valueType = Object.class;

			Type[] genericKeyArgs = value.keySet().getClass().getGenericInterfaces();
			Type[] genericValueArgs = value.values().getClass().getGenericInterfaces();

			if (genericKeyArgs != null)
				keyType = genericKeyArgs[0];
			if (genericValueArgs != null)
				valueType = genericValueArgs[1];
			
			stream.Add((int)value.size());
			
			for (Object key : value.keySet())
			{
				if (key == null)
					stream.Add(true);
				else
				{
					stream.Add(false);
					Serialize(stream,
                              SerializationUtilities.RequireType(keyType, key.getClass()),
                              key,
                              formatter,
                              references);
				}

				if (value.get(key) == null)
					stream.Add(true);
				else
				{
					stream.Add(false);
					Serialize(stream,
                              SerializationUtilities.RequireType(valueType, value.get(key).getClass()),
                              value.get(key),
                              formatter,
                              references);
				}
				
			}

		}

		/// <summary>
		/// Serialize the array into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The array to serialize.</param>
		/// <param name="formatter">The formatter to use when serializing the items in the array, possibly null.</param>
		/// <param name="references">The references object that allows for serialization by reference.</param>
		int Rank(Array value)
		{		
			int rank = 0;
			while(true)
			{
				if(Array.get(value, rank)==null)
					break;
				rank++;
					
			}
			return rank;
		}
		
protected void Serialize(BinaryStream stream, Array value, Formatter formatter, References references)
		{

			Type elementType = value.getClass().getComponentType();

			stream.Add((int)Rank(value));
			for (int i = 0; i < Rank(value); i++)
				stream.Add((int)value.getLength(i));

			SerializeArrayElement(stream, elementType, formatter, references, value, 0, new int[] {});				
		}

		/// <summary>
		/// Recursively serialize array elements.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="elementType">The type of the element.</param>
		/// <param name="formatter">The formatter to use for elements in the array.</param>
		/// <param name="references">Objects that have already been serialized.</param>
		/// <param name="value">The array to serialize.</param>
		/// <param name="curRank">The current rank.</param>
		/// <param name="indices">The set of indices for the current element.</param>
/*
		Object getValue(Array array, int[] indices)
		{
			Object obj = null;
			int rank = indices.length;
			Array.
			return obj;
		}
*/		

	static Object getArrayVal(Object array, int[] index)
	{
		Object element = new Object();
		element = array;
		for(int i =0; i<index.length; i++)
	    {        	        
	    	element = Array.get(element, index[i]);	        	        
	    }     
	           
		return element;
	}

		private void SerializeArrayElement(BinaryStream stream, Type elementType, Formatter formatter, References references, Array value, int curRank, int... indices)
		{
			int rank = Rank(value);
			if (curRank == rank)
			{
				Object childValue = getArrayVal(value, indices);// Array.get(value, indices);
				if (childValue == null)
					return;

                stream.Add((int)rank);
				for (int i = 0; i < rank; i++)
					stream.Add((int)indices[i]);

				Serialize(stream,
                          SerializationUtilities.RequireType(elementType, childValue.getClass()),
                          childValue,
                          formatter,
                          references);
			}
			else
			{
				int[] extIndices = new int[curRank+1];
				Copy(indices, extIndices, curRank);
				int length = value.getLength(curRank);
				for (int i = 0; i < length; i++)
				{
					extIndices[curRank] = i;
					SerializeArrayElement(stream, elementType, formatter, references, value, curRank + 1, extIndices);
				}
			}
		}
		void Copy(int[] src, int[] dst, int length)
		{
			for(int i=0; i<length; i++)
				dst[i] = src[i];	
		}
		/// <summary>
		/// Serialize the string into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The string to serialize.</param>
		protected void Serialize(BinaryStream stream, String value)
		{
			if (value == null)
				value = "";

			stream.Add(value);
		}

		/// <summary>
		/// Serialize the enum into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The enum to serialize.</param>
		protected void Serialize(BinaryStream stream, Enum value)
		{
			stream.Add(value.toString());
		}

		/// <summary>
		/// Serialize the bool into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The bool to serialize.</param>
		protected void Serialize(BinaryStream stream, boolean value)
		{
			stream.Add(value);
		}

		/// <summary>
		/// Serialize the byte into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The byte to serialize.</param>
		protected void Serialize(BinaryStream stream, byte value)
		{
			stream.Add(value);
		}


		/// <summary>
		/// Serialize the char into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The char to serialize.</param>
		protected void Serialize(BinaryStream stream, char value)
		{
			stream.Add((char)value);
		}

		
		/// <summary>
		/// Serialize the double into the binary stream.
		/// </summary>
		/// <param name="document">The binary stream.</param>
		/// <param name="value">The double to serialize.</param>
		protected void Serialize(BinaryStream stream, double value)
		{
			try {
				stream.Add(value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/// <summary>
		/// Serialize the float into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The float to serialize.</param>
		protected void Serialize(BinaryStream stream, float value)
		{
			stream.Add(value);
		}

		/// <summary>
		/// Serialize the int into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The int to serialize.</param>
		protected void Serialize(BinaryStream stream, int value)
		{
			stream.Add(value);
		}

		
		/// <summary>
		/// Serialize the long into the binary stream.
		/// </summary>
		/// <param name="document">The binary stream.</param>
		/// <param name="value">The long to serialize.</param>
		protected void Serialize(BinaryStream stream, long value)
		{
			stream.Add(value);
		}

		
		/// <summary>
		/// Serialize the short into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The short to serialize.</param>
		protected void Serialize(BinaryStream stream, short value)
		{
			stream.Add(value);
		}

		
		/// <summary>
		/// Serialize the Type into the binary stream.
		/// </summary>
		/// <param name="stream">The binary stream.</param>
		/// <param name="value">The Type to serialize.</param>
		protected void Serialize(BinaryStream stream, Type value)
		{
			stream.Add(value.getClass().getCanonicalName());//AssemblyQualifiedName);
		}

		static private boolean SerializeReference(BinaryStream stream, Object value, References references)
		{
			if (references.containsKey(value))
			{
				stream.Add(true);
				stream.Add((int)references.get(value));
				return true;
			}
			else
			{
				stream.Add(false);
				references.put(value, (int)references.size());
				return false;
			}
		}
	}

