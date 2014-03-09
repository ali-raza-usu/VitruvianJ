package vitruvianJ.binary;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import vitruvianJ.logging.PathUtilities;

import org.w3c.dom.*;

import vitruvianJ.serialization.SerializationUtilities;

	public class BinaryFramework
	{
		private static HashMap<String, Type> _deserializerTypeChange = new HashMap<String, Type>();
		private static HashMap<Type, String> _serializerTypeChange = new HashMap<Type, String>();

		/// <summary>
		/// Add a type change for serialization / deserialization.
		/// This helps to
		/// <list>
		/// <item>standardize serialization across platforms</item>
		/// <item>standardize serialization across applications</item>
		/// <item>backwards compatibility on type or namespace changes</item>
		/// </list>
		/// </summary>
		/// <param name="name">Name to use in serialization.</param>
		/// <param name="type">Type to change for the name.</param>
		public static void AddTypeChange(String name, Type type)
		{
			_serializerTypeChange.put(type, name);
			_deserializerTypeChange.put(name, type);
		}

		/// <summary>
		/// Serialize the object into bytes.
		/// </summary>
		/// <param name="value">The object to serialize.</param>
		/// <returns>Bytes representing the object.</returns>
		public static byte[] Serialize(Object value)
		{
			BinarySerializer serializer = new BinarySerializer();
			serializer.setTypeChange(_serializerTypeChange);
			return serializer.Serialize(value);
		}

		/// <summary>
		/// Deserialize the object from the stream.
		/// </summary>
		/// <param name="doc">The stream representing the object.</param>
		/// <returns>The object represented by bytes.</returns>
		public static Object Deserialize(byte[] bytes)
		{
			BinaryDeserializer deserializer = new BinaryDeserializer();
			deserializer.setTypeChange(_deserializerTypeChange);
			return deserializer.Deserialize(bytes);
		}

		/// <summary>
		/// Deserialize the object from bytes.
		/// </summary>
		/// <param name="doc">The bytes representing the object.</param>
		/// <param name="value">The object to deserialize the document into.</param>
		public static void Deserialize(byte[] bytes, Object value)
		{
			BinaryDeserializer deserializer = new BinaryDeserializer();
			deserializer.setTypeChange(_deserializerTypeChange);
            deserializer.Deserialize(bytes, value);
		}

		

		/// <summary>
		/// Serialize the object and save it to the file.
		/// </summary>
		/// <param name="filename">The binary file.</param>
		/// <param name="value">The object to serialize.</param>
		public static void Serialize(String filename, Object value)
		{
			PathUtilities.PushFilename(filename);

			try
			{				
				byte[] bytes = Serialize(value);
				FileOutputStream fileStream = new FileOutputStream(filename);//, FileMode.Create);
                fileStream.write(bytes, 0, bytes.length);
				fileStream.close();
			}
			catch(Exception e)
			{}
			finally
			{
				PathUtilities.PopFilename(filename);
			}
		}

		/// <summary>
		/// Deserialize an object from the file.
		/// </summary>
		/// <param name="filename">The binary file.</param>
		/// <returns>The object that was deserialized.</returns>
		public static Object Deserialize(String filename)
		{
			File file  = new File(filename);
			if (!file.exists())
				return null;

			PathUtilities.PushFilename(filename);

			try
			{
				FileInputStream fileStream = new FileInputStream(filename);//, FileMode.Open);
				int streamLength = (int)file.length();
				byte[] bytes = new byte[streamLength];
				fileStream.read(bytes, 0, streamLength);
				fileStream.close();

				return Deserialize(bytes);
			}
			catch(Exception e)
			{
				return null;
			}
			finally
			{
				PathUtilities.PopFilename(filename);
			}
		}

		/// <summary>
		/// Deserialize the file into the object.
		/// </summary>
		/// <param name="filename">The binary file.</param>
		/// <param name="value">The object to deserialize into.</param>
		public static void Deserialize(String filename, Object value)
		{
			File file = new File(filename);
			if (!file.exists())
				return;

			PathUtilities.PushFilename(filename);

			try
			{
				int streamLen = (int)file.length();
				FileInputStream fileStream = new FileInputStream(filename);//, FileMode.Open);
				byte[] bytes = new byte[streamLen];
				fileStream.read(bytes, 0, streamLen);
				fileStream.close();
				
				Deserialize(bytes, value);
			}
			catch(Exception e)
			{}
			finally
			{
				PathUtilities.PopFilename(filename);
			}
		}

				/// <summary>
		/// Deserialize an object from the binary fragment.
		/// </summary>
		/// <param name="xml">The bytes.</param>
		/// <returns>The deserialized object.</returns>
		public static Object DeserializeBytes(byte[] bytes)
		{
			return Deserialize(bytes);
		}

		/// <summary>
		/// Deserialize the binary fragment into the object.
		/// </summary>
		/// <param name="xml"></param>
		/// <param name="value"></param>
		public static void DeserializeBytes(byte[] bytes, Object value)
		{
            Deserialize(bytes, value);
		}


		/// <summary>
		/// Deserialize the embedded binary stream into the object.
		/// </summary>
		/// <param name="type">The type that the resource name is relative to.</param>
		/// <param name="resource">The name of the resource.</param>
		/// <param name="value">The object to deserialize into.</param>
		public static void Deserialize(Type type, String resource, Object value)
		{
			int streamLen  = resource.length();
			InputStream embStream = GetEmbeddedStream(type, resource);
			byte[] bytes = new byte[streamLen];
			try {
				embStream.read(bytes, 0, streamLen);
				embStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Deserialize(bytes, value);
		}

		/// <summary>
		/// Deserialize the embedded xml.
		/// </summary>
		/// <param name="type">The type that the resource name is relative to.</param>
		/// <param name="resource">The name of the resource.</param>
		/// <returns>The deserialized object.</returns>
		public static Object Deserialize(Type type, String resource) 
		{
			InputStream embStream = GetEmbeddedStream(type, resource);
			int streamLen = resource.length();
			byte[] bytes = new byte[streamLen];
			try {
				embStream.read(bytes, 0, streamLen);
				embStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			return Deserialize(bytes);
		}

		/// <summary>
		/// Get an embedded stream.
		/// </summary>
		/// <param name="type">The base type that resources are relative to.</param>
		/// <param name="resource">The name of the resource.</param>
		/// <returns>A stream to the resource.</returns>
		private static InputStream GetEmbeddedStream(Type type, String resource)
		{
			try {
				return SerializationUtilities.GetEmbeddedFile(type, resource);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		

	}

