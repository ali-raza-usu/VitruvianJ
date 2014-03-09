package vitruvianJ.serialization.xml;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class XmlDeserializerTest extends TestCase {

	public void testFindCompatibleMethodForClassInterface()
	{
		XmlDeserializer deserializer = new XmlDeserializer();
		
		Class requiredMethodParameterType = null;
		Method method =  new Class2().getClass().getMethods()[0];			
		requiredMethodParameterType = method.getParameterTypes()[0];				
		assertEquals(requiredMethodParameterType,deserializer.FindCompatibleMethodForClassInterface(new Class2().getClass(), new Class1().getClass(), "setClass1"));		
		assertEquals(new Class3().getClass(),deserializer.FindCompatibleMethodForClassInterface(new Class2().getClass(), new Class3().getClass(), "setClass1"));		
		assertEquals(requiredMethodParameterType,deserializer.FindCompatibleMethodForClassInterface(new Class2().getClass(), new Class4().getClass(), "setClass1"));
				
	}
	
	
	interface abc
	{
		public void method1();
	}
	
	interface cde extends abc
	{		
	}
	
	
	class Class1 implements abc
	{
		public void method1()
		{
			
		}
	}
	
	class Class2
	{
		
		public void setClass1(abc param){
			
		}
	}
	
	class Class3
	{
		public void method1()
		{
			
		}
	}
	
	class Class4 implements cde
	{

		@Override
		public void method1() {
			// TODO Auto-generated method stub
			
		}
		
		
	}
}
