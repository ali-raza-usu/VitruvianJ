package castle.dynamicproxy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;


// Independent, like delegate

public class IndirectInvocation implements IInvocation{

	Object[] myObjects = null;

	@Override
	public void proceed() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Object getReturnValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReturnValue(Object value) {
		// TODO Auto-generated method stub	
	}

	@Override
	public Object getProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	//********************************************************************************
	//Other methods are never getting used.
	//********************************************************************************

	@Override
	public Object[] getArguments() {
		// TODO Auto-generated method stub
		return myObjects;
	}

	@Override
	public Type[] getGenericArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getInvocationTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method getMethodInvocationTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getTargetType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getArgumentValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method GetConcreteMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method getConcreteMethodInvocationTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setArgumentValue(int index, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setArguments(Object[] objects) {
		
		myObjects = objects;
	}

}
