package vitruvianJ.events;

public class Entry<K> {
	private final K mKey;
	  
	  
	  Entry(K k){ 
	    mKey = k;   
	  }
	 
	   K getKey(){
	    return mKey;
	  }
	 

	 
	  public String toString()  { 
	    return "(" + mKey + ", " + mKey + ")"; 
	  }
	
	public static void main(String args[])
	{		
		Entry<String> grade440 = new Entry<String>("A");
		Entry<Integer> marks440 = new Entry<Integer>(100);
		System.out.println("grade: " + grade440);
		System.out.println("marks: " + marks440);

	}

}