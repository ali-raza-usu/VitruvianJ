package vitruvianJ.persistence;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

import vitruvianJ.core.PathUtilities;


public abstract class FileAdapter implements IAdapter
{
	private List<Type> _requestors = new LinkedList<Type>();
	private String _basePath = "";
	private String _extension = "";

	/// <summary>
	/// Construct a FileAdapter
	/// </summary>
	/// <param name="extension">The filename extension to use.</param>
	public FileAdapter(String extension)
	{
		_extension = extension;
	}

	/// <summary>
	/// Change an entry to a path.
	/// </summary>
	/// <param name="entry">The entry to change.</param>
	/// <returns>An entry id.</returns>
	protected String IdToPath(DirectoryEntry entry)
	{
		String partialPath = entry.getId().replace(DirectoryEntry.SEPARATOR, File.separatorChar);
		return PathUtilities.GetAbsolutePath(_basePath, partialPath);
	}

	/// <summary>
	/// Change a path to an entry id.
	/// </summary>
	/// <param name="path">The path to change.</param>
	/// <returns>An entry id.</returns>
	protected String PathToId(String path)
	{
		String partialPath = PathUtilities.GetRelativePath(_basePath, path);
		return partialPath.replace(File.separatorChar, DirectoryEntry.SEPARATOR);
	}

	/// <summary>
	/// Change an entry to a filename.
	/// </summary>
	/// <param name="entry">The entry to change.</param>
	/// <returns>The filename.</returns>
	protected String IdToFilename(DirectoryEntry entry)
	{
		return String.format("{0}.{1}", IdToPath(entry), _extension);
	}

	/// <summary>
	/// Change a filename to an entry id.
	/// </summary>
	/// <param name="filename">The filename to change.</param>
	/// <returns>The entry id.</returns>
	
	protected String FilenameToId(String filename)
	{
		//String s[] = new File(filename).getName().split(".");
		//filename = // Path.GetFileNameWithoutExtension(filename);
		//string partialPath = PathUtilities.RelativePath(_basePath, filename);
		//return partialPath.Replace(Path.DirectorySeparatorChar, DirectoryEntry.SEPARATOR);
		return "";
	}
	 
	/// <summary>
	/// The objects that should use this adapter.
	/// </summary>
    //[TypeFormatter]
    //[Serialize]
    public List<Type> getRequestors()
	{
		 return _requestors; 
	}
    
    public void setRequestors(List<Type> value)
	{ 
    	_requestors = value; 
    }
	

	/// <summary>
	/// The base path of this adapter.
	/// All entries are relative to this path.
	/// </summary>
	//[AppPathFormatter]
    //[Serialize]
	public String getBasePath()
	{
		return _basePath; 
	}
	public void setBasePath(String value)
	{
		_basePath = value; 
	}

	/// <summary>
	/// Initialize the adapter.
	/// </summary>
	public void Init()
	{
		if (_basePath.equals(""))
			return;
		File directory = new File(_basePath);
		if (!directory.exists())
			directory.mkdir();
	}

	/// <summary>
	/// Cleanup the adapter.
	/// </summary>
	public void Cleanup()
	{
	}

	/// <summary>
	/// Get a relative listing based on the entry.
	/// </summary>
	/// <param name="entry">The entry to get a listing from.</param>
	/// <returns>A listing of directory information.</returns>
	String[] getDirectories(String path)
	{
		File directory = new File(path);
		File[] contents = directory.listFiles();
		ArrayList<String> subdirectories = new ArrayList<String>();
		for(int i =0; i< contents.length; i++)
		{
			if(contents[i].isDirectory())
				subdirectories.add(contents[i].getName());
		}
		return  subdirectories.toArray(new String[subdirectories.size()]);
		
	}
	String[] getDirectoryFiles(String path)
	{
		File directory = new File(path);
		File[] contents = directory.listFiles();
		ArrayList<String> subdirectories = new ArrayList<String>();
		for(int i =0; i< contents.length; i++)
		{
			if(!contents[i].isDirectory())
				subdirectories.add(contents[i].getName());
		}
		return  subdirectories.toArray(new String[subdirectories.size()]);
		
	}
	
	
	public List<DirectoryEntry> GetListing(DirectoryEntry entry)
	{
		List<DirectoryEntry> result = new LinkedList<DirectoryEntry>();

		if(entry.getEntryType().equals(EntryType.Directory))
		{
						
					String path = IdToPath(entry);

					// get the subdirectories
					String[] dirs =  getDirectories(path);// Directory.GetDirectories(path);
					for (int i = 0; i < dirs.length; i++)
					{
						DirectoryEntry rEntry = new DirectoryEntry();
						rEntry.setEntryType(EntryType.Directory);
						rEntry.setId(PathToId(dirs[i]));
						result.add(rEntry);
					}

					// get the filenames
					String[] filenames = getDirectoryFiles(path);// Directory.GetFiles(path);
					for (int i = 0; i < filenames.length; i++)
					{
						DirectoryEntry rEntry = new DirectoryEntry();
						rEntry.setEntryType(EntryType.Object);
						rEntry.setId(FilenameToId(filenames[i]));
						result.add(rEntry);
					}

			
				}
		/*
		else if(entry.getEntryType().equals(EntryType.Object))
				{
					break;
				}
			*/	
		

		return result;
	}

