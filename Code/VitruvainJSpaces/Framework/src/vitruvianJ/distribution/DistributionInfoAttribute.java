package vitruvianJ.distribution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target ({ElementType.TYPE })
public @interface DistributionInfoAttribute {
 boolean getMigratable();
}

//public class DistributionInfoAttribute //: Attribute
//{
//    private boolean _migratable = false;
//
//    /// <summary>
//    /// Construct a DistributionInfoAttribute.
//    /// </summary>
//    public DistributionInfoAttribute()
//    {
//    }
//
//    /// <summary>
//    /// Identifies if the proxy class is migratable.
//    /// </summary>
//    public boolean getMigratable()
//    {
//       return _migratable; 
//    }
//    
//    public void setMigratable(boolean value) { _migratable = value; }
//   
//}
