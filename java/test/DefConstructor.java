

public class DefConstructor {
	
	public DefConstructor (){
		System.out.println ("()");	
	}

	public DefConstructor (String eins) {
		System.out.println ("no super");	
	}

	public DefConstructor (int i) {
		super ();
		System.out.println ("super()");
	}

	public static void main (String [] args) {
		System.out.println ("eins");
		DefConstructor def = new DefConstructor ();
		System.out.println ("zwei");
		def = new DefConstructor ("eins");
		System.out.println ("drei");
		def = new DefConstructor (1);
	}
}
