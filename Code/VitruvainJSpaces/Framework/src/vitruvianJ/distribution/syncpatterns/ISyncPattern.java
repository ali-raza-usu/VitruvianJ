package vitruvianJ.distribution.syncpatterns;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import vitruvianJ.distribution.proxies.ISyncProxy;

public interface ISyncPattern
{
	/// <summary>
	/// Initialize the sync pattern.
	/// At least one method info will be null, and at least one will not be null.
	/// </summary>
	/// <param name="parent">The proxy parent object.</param>
	/// <param name="method">The method to call. Can be null.</param>
	/// <param name="property">The property to use. Can be null.</param>
    void Init(ISyncProxy proxy, Method method, Field property);

	/// <summary>
	/// Start the sync pattern
	/// </summary>
	void Start();

	/// <summary>
	/// Stop the sync pattern
	/// </summary>
	void Stop();

    /// <summary>
    /// The ISyncProxy the pattern is bound to.
    /// </summary>
    ISyncProxy getProxy();
    

	/// <summary>
	/// Handle the method as a proxy for the parent.
	/// </summary>
	/// <param name="args">The arguments to the method.</param>
	/// <returns>The result of the function.  Returning null is ok.</returns>
	Object HandleMethod(Object... args);

	/// <summary>
	/// Handle the property get as a proxy for the parent.
	/// </summary>
	/// <param name="args">The arguments to the method.</param>
	/// <returns>The value from the property.</returns>
    Object HandlePropertyGet(Object... args);

	/// <summary>
	/// Handle the property set as a proxy for the parent.
	/// </summary>
	/// <param name="args">The arguments to the method.</param>
    Object HandlePropertySet(Object... args);
}
