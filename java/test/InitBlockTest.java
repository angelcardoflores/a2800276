public class InitBlockTest {

	String test;

	{
		test = "block";
		System.out.println (test);
	}

	public InitBlockTest () {
		System.out.println ("Constructor");
	}

	public static void main (String [] args) {
		InitBlockTest test = new InitBlockTest();
	}

}
