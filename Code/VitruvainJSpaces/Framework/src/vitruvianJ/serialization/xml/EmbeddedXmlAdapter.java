package vitruvianJ.serialization.xml;
import java.lang.reflect.Type;


import vitruvianJ.persistence.*;
public class EmbeddedXmlAdapter extends FileAdapter
{
	private static final String EXTENSION = "xml";
	private Type _baseType = null;

	/// <summary>
	/// Default Constructor
	/// </summary>
	public EmbeddedXmlAdapter()		
	{
		super(EXTENSION);
	}

	/// <summary>
	/// Construct an adapter giving the base type.
	/// </summary>
	/// <param name="baseType">The base type used as a reference for embedded resources.</param>
	public EmbeddedXmlAdapter(Type baseType)	
	{
		super(EXTENSION);
		_baseType = baseType;
	}

	/// <summary>
	/// The base type of the resource ids.
	/// </summary>
	//[TypeFormatter]
	//[Serialize]
    public Type getBaseType()
	{
    	return _baseType; 
    }
	public void	setBaseType(Type value){ _baseType = value; }
	

	/// <summary>
	/// Append the object into the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to append the object.</param>
	/// <param name="value">The object to append.</param>
	/// <returns>True if successful, otherwise False.</returns>
	/// <exception cref="NotImplementedException">Throws a NotImplementedException.</exception>
	public boolean Append(DirectoryEntry entry, Object value) throws Exception
	{
		throw new Exception("The method or operation is not implemented.");
	}

	/// <summary>
	/// Load the object from the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to load the object from.</param>
	/// <returns>The object that was loaded.</returns>
	public Object Load(DirectoryEntry entry)
	{
		return XmlFramework.Deserialize(_baseType, entry.getId());
	}

	/// <summary>
	/// Load the object from the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to load the object from.</param>
	/// <param name="value">The object to load into.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public boolean Load(DirectoryEntry entry, Object value)
	{
		XmlFramework.Deserialize(_baseType, entry.getId(), value);
		return true;
	}

	/// <summary>
	/// Store the object into the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to store the object.</param>
	/// <param name="value">The object to store.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public boolean Store(DirectoryEntry entry, Object value) throws Exception
	{
		throw new Exception("The method or operation is not implemented.");
	}
}