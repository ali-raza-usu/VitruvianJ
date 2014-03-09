package vitruvianJ.serialization.formatters;




public abstract class Formatter {

	
	public abstract String Format(Object value);

	public abstract Object Unformat(String value);
}
