package vitruvianJ.distribution.encoders;

import vitruvianJ.logging.JGUID;
import vitruvianJ.serialization.IEncoder;

abstract public class ObjectEncoder implements IEncoder
{
    private JGUID _brokerId = new JGUID();

    public JGUID getBrokerId()
    {
        return _brokerId; 
    }
    public void setBrokerId(JGUID value) 
    { 
    	_brokerId = value;
    }

    abstract public void Init();

    abstract public void Cleanup();

    abstract public Object ToObject(byte[] bytes, int offset, int length);

    abstract public byte[] ToBytes(Object value);

    abstract public Object clone();

}
