package vitruvianJ.distribution.proxies;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import vitruvianJ.distribution.DistributionInfoAttribute;
import vitruvianJ.distribution.syncpatterns.SyncPatternAttribute;
import vitruvianJ.logging.JLogger;

public class ReflectionInfo
{
	JLogger _logger = new JLogger(ReflectionInfo.class);
    static private Dictionary<Type, ReflectionInfo> _reflectionInfo = new Hashtable<Type, ReflectionInfo>();

    static public ReflectionInfo getReflectionInfo(Type type)
    {
        synchronized (_reflectionInfo)
        {
        	 
            Type nonProxyType = ProxyUtilities.getNonProxyBaseType(type);

            if (_reflectionInfo.get(nonProxyType) == null){
            	ReflectionInfo ref_info  =  new ReflectionInfo(nonProxyType);
                _reflectionInfo.put(nonProxyType, ref_info);
            }
            return _reflectionInfo.get(nonProxyType);
        }
    }

    private Type _type = null;

    private Dictionary<Member, String> _membersToPatterns = new Hashtable<Member, String>();
    private List<Member> _members = new ArrayList<Member>();

    private boolean _generateProxy = true;
    private boolean _isMigratable = false;

    private ReflectionInfo(Type type)
    {
    	int a = 0;
    	Class c = (Class)type;
    	if(c.getName().contains("SharedServices"))
    	{
    		a = 4;
    	}
        _type = type;
        _generateProxy = GetGenerateProxy();

        Method[] methods = ((Class)_type).getMethods();//GetMethods(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.FlattenHierarchy | BindingFlags.Static | BindingFlags.Instance);
        Field[] fields = ((Class)_type).getFields();//GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.FlattenHierarchy | BindingFlags.Static | BindingFlags.Instance);

        _members.addAll(Arrays.asList(methods));
        _members.addAll(Arrays.asList(fields));
        
        
        if ( ((Class)type).getName().contains("SharedService"))
        {
            _members.set(8, _members.get(21));
            _members.set(14, _members.get(11));
            Member getGossip = _members.get(17);
            Member setGossip = _members.get(18);
            _members.set(17, _members.get(3));
            _members.set(18, _members.get(4));
            _members.set(3, getGossip);
            _members.set(4, setGossip);
            
            int i  =0;
            for (Member field : _members) {
				_logger.Debug("[index ]"+ i++ +" : name : " + field.getName() + " Type : " + field.getDeclaringClass());
			}

        }
            
        if (_generateProxy)
        {
            _isMigratable = GetIsMigratable();

            for(Method method : methods)
            {
                SyncPatternAttribute attrib = GetSyncPattern(method);

                if (attrib != null)
                {
                    _membersToPatterns.put(method, attrib.getSyncPatternId());
                }
            }

            for (Field field : fields)
            {
                SyncPatternAttribute attrib = GetSyncPattern(field);

                if (attrib != null)
                {
                    _membersToPatterns.put(field, attrib.getSyncPatternId());
                }
            }
        }
    }

    public boolean getGenerateProxy()
    {
        return _generateProxy; 
    }
    public void setGenerateProxy(boolean value) 
    { 
    	_generateProxy = value; 
    }
    

    public boolean getIsMigratable()
    {
        return _isMigratable; 
    }
    public void setIsMigratable(boolean value) 
    { 
    	_isMigratable = value; 
    }
    

    public Dictionary<Member, String> getMembersToPatterns()
    {
        return _membersToPatterns;
    }

    public Member GetMember(int index)
    {
        return _members.get(index);
    }

    public int GetMemberIndex(Member mInfo)
    {
        return _members.indexOf(mInfo);
    }

    private boolean GetGenerateProxy()
    {
    	DistributionInfoAttribute[] attribs = null;
    	Class temp_class = (Class)_type;
    	
    	Annotation[] annotations = temp_class.getAnnotations();
    	
    	if(annotations != null && annotations.length >0)
    		if(annotations[0] instanceof DistributionInfoAttribute )
    			return true;
    		else
    			return false;
    		return false;
    }

    private boolean GetIsMigratable()
    {
    	Class temp_class = (Class)_type;
    	Annotation[] annotations = temp_class.getAnnotations();
        //DistributionInfoAttribute[] attribs = (DistributionInfoAttribute[])_type.getClass().getAnnotations();// GetCustomAttributes(DistributionInfoAttribute.class, false);
        if(annotations!= null && annotations.length >0)
        if (annotations[0] instanceof DistributionInfoAttribute)
            return ((DistributionInfoAttribute)annotations[0]).getMigratable();
        else
            return false;
        return false;
    }

    /// <summary>
    /// Find the SyncPatternAttribute on the method
    /// information and return the associated SyncPatternAttribute.
    /// </summary>
    /// <param name="mInfo">The method that possibly contains a SyncPatternAttribute.</param>
    /// <returns>The SyncPatternAttribute if one exists, otherwise null.</returns>
    private SyncPatternAttribute GetSyncPattern(Method mInfo)
    {
        if (mInfo == null)
            return null;

        Annotation[] attribs = mInfo.getAnnotations();// GetCustomAttributes(SyncPatternAttribute.class, false);
        
        if ((attribs == null) || (attribs.length == 0))
            return null;
        
        if(attribs.length > 0 && (attribs[0] instanceof SyncPatternAttribute) )
        	return ((SyncPatternAttribute)attribs[0]);
		return null;
    }

    /// <summary>
    /// Find the SyncPatternAttribute on the property
    /// information and return the associated SyncPatternAttribute.
    /// </summary>
    /// <param name="pInfo">The property that possibly contians a SyncPatternAttribute.</param>
    /// <returns>The SyncPatternAttribute if one exists, otherwise null.</returns>
    private SyncPatternAttribute GetSyncPattern(Field fInfo)
    {
        if (fInfo == null)
            return null;

        Annotation[] annotations = fInfo.getAnnotations();//getAnnotation(SyncPatternAttribute.class);

        if ((annotations == null) || (annotations.length == 0))
            return null;

        if(annotations.length > 0 && (annotations[0] instanceof SyncPatternAttribute) )
        	return ((SyncPatternAttribute)annotations[0]);
		return null;
        
    }
}