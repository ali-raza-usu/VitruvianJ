package vitruvianJ.distribution.proxies;

import java.util.List;

import vitruvianJ.logging.*;

public interface IRemoteSyncProxy extends ISyncProxy
{
    boolean IsInitialized();
    void setInitialized(boolean value);
   
    List<JGUID> getBrokers();
    
    JGUID getPreferredBroker(); //JGUID? PreferredBroker
    
    void AddBroker(JGUID brokerId);

    void RemoveBroker(JGUID brokerId);
}