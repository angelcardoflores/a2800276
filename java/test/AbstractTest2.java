

public abstract class AbstractTest2 {
	
	public AbstractTest2 () {
		System.out.println ("works");	
	}
	abstract void test ();

	public static void main (String [] args) {
		AbstractTest2 test = new AbstractTest2 () {
			void test () {
				System.out.println ("huhu");	
			}
		};	
	}
	
}
