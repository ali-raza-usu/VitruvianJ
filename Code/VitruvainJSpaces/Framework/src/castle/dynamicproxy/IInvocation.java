package castle.dynamicproxy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface IInvocation {

	
	Object[] getArguments();//

	void setArguments(Object[] objects);
	
	
	Type[] getGenericArguments();

	
	Object getInvocationTarget();

	
	Method getMethod();// { get; }

	
	Method getMethodInvocationTarget();

	Object getProxy();

	Object getReturnValue();
	
	void setReturnValue(Object value);
	Type getTargetType();
	Object getArgumentValue(int index);
	Method GetConcreteMethod();

	/// <summary>
	/// Returns the concrete instantiation of <see cref = "MethodInvocationTarget" />, with any
	/// generic parameters bound to real types.
	/// For interface proxies, this will point to the <see cref = "MethodInfo" /> on the target class.
	/// </summary>
	/// <returns>The concrete instantiation of <see cref = "MethodInvocationTarget" />, or
	/// <see cref = "MethodInvocationTarget" /> if not a generic method.</returns>
	/// <remarks>
	/// In debug builds this can be slower than calling <see cref = "MethodInvocationTarget" />.
	/// </remarks>
	Method getConcreteMethodInvocationTarget();

	/// <summary>
	/// Proceeds the call to the next interceptor in line, and ultimately to the target method.
	/// </summary>
	/// <remarks>
	/// Since interface proxies without a target don't have the target implementation to proceed to,
	/// it is important, that the last interceptor does not call this method, otherwise a
	/// <see cref = "NotImplementedException" /> will be thrown.
	/// </remarks>
	void proceed();

	/// <summary>
	/// Overrides the value of an argument at the given <paramref name = "index" /> with the
	/// new <paramref name = "value" /> provided.
	/// </summary>
	/// <remarks>
	/// This method accepts an <see cref = "object" />, however the value provided must be compatible
	/// with the type of the argument defined on the method, otherwise an exception will be thrown.
	/// </remarks>
	/// <param name = "index">The index of the argument to override.</param>
	/// <param name = "value">The new value for the argument.</param>
	void setArgumentValue(int index, Object value);

}
