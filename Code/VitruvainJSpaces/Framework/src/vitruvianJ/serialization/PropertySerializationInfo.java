package vitruvianJ.serialization;
import java.lang.reflect.Field;


import vitruvianJ.serialization.formatters.Formatter;

public class PropertySerializationInfo
{
    /// <summary>
    /// The property info object.
    /// </summary>
	/// As property does not exisits in Java so, property will be used as field here.
    public Field Info = null;

    /// <summary>
    /// The formatter for this property info.
    /// </summary>
    public Formatter Formatter = null;

    /// <summary>
    /// Construct a PropertySerializationInfo object.
    /// </summary>
    /// <param name="info">The property info object.</param>
    /// <param name="formatter">The formatter used on this property info.</param>
    public PropertySerializationInfo(Field info, Formatter formatter)
    {
        Info = info;
        Formatter = formatter;
    }
}