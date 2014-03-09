package vitruvianJ.persistence;

import java.util.List;

public interface IAdapter
{
	/// <summary>
	/// Initialize the adapter.
	/// </summary>
	void Init();

	/// <summary>
	/// Cleanup the adapter.
	/// </summary>
	void Cleanup();

	/// <summary>
	/// Get a relative listing based on the entry.
	/// </summary>
	/// <param name="entry">The entry to get a listing from.</param>
	/// <returns>A listing of directory information.</returns>
	List<DirectoryEntry> GetListing(DirectoryEntry entry);

	/// <summary>
	/// Determine if this adapter should be used for the given object.
	/// </summary>
	/// <param name="value">The object that needs an adapter.</param>
	/// <returns>True if this is an appropriate adapter, otherwise False.</returns>
	boolean IsAdapter(Object value);

	/// <summary>
	/// Create a new entry.
	/// </summary>
	/// <param name="entry">The entry to create.</param>
	/// <returns>True if successful, otherwise False.</returns>
	boolean Create(DirectoryEntry entry);

	/// <summary>
	/// Delete the entry.
	/// </summary>
	/// <param name="entry">The entry to delete.</param>
	/// <returns>True if successful, otherwise False.</returns>
	boolean Delete(DirectoryEntry entry);

	/// <summary>
	/// Determine if the entry exists.
	/// </summary>
	/// <param name="entry">The entry.</param>
	/// <returns>True if the entry exists, otherwise false.</returns>
	boolean Exists(DirectoryEntry entry);

	/// <summary>
	/// Move an entry.
	/// </summary>
	/// <param name="oldEntry">The position of the old entry.</param>
	/// <param name="newEntry">The desired position of the entry.</param>
	/// <returns>True if successful, otherwise False.</returns>
	boolean Move(DirectoryEntry oldEntry, DirectoryEntry newEntry) throws Exception;

	/// <summary>
	/// Store the object into the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to store the object.</param>
	/// <param name="value">The object to store.</param>
	/// <returns>True if successful, otherwise False.</returns>
	boolean Store(DirectoryEntry entry, Object value) throws Exception;

	/// <summary>
	/// Append the object into the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to append the object.</param>
	/// <param name="value">The object to append.</param>
	/// <returns>True if successful, otherwise False.</returns>
	boolean Append(DirectoryEntry entry, Object value) throws Exception;

	/// <summary>
	/// Load the object from the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to load the object from.</param>
	/// <param name="value">The object to load into.</param>
	/// <returns>True if successful, otherwise False.</returns>
	boolean Load(DirectoryEntry entry, Object value);

	/// <summary>
	/// Load the object from the given entry.
	/// </summary>
	/// <param name="entry">The entry indicating where to load the object from.</param>
	/// <returns>The object that was loaded.</returns>
	Object Load(DirectoryEntry entry);
}
