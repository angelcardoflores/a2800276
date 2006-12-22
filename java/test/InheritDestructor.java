

class Base {

	Base () {
		System.out.println ("Base crt.");
	}

	protected void finalize () throws Throwable {
		System.out.println ("Base fnlzd.");
		super.finalize();
	}

}

public class InheritDestructor extends Base {

	public static void fin () {
		for (int i=0; i<100000;i++) {
			InheritDestructor bla = new InheritDestructor ();
		}
	}

	public static void main (String [] rums) {
		InheritDestructor i = new InheritDestructor();
		System.out.println ("created .. done");
		fin();
		System.out.println ("loop done");		
	}
	protected void finalize () throws Throwable {
		System.out.println ("InheritDestructor fnlzd.");
		System.out.flush();
		super.finalize();
		System.out.flush();
	}
	

}
