package vitruvianJ.persistence;

public class DirectoryEntry
{
	/// <summary>
	/// The root entry.  It has an entry id of '.'
	/// </summary>
	public static DirectoryEntry Root = new DirectoryEntry();

	/// <summary>
	/// The separator used by the ids.
	/// </summary>
	public static final char SEPARATOR = '.';

	private EntryType _entryType = EntryType.Directory;
	private String _id = "";

	/// <summary>
	/// Default Constructor.
	/// </summary>
	public DirectoryEntry()
	{}

	/// <summary>
	/// Construct a DirectoryEntry.
	/// </summary>
	/// <param name="type">The kind of entry.</param>
	/// <param name="id">The id of the entry.</param>
	public DirectoryEntry(EntryType type, String id)
	{
		_entryType = type;
		_id = id;
	}

	/// <summary>
	/// The kind of entry.
	/// </summary>
	
	public EntryType getEntryType()
	{
		 return _entryType;
	}
	
	public void setEntryType(EntryType value)
	{ 
		_entryType = value; 	
	}

	/// <summary>
	/// The id of the entry.
	/// </summary>
	public String getId()
	{
		return _id; 
	}
	public void setId(String value)
	{ _id = value; 
	}
}