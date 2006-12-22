

public class FieldVsMethodInheritance {
	
	protected String base = "BASE";

	public void test1 () {
		System.out.println (base);	
	}

	public void intermediate () {
		System.out.println ("This is intermediate in base");	
	}

	public void test2 () {
		intermediate ();	
	}
	
	public static void main (String [] args) {
			
	}
}

class Derived extends FieldVsMethodInheritance {
	protected String base = "derived";

	public void intermediate () {
		System.out.println ("overridden intermediate");	
	}
}
