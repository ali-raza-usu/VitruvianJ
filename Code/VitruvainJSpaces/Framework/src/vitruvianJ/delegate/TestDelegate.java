package vitruvianJ.delegate;

import junit.framework.*;
/**
 * com.lordjoe.csharp.TestDelegate2
 * @author Steve Lewis smlewis@lordjoe.com
 */
public class TestDelegate  extends TestCase
{
    public static final int ITER_COUNT = 10 * 1000 * 1000;

    /**
     * common interface - note No implementing class i.e. Class1
     * implements this
     */
    public static interface IStringDisplay {
        public void doDisplay(String s);
    }

   
    public TestDelegate(String name) {
	   super(name);	    
    }

   /**
    * This section tests that DelegateTemplate fails when passed
    * an unsuitable interface
    */
    public static interface IBadStringDisplay {
         public void doDisplay(String s);
         public void doDisplay2(String s);
     }
     public static interface IBadStringDisplay2 {
      }
/*
    public void testDelegateBuild()
    {
        try {
             new Delegator(IBadStringDisplay.class);
             fail();
         }
         catch(IllegalArgumentException ex) {}
        try {
             new Delegator(IBadStringDisplay2.class);
             fail();
         }
         catch(IllegalArgumentException ex) {}
     }
*/
    /**
     * code to test calls to delegate
     */
    /*
    public void testDelegate() throws Exception
     {
    	
        Delegator myDelegate = new Delegator(IStringDisplay.class);
         Class1 obj1 = new Class1();
         Class2 obj2 = new Class2();
         IStringDisplay[] items = new IStringDisplay[3];
         items[0] = (IStringDisplay)myDelegate.build(obj1,"show");
         items[1] = (IStringDisplay)myDelegate.build(obj2,"display");
         items[2] = (IStringDisplay)myDelegate.build(Class3.class, "staticDisplay");

          for(int i = 0; i < items.length; i++) {
             IStringDisplay item = items[i];
             item.doDisplay("test");
         }
       //  timingTest(items,obj1,obj2,ITER_COUNT);
         
        
     }
*/
    /**
     * code to test calls to delegate
     */
  


    public void testEventDelegateModel() throws Exception
    {    	   	
       Class[] params = { String.class };
       Delegator myDelegate = new Delegator(params,Void.TYPE);       
       Class1 class1 = new Class1();
       class1.subscribe(myDelegate);
       
       Class2 class2 = new Class2();
       class2.subscribe(myDelegate);
       
       Class3 class3 = new Class3();
       class3.subscribe(myDelegate);
              
       //myDelegate.removeObserver(class3);
       
       myDelegate.notifyObservers();       
    }

    
    
    /**
     * Test of timing - note set iteration large i.e. 1000000 for
     * resaonbale results
     */
    public void timingTest(IStringDisplay[] items,Class1 obj1,Class2 obj2,int iterations)
    {
        // Warm up hotspot
        for(int k = 0; k < 100; k++) {
            for(int i = 0; i < items.length; i++) {
                IStringDisplay item = items[i];
                item.doDisplay("test");
            }
            obj1.show("test");
            obj2.display("test");
            Class3.staticDisplay("test");
        }
        long start = System.currentTimeMillis();
        for(int j = 0; j < iterations; j++) {
            for(int i = 0; i < items.length; i++) {
                IStringDisplay item = items[i];
                item.doDisplay("test");
           }
        }
        long end = System.currentTimeMillis();
        double delegateTime = (end - start) / 1000;
        double perIteration =  1000 * 1000 * delegateTime /  iterations;

        start = System.currentTimeMillis();
        for(int j = 0; j < iterations; j++) {
            obj1.show("test");
            obj2.display("test");
            Class3.staticDisplay("test");
        }
        end = System.currentTimeMillis();
        double directTime = (end - start) / 1000;
        double perCallIteration =  1000 * 1000  * directTime /  iterations;

        System.out.println("Ran " + iterations  + " iterations ");
        System.out.println("Delegate Test took " + delegateTime  + "sec");
        System.out.println("per iteration " + perIteration  + "microsec");
         System.out.println("Direct Calls took " + directTime  + "sec");
        System.out.println("per iteration " + perCallIteration  + "microsec");


    }


      public static Test suite() {
          return new TestSuite(TestDelegate.class);
      }

      public static void main(String[] args) {
          String[] RealArgs =  { "com.lordjoe.csharp.TestDelegate3" };
         // junit.swingui.TestRunner.main(RealArgs);
         junit.textui.TestRunner.main(RealArgs);
      }

}

/**
 * some classes we need to call
 * method body might printout fir vicibility or
 * increment a variable for timing test (to prevent optimizing out)
 */
class Class1 implements Observer{
  int count;
  private IDelegate subject;
  
  public Class1()
  {	  	  	
  }
  
  public void subscribe(Delegator value)
  {
	  value.registerObserver(this);
	  subject = value.build(this, "show");
  }
  
  public void show(String s)   {
      count++; System.out.println(s + " : class 1");
  }
@Override
public void update() {
	// TODO Auto-generated method stub
	 subject.invoke("test");
	 
}
}
class Class2 implements Observer{
    int count;
    
    private IDelegate subject;
    
    public Class2()
    {  	  	
    }
    
    public void subscribe(Delegator value)
    {
  	  value.registerObserver(this);
  	  subject = value.build(this, "display");
    }
    
    
  public void display(String s) {
      count++;System.out.println(s + " : class 2");
  }
  
  public String displayToString(String s) {
      count++;System.out.println(s);
      return count+"";
  }

@Override
public void update() {
	// TODO Auto-generated method stub
	subject.invoke("test");	
}
}
/**
 * here the methos is static
 */
class Class3 implements Observer{ // allows static method as well
    static int count;
    
    private IDelegate subject;
    
    public Class3()
    {}
    
    public void subscribe(Delegator value)
    {
  	  value.registerObserver(this);
  	  subject = value.build(this, "staticDisplay");
    }
    
  public static void staticDisplay(String s) {
    count++; System.out.println(s + " : class 3");
  }
@Override
public void update() {
	// TODO Auto-generated method stub
	subject.invoke("test");
}
}