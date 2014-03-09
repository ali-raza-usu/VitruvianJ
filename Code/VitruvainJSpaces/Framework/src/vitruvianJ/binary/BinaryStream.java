package vitruvianJ.binary;
import vitruvianJ.core.ClassFactory;
import vitruvianJ.serialization.xml.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.*;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import vitruvianJ.*;

public class BinaryStream extends ByteList
{
	private List<Type> _types = new LinkedList<Type>();
	private int offset; 
	public BinaryStream()
	{}
	
	public BinaryStream(byte[] bytes)
	{
        FromBytes(bytes);
    }
	
	/// <summary>
	/// Add a type to the stream.
	/// </summary>
	/// <param name="type">The type to add.</param>
	void AddType(Type type, boolean requireType)
	{
        Add(requireType);
        if (!requireType)
            return;

		int index = _types.indexOf(type);
		if (index < 0)
		{
			index = _types.size();
			_types.add(type);
		}
		
		Add((int)index);
	}

    /// <summary>
    /// Get the type at the offset.
    /// </summary>
    /// <param name="defaultType">The type to use if the type is not specified.</param>
    /// <param name="offset">The offset into the stream.</param>
    /// <returns>The type at the given offset or the default type.</returns>
    Type GetType(Type defaultType, int offset)
    {
        boolean containsType = GetBool(offset);
        if (containsType)
        {
            int index = GetInt(offset);
            defaultType = _types.get(index);
        }

        return defaultType;
    }

    /// <summary>
	/// Get the bytes contained by the stream.
	/// </summary>
	/// <returns>The bytes in the stream.</returns>
	public byte[] ToBytes()
	{
		ByteList result = new ByteList();

        result.Add((int)_types.size());
        for (int i = 0; i < _types.size(); i++)
            result.Add(_types.get(i).getClass().getCanonicalName());
		
		result.Add(this);
		return result.ToBytes();
	}
	
	/// <summary>
	/// Fill the binary stream with the bytes.
	/// </summary>
	/// <param name="bytes">The bytes.</param>
	
	public String getString(byte[] bytes, int index, int length)
	{
		String str ="";
		byte[] tempByte = new byte[length];
		for(int i=index; i<index+length; i++)
		{
			tempByte[i-index] = bytes[i];
		}		
		str = new String(tempByte);
		return str;
	}
	
	public void FromBytes(byte[] bytes)		
	{
		Clear();
		
		int index = 0;
		int numTypes =  (Integer)getUInt(bytes, index);
		index += 4;
		
		for (int i = 0; i < numTypes; i++)
		{
			int length = (Integer)getUInt(bytes, index);
			index += 2;
			String type = getString(bytes, index, length);
			index += length;
			try{
			_types.add(ClassFactory.FindType(type));
			}catch(Exception ex)
			{
				
			}
		}
		
		Add(bytes, index, bytes.length - index);
	}
}