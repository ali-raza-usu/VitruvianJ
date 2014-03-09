package vitruvianJ.logging;

import java.util.Random;

	
public class GUID {

	private String id = "";
	public GUID()
	{
		id = getGuidVal();		
	}
	
	
	public GUID(String guid_id)
	{
		this.id = guid_id; 
	}
	
	
	public GUID getGUID()
	{
		return new GUID(getGuidVal());
	}
	
	
	public GUID isEmpty()
	{
		GUID guid = new GUID();
		guid.id = "";
		return guid;
	}
	
	
	public String getGuidVal()
	{			
		String randString = "";		
		randString = getHexGroup(8) + "-" + getHexGroup(4)+ "-" + getHexGroup(4)+ "-" + getHexGroup(4)+ "-" + getHexGroup(12);
		return randString;	
	}
	
	public String getHexGroup(int groupSize)
	{
		String randString = "";
		
		Random rand = new Random();
		for(int i =0; i<groupSize; i++){
			int val = rand.nextInt(15);
				randString =randString + Integer.toHexString(val).toUpperCase();
		}		
		return randString;	
		
	}
	public String toString()
	{
		return id;
	}
}
