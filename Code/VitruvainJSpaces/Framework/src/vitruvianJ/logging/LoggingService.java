package vitruvianJ.logging;

import org.apache.log4j.*;

import vitruvianJ.serialization.IPFormatter;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.services.IService;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class LoggingService implements  IService {

	public LoggingService(){		

	}

	@Serialize
	public JGUID getId(){
		return new JGUID().getJGUID();
	}
	
	public String getName(){ 
		return "Logging Service";
	}
	
	
	
	public void Init(){
		//configure();
	}
	
	public void Cleanup(){}





	
}
