package vitruvianJ.distribution.proxies;

import java.lang.reflect.Member;

import vitruvianJ.distribution.ISyncPattern;
import vitruvianJ.logging.JGUID;

public interface ISyncProxy {

	public JGUID getProxyId();
	public ISyncPattern getSyncPattern(Member memberInfo);
	public void StartSyncPatterns();
	public void StopSyncPatterns();
}
