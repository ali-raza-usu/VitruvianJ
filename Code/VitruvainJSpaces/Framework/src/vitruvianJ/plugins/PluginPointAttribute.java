package vitruvianJ.plugins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

@Retention(RetentionPolicy.RUNTIME)
@Target ({ElementType.FIELD , ElementType.METHOD})
	public @interface PluginPointAttribute
	{
		public String id();
		public Type _requireType = null;		
		
	}
