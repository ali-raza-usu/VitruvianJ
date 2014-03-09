package vitruvianJ.serialization.xml;
import vitruvianJ.persistence.*;

import java.io.*;

public class XmlAdapter extends FileAdapter
{
	private static final String EXTENSION = "xml";

	/// <summary>
	/// Default Constructor
	/// </summary>
	public XmlAdapter() 
	{
		super(EXTENSION);
	}

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
		String filename = IdToFilename(entry);
		return XmlFramework.Deserialize(filename);
	}

	/// <summary>
	/// Load the object from the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to load the object from.</param>
	/// <param name="value">The object to load into.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public boolean Load(DirectoryEntry entry, Object value)
	{
		String filename = IdToFilename(entry);
		XmlFramework.Deserialize(filename, value);
		return true;
	}

	/// <summary>
	/// Store the object into the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to store the object.</param>
	/// <param name="value">The object to store.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public boolean Store(DirectoryEntry entry, Object value)
	{
		String filename = IdToFilename(entry);
		File file = new File(filename);
		
		String path = file.getParent();// Path.GetDirectoryName(filename);
		if (new File(path).exists())//!Directory.Exists(path))
			file.mkdir();//Directory.CreateDirectory(path);
		XmlFramework.Serialize(filename, value);
		return true;
	}
}