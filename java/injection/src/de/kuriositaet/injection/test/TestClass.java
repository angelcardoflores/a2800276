package de.kuriositaet.injection.test;

public class TestClass extends TestBase implements TestInterface {
	public boolean DEBUG;

	public String testString;

	private TestInterface testInterface;

	private String str3;

	private String str2;

	private String str1;


	public static String testStaticString;
	public static String testStaticString2;

	public TestClass() {
	}

	public TestClass(String str) {
		this.testString = str;
	}
	
	public TestClass (String str1, String str2, String str3) {
		this.str1 = str1;
		this.str2 = str2;
		this.str3 = str3;
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
	
	public static void setStatic(String str) {
		testStaticString2=str;
	}
	
	public boolean debug () {
		return this.DEBUG;
	}

	public String getStr1() {
		return str1;
	}

	public String getStr2() {
		return str2;
	}

	public String getStr3() {
		return str3;
	}
}
