public class InheritTest {
	
	public InheritTest () {
		this.initialize ();	
	}

	public void initialize () {
		System.out.println ("Initializing Parent");	
	}

	public static void main (String [] args) {
		InheritTest it = new InheritTest();
		InheritTest ch = new Child();
		Child child = new Child();
	}
	
}

class Child extends InheritTest {
	public Child () {
		super();	
	}

	public void initialize () {
		System.out.println ("Initializing Child");	
	}
}

