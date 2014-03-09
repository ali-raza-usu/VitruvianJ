package vitruvianJ.serialization;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target ({ElementType.FIELD , ElementType.METHOD})
public @interface IPFormatter {	
	 String getFormatter();
}
