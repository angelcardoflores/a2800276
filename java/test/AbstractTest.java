

public class AbstractTest {

	public static void main (String [] args) {
		Base.doAnotherThing();
	}

}

abstract class Base {
	
	abstract void doSomething();
	static void doAnotherThing () {
		System.out.println ("Done, base");
	}
}

class Child extends Base {
	void doSomething () {
		System.out.println ("Doing it!");
	}
}
