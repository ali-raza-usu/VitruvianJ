package vitruvianJ.serialization.formatters;

import java.io.File;

import vitruvianJ.core.PathUtilities;


public class PathFormatter extends Formatter {

	@Override
	public String Format(Object value) {
		String directory = "";//System.getProperty("user.dir");
		String filename = PathUtilities.GetFilename();
		File file = new File(filename);
		directory  = file.getParent();
		return PathUtilities.GetRelativePath(directory, value.toString());		
	}

	@Override
	public Object Unformat(String value) {
		
		String directory = System.getProperty("user.dir");
		return PathUtilities.GetAbsolutePath(directory, value.toString());
	}

}
