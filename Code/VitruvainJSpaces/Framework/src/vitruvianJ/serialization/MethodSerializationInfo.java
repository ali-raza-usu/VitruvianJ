package vitruvianJ.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


import vitruvianJ.serialization.formatters.Formatter;
public class MethodSerializationInfo
{
    /// <summary>
    /// The field info object.
    /// </summary>
    public Method Info = null;

    /// <summary>
    /// The formatter for this field info.
    /// </summary>
    public Formatter Formatter = null;

    /// <summary>
    /// Construct a FieldSerializationInfo object.
    /// </summary>
    /// <param name="info">The field info object.</param>
    /// <param name="formatter">The formatter used on this field info.</param>
    public MethodSerializationInfo(Method info, Formatter formatter)
    {
        Info = info;
        Formatter = formatter;
    }
}