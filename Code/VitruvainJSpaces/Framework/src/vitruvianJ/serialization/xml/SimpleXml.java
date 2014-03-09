package vitruvianJ.serialization.xml;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})//fields can only be serialized but not deserialized at the moment, as there is no extra data in the smiple xml format to differentiate between a method and a field during deserialization
public @interface SimpleXml {
	String name();
}
