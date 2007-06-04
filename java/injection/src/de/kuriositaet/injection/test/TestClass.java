package de.kuriositaet.injection.test;

public class TestClass extends TestBase implements TestInterface {
	public boolean DEBUG;

	public String testString;

	private TestInterface testInterface;


	public static String testStaticString;

	public TestClass() {
	}

	public TestClass(String str) {
		this.testString = str;
	}

	public String testMethod() {
		return testString;
	}

	public void testMethod(String str) {
		this.testString = str;
	}
	
	public void setTestInterface (TestInterface t){
		//System.out.println("Setting: "+t);
		this.testInterface = t;
	}
	
	public TestInterface getTestInterface () {
		return this.testInterface;
	}

	public static void testStaticMethod() {
	}
	
	public boolean debug () {
		return this.DEBUG;
	}
}
