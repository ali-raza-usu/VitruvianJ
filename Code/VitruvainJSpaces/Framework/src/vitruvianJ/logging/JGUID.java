package vitruvianJ.logging;

import java.util.Random;
import java.util.UUID;

public class JGUID {
	private String id = "";
	public JGUID()
	{
		id = getJGuidVal();		
	}
	
	
	public JGUID(String guid_id)
	{
		this.id = guid_id; 
	}
	
	
	public JGUID(JGUID guid)
	{
		this.id = guid.toString(); 
	}
	
	public JGUID getJGUID()
	{
		return new JGUID(getJGuidVal());
	}
	
	
	public JGUID isEmpty()
	{
		JGUID guid = new JGUID();
		guid.id = "";
		return guid;
	}
	
	
	public String getJGuidVal()
	{			
		UUID unique_id = UUID.randomUUID();
		
		return unique_id.toString();	
	}
	
	public String toString()
	{
		return id;
	}
}
