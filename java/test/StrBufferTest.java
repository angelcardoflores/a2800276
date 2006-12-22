
public class StrBufferTest {

	public static boolean t;
	public static void main (String []args) {

		String test = "dies ist ein Test"+
				"nur ein Test";

		String test2 = "noch ein test"+
				"der erste war"+
				test+
				"super";

		String test3 = test2;
		if (t) test3 += test;
		test3 += "hallo";

		System.out.println (test);
		System.out.println (test2);
	}

}
