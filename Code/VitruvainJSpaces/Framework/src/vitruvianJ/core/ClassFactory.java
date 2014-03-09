package vitruvianJ.core;


import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import vitruvianJ.logging.*;
public class ClassFactory
	{
		private static JLogger _logger = new JLogger(ClassFactory.class);
		private static List<Class<?>> _assemblies = new LinkedList<Class<?>>();
		private static List<String> _staticAssemblies = new LinkedList<String>();
		private static ArrayList<String> className = new ArrayList<String>();
		
		
		public ClassFactory()
		{
			File f= new File(PathUtilities.GetDirectoryName());			
			String[] files = f.list();
						
			
			for (int i = 0; i < files.length; i++){
				String names[] = files[i].split(".");
				_staticAssemblies.add(names[0]);
			}
		}

	
				
		public static void recursiveTraversal(File fileObject){		
	        if (fileObject.isDirectory()){
	            
	            File allFiles[] = fileObject.listFiles();
	            for(File aFile : allFiles){
	                recursiveTraversal(aFile);
	        }
	        }else if (fileObject.isFile()){
	        	if(fileObject.getName().endsWith("class"))
				{
	        		String path =fileObject.getPath();
	        		String abs_path =fileObject.getAbsolutePath();
	        		String canonical_name =fileObject.getPath();
	        		String name =fileObject.getName(); 
					className.add(path);
				}			
	        }		
	    }


		
		static void AddAssembly(Class<?> assembly)
		{
//			if (_logger.IsDebugEnabled())
//				_logger.DebugFormat("ClassFactory is adding assembly {0}.", assembly.getName());
			_assemblies.add(assembly);
		}

		static Class<?> GetAssembly(String assemblyPath)
		{
			for(Class<?> assembly : _assemblies)
			//foreach (Assembly assembly in _assemblies)
			{
				String path = assembly.getClass().getResource(assembly.getName()).getPath();
				if (path.equals(assemblyPath))
					return assembly;
			}

			return null;
		}

		private static List<Object> getAllClasses()
		{
			List<Object> objects = new ArrayList<Object>();
			recursiveTraversal(new File(PathUtilities.getExecutablePath()) );
			for(String name : className)
			{
				try {			
					Class my_class= Class.forName(name);
					Object my_object = my_class.newInstance();
					objects.add(Class.forName(name).newInstance());					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println(name);
				}
			}
			return objects;
		}
		
		 public static List<Class> GetAllTypes()
	        {
	            List<Class> result = new ArrayList<Class>();

	            List<Object> assemblies = new ArrayList<Object>();
	            assemblies = getAllClasses();//.AddRange(AppDomain.CurrentDomain.GetAssemblies());

	            //string exePath = Path.GetDirectoryName(PathUtilities.ExecutablePath);
	            String exePath =  PathUtilities.getExecutablePath();
	            File file = new File(exePath);
	            
	            String[] files = file.list();// Directory.GetFiles(exePath, "*.class", SearchOption.AllDirectories);
	            for (String filename : files)
	            {
	                boolean isLoaded = false;
	                for (Object assembly : assemblies)
	                {
	                	String name = PathUtilities.getFileNameWithoutExtension(filename);
	                    if (assembly.getClass().getName().equals(name) )
	                    {
	                        isLoaded = true;
	                        break;
	                    }
	                }

	                if (!isLoaded)
	                {
	                    Class assembly = null;
						try {
							assembly = Class.forName(file.getName());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    
	                    assemblies.add(assembly);
	                    AddAssembly(assembly);
	                }
	            }

	            for(Object assembly : assemblies)
	            {
	                try
	                {
	                    result.add(assembly.getClass());
	                }
	                catch(Exception e)
	                { }
	            }

	            return result;
	        }
		 
		 
		 
		static Boolean IsAssemblyLoaded(String assemblyName)
		{
			//foreach (string name in _staticAssemblies)
			for(String name: _staticAssemblies)
			{
				if (name.equals(assemblyName))
					return true;
			}

			//foreach (Assembly assembly in _assemblies)
			/*
			for(Assembly assembly :_assemblies)
			{
				if (assembly.GetName().Name. == assemblyName)
					return true;
			}
			*/

			return false;
		}

		public static Type FindType(String type)// throws Exception
		{
			try{
			Type result;

			result = Class.forName(type);// type.getClass();// type.getClass();// Type.GetType(type, false, false);
			if (result != null)
				return result;

            // Split the type into [typeName | assemblyName]
			String[] typeInfo = type.split(",");
           

            String assemblyName = "";
            if (typeInfo.length > 1)
                assemblyName = typeInfo[1];

			for(Class<?> assembly:_assemblies)
			{
                // skip this assembly if it doesn't have the right name
                if (!assemblyName.equals(""))
                {
                    if (!assembly.getName().equals(assemblyName))
                        continue;
                }

                result = assembly.getClass();// .GetType(typeName, false, false);
				if (result != null)
				{
//                    if (_logger.IsDebugEnabled())
//					    _logger.DebugFormat("Loaded type '{0}' from dynamic assembly.", type);
					return result;
				}
			}
/*
            if (_logger.IsErrorEnabled)
            {
            	_logger.Error("Unable to find type '{0}'" );
                //_logger.ErrorFormat("Unable to find type '{0}'", type);

              //  if (_logger.IsDebugEnabled())
                //    DebugService.Debug(new ClassFactory(), "ClassFactory.UnknownType", type);
            }

			throw new Exception(String.format("Unable to find type {0}.", type));
		}
*/		
			return result;
			}catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		public static Object CreateObject(Type type) throws Exception
		{
			return CreateObject(type, null);
		}

		public static Object CreateObject(Type type, Object... constructorParams) throws Exception
		{
			Class<?> type_val = (Class<?>)type;
			
			if (type == null)
				throw new  Exception("Type");

			try
			{
				//Array.class				
				if ( constructorParams != null)// && constructorParams[0] instanceof Array )//((Class)type).equals(Array.class))
				{
					if(type_val.isArray())
					{
						return Array.newInstance(Object.class,(int[])constructorParams[0]);
					}
					//Object obj = constructorParams[0];
					//Class cls = obj.getClass();
					return Array.newInstance(type_val,(int[])constructorParams[0]);
				}
				else		
				{					
					
					return (Class.forName(type_val.getName())).newInstance();// Activator.CreateInstance(type, constructorParams);
				}
			}
			catch (Exception e)
			{
				//e.printStackTrace();
				_logger.Debug(e.getStackTrace().toString());
				throw e;
				
			}
		}
	}