package vitruvianJ.distribution;
import java.lang.ref.*;
import java.lang.reflect.*;

import vitruvianJ.distribution.proxies.ISyncProxy;

/// <summary>
/// A pattern that synchronizes data over a distributed environment.
/// </summary>
public interface ISyncPattern
{
	/// <summary>
	/// Initialize the sync pattern.
	/// At least one method info will be null, and at least one will not be null.
	/// </summary>
	/// <param name="parent">The proxy parent object.</param>
	/// <param name="method">The method to call. Can be null.</param>
	/// <param name="propertyGet">The property to get. Can be null.</param>
	/// <param name="propertySet">The property to set.  Can be null.</param>
	public void Init(ISyncProxy proxy, Method method, Field propertyGet);

	/// <summary>
	/// Start the sync pattern
	/// </summary>
	public void Start();

	/// <summary>
	/// Stop the sync pattern
	/// </summary>
	public void Stop();

	public ISyncProxy getProxy();
    
	
	/// <summary>
	/// Handle the method as a proxy for the parent.
	/// </summary>
	/// <param name="args">The arguments to the method.</param>
	/// <returns>The result of the function.  Returning null is ok.</returns>
	public Object HandleMethod(Object... args);

	/// <summary>
	/// Handle the property get as a proxy for the parent.
	/// </summary>
	/// <param name="args">The arguments to the method.</param>
	/// <returns>The value from the property.</returns>
	public Object HandlePropertyGet(Object... args);

	/// <summary>
	/// Handle the property set as a proxy for the parent.
	/// </summary>
	/// <param name="args">The arguments to the method.</param>
	public Object HandlePropertySet(Object... args);
}