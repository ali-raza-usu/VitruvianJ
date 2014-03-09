package vitruvianJ.serialization;

import java.lang.reflect.*;


import vitruvianJ.serialization.formatters.Formatter;


public class TypeSerializationInfo
{
	
	
    /// <summary>
    /// The type this information is about.
    /// </summary>
    public Type _Type = null;

    /// <summary>
    /// The type's information for serialization.
    /// </summary>
    public SerializationInfo Serializable = new SerializationInfo();

    /// <summary>
    /// The type's information for deserialization.
    /// </summary>
    public SerializationInfo Deserializable = new SerializationInfo();

    /// <summary>
    /// Construct a type serialization object.
    /// </summary>
    /// <param name="type">The type this information is about.</param>
    /*
    public TypeSerializationInfo(Type type)
    {
        _Type = type;

        Class<?> type_val = (Class<?>)type;
        //PropertyInfo[] properties = type.GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance);
        Field[] fields =  type_val.getFields();// .getClass().getDeclaredFields();//GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance);                        
        Boolean isOptimistic = SerializationUtilities.IsOptimistic(type_val);

        

        for (int i = 0; i < fields.length; i++)
        {
            Formatter formatter = SerializationUtilities.GetFormatter(fields[i]);

            if (SerializationUtilities.IsSerializable(fields[i], isOptimistic))
            {
                FieldSerializationInfo info = new FieldSerializationInfo(fields[i], formatter);
                Serializable.Fields.add(info);
            }

            if (SerializationUtilities.IsDeserializable(fields[i], isOptimistic))
            {
                FieldSerializationInfo info = new FieldSerializationInfo(fields[i], formatter);
                Deserializable.Fields.add(info);
            }
        }

      

        Serializable.BuildMap();
        Deserializable.BuildMap();
    }
    
    */
    public TypeSerializationInfo(Type type)
    {
        _Type = type;

        Class<?> type_val = (Class<?>)type;
        //PropertyInfo[] properties = type.GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance);
        Method[] methods =  type_val.getMethods();// .getClass().getDeclaredFields();//GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.Instance);                        
        Boolean isOptimistic = SerializationUtilities.IsOptimistic(type_val);

        

        for (int i = 0; i < methods.length; i++)
        {
            Formatter formatter = SerializationUtilities.GetFormatter(methods[i]);

            if (SerializationUtilities.IsSerializable(methods[i], isOptimistic))
            {
                MethodSerializationInfo info = new MethodSerializationInfo(methods[i], formatter);
                Serializable.Methods.add(info);
            }

            if (SerializationUtilities.IsDeserializable(methods[i], isOptimistic))
            {
                MethodSerializationInfo info = new MethodSerializationInfo(methods[i], formatter);
                Deserializable.Methods.add(info);
            }
        }

      

        Serializable.BuildMap();
        Deserializable.BuildMap();
    }
    
}