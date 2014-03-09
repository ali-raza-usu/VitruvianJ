package vitruvianJ.delegate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DelegateProxy extends Delegator implements InvocationHandler
   {
       
       private  Delegator m_Template;

        /**
          * constructor supplying a Class passing in types not template
         * @param target possibly null target if null the method must be static
         * @param target non-null class implementing a suitable  method
         * @param target non-null object implementing a suitable static method
          * @param MethodName nun-null name of a public static method in target
        * @param template non-null template with the required arguemts and return
       */
       
       protected DelegateProxy(Object target,Class targetClass,String MethodName,Delegator template)
           {
       	super();
           m_Template = template;
           setTarget(target);
           Method meth = findSuitableMethod(targetClass,MethodName,template);
           setMethod(meth);
       }



       
       protected void  validateArgs(Object[] args)  throws IllegalArgumentException
        {
            Class[] MyArgs = getArguments();
            if(args.length !=  MyArgs.length)
                throw new IllegalArgumentException("Delegate required " +  MyArgs.length +
                    "arguments");
            for(int i = 0; i < args.length; i++) {
                if(!MyArgs[i].isInstance(args[i]) )
                    throw new IllegalArgumentException("Argument " + i +
                            " must be of class " +  MyArgs[i].getName());
            }
        }

      

   }// end class DelegateProxy

