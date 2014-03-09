package vitruvianJ.services;

import vitruvianJ.logging.JGUID;



public interface IService {
	
	public JGUID getId();

	
	public String getName();

	public void Init();

	public void Cleanup();
}
