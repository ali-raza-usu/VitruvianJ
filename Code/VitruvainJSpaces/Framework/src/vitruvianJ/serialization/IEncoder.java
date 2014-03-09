package vitruvianJ.serialization;
import java.lang.Cloneable;

public interface IEncoder extends Cloneable
{
	/// <summary>
	/// Convert the bytes to an object.
	/// </summary>
	/// <param name="bytes">The bytes that contain the object.</param>
	/// <returns>The object converted from the bytes.</returns>
	Object ToObject(byte[] bytes);
	Object ToObject(byte[] bytes, int offset, int length);
	
	

	/// <summary>
	/// Convert the object to bytes.
	/// </summary>
	/// <param name="value">The object to convert to bytes.</param>
	/// <returns>The bytes that contain the object.</returns>
	byte[] ToBytes(Object value);

   

	public Object clone();
}