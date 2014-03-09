package vitruvianJ.communication.session.protocols;

public class PendingProtocol
{
    private IProtocol _protocol = null;
    private long _expiration = 0;

    /// <summary>
    /// Construct a PendingProtocol
    /// </summary>
    /// <param name="protocol">The protocol to encapsulate.</param>
    /// <param name="expiration">The expiration time of the protocol.</param>
    public PendingProtocol(IProtocol protocol, long expiration)
    {
        _protocol = protocol;
        _expiration = expiration;
    }

    /// <summary>
    /// The encapsulated protocol.
    /// </summary>
    public IProtocol getProtocol()
    {
        return _protocol; 
    }

    /// <summary>
    /// The expiration time of the protocol.
    /// </summary>
    public long getExpiration()
    {
        return _expiration; 
    }
    
    public void setExpiration(long value) 
    { 
    	_expiration = value; 
    }
    
}
