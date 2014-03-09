package vitruvianJ.binary;


import static java.lang.System.out;

import java.lang.reflect.Array;

import junit.framework.Assert;

import org.junit.Before;

public class BinaryDeserializerTest {

	@Before
	public void setUp() throws Exception {
	}
	
	public void testSetArrayVal()
	{		
	        Object matrix = Array.newInstance(int.class, 2, 2,2);
	        int[] index = new int[]{0 ,1,1};
	        int val = 2;	       
	        matrix = setArrayVal(matrix, index, val);
	        for (int i = 0; i < 2; i++)
	            for (int j = 0; j < 2; j++)
	            	for (int k = 0; k < 2; k++)
	                out.format("matrix[%d][%d][%d] = %d%n", i, j, k, ((int[][][])matrix)[i][j][k]);	    
	}
	
	
	public void testGetArrayVal()
	{		
	        Object matrix = Array.newInstance(int.class, 2, 2,2);
	        int[] index = new int[]{0 ,1,1};
	        int val = 2;	       
	        matrix = setArrayVal(matrix, index, val);
	        Assert.assertEquals(2, getArrayVal(matrix,index));
	        for (int i = 0; i < 2; i++)
	            for (int j = 0; j < 2; j++)
	            	for (int k = 0; k < 2; k++)
	                out.format("matrix[%d][%d][%d] = %d%n", i, j, k, ((int[][][])matrix)[i][j][k]);	    
	}
	
	
	static Object setArrayVal(Object array, int[] index, int val)
    {
    	Object element = new Object();
    	element = array;
    	for(int i =0; i<index.length-1; i++)
        {        	        
        	element = Array.get(element, index[i]);	        	        
        }     
        Array.setInt(element, index[index.length-1], val);       
    	return array;
    }

	static int getArrayVal(Object array, int[] index)
    {
    	Object element = new Object();
    	element = array;
    	for(int i =0; i<index.length; i++)
        {        	        
        	element = Array.get(element, index[i]);	        	        
        }     
               
    	return ((Integer)element).intValue();
    }
}
