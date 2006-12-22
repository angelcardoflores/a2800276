

public class InheritException {


	public static void main (String [] args) {}
	
}

class Base {

	void foo() {

	}

	void bar () throws Exception {
		throw new Exception ("baaarg!");
	}
}

class Child extends Base{
	// does work
	void foo() throws Exception {
		throw new Exception ("Aarrrgh!");
	}
	//works
	void bar () {
	}
}