	/// <summary>
	/// Determine if this adapter should be used for the given object.
	/// </summary>
	/// <param name="value">The object that needs an adapter.</param>
	/// <returns>True if this is an appropriate adapter, otherwise False.</returns>
	public boolean IsAdapter(Object value)
	{
		return _requestors.contains(value.getClass());
	}

	/// <summary>
	/// Create a new entry.
	/// </summary>
	/// <param name="entry">The entry to create.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public boolean Create(DirectoryEntry entry)
	{
		if(entry.getEntryType().equals(EntryType.Directory))// switch (entry.EntryType)
		{
			
					String path = IdToPath(entry);
					File directory = new File(path);
					if (directory.exists())
						return true;

					directory.mkdir();
					return true;
		}
		else if(entry.getEntryType().equals(EntryType.Object))// switch (entry.EntryType)
				{
					String filename = IdToFilename(entry);
					return new File(filename).exists();
				}
		

		return false;
	}

	/// <summary>
	/// Delete the entry.
	/// </summary>
	/// <param name="entry">The entry to delete.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public boolean Delete(DirectoryEntry entry)
	{
		if(entry.getEntryType().equals(EntryType.Directory))//switch (entry.EntryType)
		{
			
					String path = IdToPath(entry);
					File directory = new File(path);
					directory.delete();
					return true;
		}
		else if(entry.getEntryType().equals(EntryType.Object))//switch (entry.EntryType)
		{
					String filename = IdToFilename(entry);
					File directory = new File(filename);
					directory.delete();
					return true;
		}		
		return false;
	}

	/// <summary>
	/// Determine if the entry exists.
	/// </summary>
	/// <param name="entry">The entry.</param>
	/// <returns>True if the entry exists, otherwise false.</returns>
	public boolean Exists(DirectoryEntry entry)
	{		
		if(entry.getEntryType().equals(EntryType.Directory))
				return  new File(IdToPath(entry)).exists();
		else if(entry.getEntryType().equals(EntryType.Object))
				return new File(IdToFilename(entry)).exists();		
		return false;
	}

	/// <summary>
	/// Move an entry.
	/// </summary>
	/// <param name="oldEntry">The position of the old entry.</param>
	/// <param name="newEntry">The desired position of the entry.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public boolean Move(DirectoryEntry oldEntry, DirectoryEntry newEntry) throws Exception
	{
		if (!oldEntry.getEntryType().equals(newEntry.getEntryType()))
			throw new Exception("Entry types do not match.");

		if(newEntry.getEntryType().equals(EntryType.Directory))
		{
					String oldPath = IdToPath(oldEntry);
					String newPath = IdToPath(newEntry);
					File oldDir = new File(oldPath);
					File newDir = new File(newPath);
					oldDir.renameTo(newDir);//.Move(oldPath, newPath);
					return true;
		}
		if(newEntry.getEntryType().equals(EntryType.Object))
		{
					String oldFilename = IdToFilename(oldEntry);
					String newFilename = IdToFilename(newEntry);
					File oldDir = new File(oldFilename);
					File newDir = new File(newFilename);
					oldDir.renameTo(newDir);//.Move(oldPath, newPath);
					//File.Move(oldFilename, newFilename);
					return true;
		}		
		return false;
	}

	/// <summary>
	/// Store the object into the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to store the object.</param>
	/// <param name="value">The object to store.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public abstract boolean Store(DirectoryEntry entry, Object value) throws Exception ;

	/// <summary>
	/// Append the object into the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to append the object.</param>
	/// <param name="value">The object to append.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public abstract boolean Append(DirectoryEntry entry, Object value) throws Exception;

	/// <summary>
	/// Load the object from the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to load the object from.</param>
	/// <param name="value">The object to load into.</param>
	/// <returns>True if successful, otherwise False.</returns>
	public abstract boolean Load(DirectoryEntry entry, Object value);

	/// <summary>
	/// Load the object from the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to load the object from.</param>
	/// <returns>The object that was loaded.</returns>
	public abstract Object Load(DirectoryEntry entry);
}