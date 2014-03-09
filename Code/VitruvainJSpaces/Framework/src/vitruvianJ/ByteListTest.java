 package vitruvianJ;

import static org.junit.Assert.*;



import org.junit.Before;
import org.junit.Test;



public class ByteListTest{

	ByteList _bList;
	@Before
	public void setUp() throws Exception {
		_bList = new ByteList();
	}
	
	@Test
	public void testByteList() {
		
		ByteList byteList = new ByteList();
		try{
		assertEquals((byteList.GetSection(0)).length, 1024);
		}catch(Exception e){}
	}

	@Test
	public void testByteListObjectArray() {
		try{			
		ByteList byteList = new ByteList(true,2,3.5);		
		assertEquals(false, byteList.GetBool(0));
		}catch(Exception e){}
	}

	@Test
	public void testGetThis() {
		
		fail("Not yet implemented");
	}

	@Test
	public void testSetThis() {
		try{
			
		 _bList.setThis(3);		
		 byte[] b = _bList.getSections().get(3);
		 assertNotNull(b.length);
		 //_bList.getS
		
		}catch(Exception e){}
		//fail("Not yet implemented");
	}

	@Test
	public void testClear() {
		try{
		_bList = new ByteList();
		_bList.setThis(3);				
		 _bList.Clear();
		 int size = _bList.getSections().size();
		 assertEquals(size, 0);
		}catch(Exception e){}
	}

	@Test
	public void testGetLength() {
		try{
		_bList = new ByteList();
		_bList.setThis(3);	
		int length = _bList.getLength();
		assertEquals(length, 0);
		}catch(Exception e){}
	}

	@Test
	public void testAddByteList() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddByte() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddChar() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddFloat() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddStringBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddString() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testBlockCopy() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddByteArrayIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetByteList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetByte() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBool() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetChar() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetShort() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFloat() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStringIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStringInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetArrayObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBytes() {
		fail("Not yet implemented");
	}

	@Test
	public void testToBytes() {
		fail("Not yet implemented");
	}

	@Test
	public void testFromBytes() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPrimitives() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetUInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStringByteArrayIntBoolean() {
		fail("Not yet implemented");
	}

}
