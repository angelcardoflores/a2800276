
public class Test {

	static int count;

	public static void testStatic () {
		protocol ("testStatic: "+count);
		count++;
	}

	public void callAnother () {
		protocol ("callAnother");
		System.out.println ("Result: "+another());
	}

	public String another () {
		protocol ("another");
		return "another result";
		
	}

	public static void protocol (String mes) {
		System.out.println ("method: "+mes);
	}

	public static void main (String [] args) {
		protocol ("main");
		int i = 0;
		for (i = 0; i<25; i++) {
			testStatic();	
		}
		Test test = new Test();
		for (; i<35;i++) {
			test.callAnother();
		}
		protocol ("main end");
	}

}
