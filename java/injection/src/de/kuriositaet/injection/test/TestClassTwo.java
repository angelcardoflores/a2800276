package de.kuriositaet.injection.test;

public class TestClassTwo {

	private TestInterface testInterfaceImpl;

	public static String staticTest1;
	public static String staticTest2;
	
	public String testString;
	
	
	public TestClassTwo(){}
	
	public TestClassTwo (TestInterface t) {
		this.testInterfaceImpl = t;
	}
	
	public void setString(String str) {
		this.testString = str;
	}
	
	public static void setStaticString(String str) {
		staticTest2 = str;
	}
	
	public String getTestString () {
		return this.testInterfaceImpl.testMethod();
	}
	
	public boolean debug () {
		return this.testInterfaceImpl.debug();
	}
	
	public TestInterface getTestInterface() {
		return this.testInterfaceImpl;
	}
	
}
