package vitruvianJ.communication.session.sockets;

import java.util.ArrayList;

import vitruvianJ.core.PathUtilities;
import vitruvianJ.logging.*;
import vitruvianJ.serialization.Serialize;
import vitruvianJ.serialization.xml.XmlFramework;
import vitruvianJ.services.IService;

/// <summary>
/// An IP Table coming from Config\IpAddresses.xml file.
/// </summary>
public class IPTable implements IService
{
    static private JLogger _logger = new JLogger(IPTable.class);

	static private ArrayList<IPEntry> _entries = new ArrayList<IPEntry>();
    static private String _filePath = "";
    static private IPEndPointFormatter _formatter = new IPEndPointFormatter();

    /// <summary>
    /// Get the name of the endpoint.
    /// </summary>
    /// <param name="endPoint"></param>
    /// <returns></returns>
	static public String GetName(IPEndPoint endPoint)
	{
		for (int i = 0; i < _entries.size(); i++)
		{
			if (_entries.get(i).getEndPoint().equals(endPoint))
				return _entries.get(i).getName();
		}

        if (_logger.IsWarnEnabled())
            _logger.WarnFormat("Unable to find name for IPEndPoint %1s:%2s.  Using 'Address:Port' format.", endPoint.getIPAddress(), endPoint.getPort());

        return _formatter.Format(endPoint);
	}

    /// <summary>
    /// Get the endpoint from the name.
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
	static public IPEndPoint GetEndPoint(String name)
	{
        try
        {
            for (int i = 0; i < _entries.size(); i++)
            {
                if (_entries.get(i).getName().equals(name) )
                    return _entries.get(i).getEndPoint();
            }

//            if (_logger.IsWarnEnabled())
//                _logger.WarnFormat("Unable to find IPEndPoint for %1s.  Using 'Address:Port' format.", name);

            return (IPEndPoint)_formatter.Unformat(name);
        }
        catch(Exception e)
        {
            if (_logger.IsWarnEnabled())
                _logger.WarnFormat("Unable to create IPEndPoint for %1s.", name);
            
            return null;
        }
	}

	@Serialize//(getName = "get")
    //[AppPathFormatter]
    static public String getFilePath()
    {
        return _filePath; 
    }
    
	@Serialize//(getName = "set")
	static public void setFilePath(String value)
        {
            _filePath = value;
            XmlFramework.Deserialize(PathUtilities.GetAbsolutePath(_filePath), _entries);

//            if (_logger.IsDebugEnabled())
//            {
//                StringBuilder sb = new StringBuilder();
//                sb.append(String.format("Loaded configuration file %1s containing %2s entries.", _filePath, _entries.size()));
//
//                for(IPEntry entry : _entries)
//                    sb.append(String.format(" %1s -> %2s : %3s", entry.getName(), entry.getEndPoint().getIPAddress(), entry.getEndPoint().getPort()));
//
//                _logger.Debug(sb.toString());
//            }
        }
    

    
    private JGUID _id = new JGUID();

   

   

    

	@Override
	public void Cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JGUID getId() {
		// TODO Auto-generated method stub
		return _id; 
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "IP Table";
	}

	

   
}
