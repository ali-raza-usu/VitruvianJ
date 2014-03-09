package vitruvianJ.events;

public class ChildEntry extends Entry<Integer>{

	ChildEntry(Integer k) {
		super(k);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	Integer getKey() {
		// TODO Auto-generated method stub
		return super.getKey();
	}
	
	public static void main(String args[])
	{		
		
		ChildEntry marks440 = new ChildEntry(100);
		System.out.println("marks: " + marks440.getKey());

	}
}
