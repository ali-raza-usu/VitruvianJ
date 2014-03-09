package vitruvianJ.logging;

import java.io.File;
import java.net.URL;
import java.util.*;

import javax.swing.filechooser.FileSystemView;

public class PathUtilities {

	public static String ApplicationName = PathUtilities.GetDirectoryName();
	public static Map<Thread, List<String>> _threadFilenames = new  HashMap<Thread, List<String>>();
	
	
	public static String getExecutablePath()
	{
		
		String path = System.getProperty("user.dir");
		//URL u = JFileAppender.class.getResource("PathUtilities.class");
		//String path = new File(u.toString()).getParent();	
		return path;
	}
	
	public static String GetDirectoryName()
	{		
		String path = getExecutablePath();	
		//path = path.substring(6);				
		return path;		
	}

	
	
	
	
	public static String GetApplicationPath()
	{		
		String path =  getExecutablePath();	
		//path = path.substring(6);				
		return path;		
	}
	
	public static String GetFilename()
	{
		Thread thread = Thread.currentThread();
		if (!_threadFilenames.containsKey(thread))
			return "";

		List<String> filenames = _threadFilenames.get(thread);
		return filenames.get(filenames.size() - 1);
	}
	
	public static void PushFilename(String filename)
	{
		Thread thread = Thread.currentThread();
		if (!_threadFilenames.containsKey(thread))
			_threadFilenames.put(thread, new LinkedList<String>());

		_threadFilenames.get(thread).add(filename);		
	}

	public static void PopFilename(String filename)
	{
		Thread thread = Thread.currentThread();
		if (!_threadFilenames.containsKey(thread))
			return;

		List<String> filenames = _threadFilenames.get(thread);
		filenames.remove(filename);
		if (filenames.size() == 0)
			_threadFilenames.remove(thread);
	}
	
	
	public static String GetRelativePath(String filePath)
	{
		return GetRelativePath(GetApplicationPath(), filePath);		
	}
	
	public static String GetRelativePath(String basePath, String filePath)
	{
		if ((filePath == null) ||(filePath == ""))
				return "";

			String[] baseArr = basePath.split(Character.toString(File.separatorChar));// Path.DirectorySeparatorChar);
			String[] fileArr = filePath.split(Character.toString(File.separatorChar));

			// Check the directory
			if (baseArr[0] != fileArr[0])
				return filePath;

			int fileLength = fileArr.length;
			int baseLength = baseArr.length;

			while (fileLength > 0 && fileArr[fileLength - 1].trim() == "")
				fileLength--;

			while (baseLength > 0 && baseArr[baseLength - 1].trim() == "")
				baseLength--;

			int length = Math.min(fileLength, baseLength);

			String relativePath = "";

			int index;
			for (index = 0; index < length; index++)
			{
				// if the two strings are not equal then quit
				if (fileArr[index].compareToIgnoreCase(baseArr[index]) != 0)
					break;
			}

			for (int j = index; j < fileLength; j++)
			{
				if (fileArr[j] != "")
					relativePath += fileArr[j] + ((j != fileLength - 1) ? Character.toString(File.separatorChar) : "");
			}

			for (int k = index; k < baseLength; k++)
				relativePath = ".." + File.pathSeparatorChar + relativePath;

			relativePath = "." + File.separatorChar + relativePath;

			return relativePath;
		
	}
	
	static boolean IsPathRooted(String path)
	{
		boolean flag = false;
		return flag;
	}
	
	public static String GetAbsolutePath(String basePath, String relativePath)
	{
		if ((relativePath == null) ||
			    (relativePath == ""))
				return basePath;

			// If the path is already rooted, it is absolute.
			if (IsPathRooted(relativePath))
				return relativePath;

			// Trim off the leading "current directory" token
			if (relativePath.startsWith(".")) //if (relativePath.StartsWith(@".\"))
				relativePath = relativePath.substring(2);

			// Check to see if we're already done
			if (relativePath == "")
				return basePath;

			// Trim off the trailing \ from the base path if it exists
			if (basePath.endsWith("\\")) //			if (basePath.EndsWith(@"\"))
				basePath = basePath.substring(0, basePath.length() - 1);

			int startIndex = 0;
			while ((startIndex < relativePath.length()) &&
			       (relativePath.substring(startIndex, 3) == (".." + File.separatorChar)))
			{
				// Move the base up one level in response to the "previous directory" token
				File di = new File(basePath).getParentFile();

				if (di == null)
					return new File(relativePath).getName();

				basePath =  di.getPath();
				startIndex += 3;
				
			}
			
			return   basePath+"\\"+ relativePath.substring(startIndex);			
	}
	
	
	
	
	
	
	public static String GetAbsolutePath(String filePath)
	{
		return GetAbsolutePath(GetApplicationPath(), filePath);
	}
}
