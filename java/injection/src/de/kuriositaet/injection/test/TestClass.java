package de.kuriositaet.injection.test;

public class TestClass extends TestBase implements TestInterface{
	public boolean DEBUG;
	public String testString;
	public static String testStaticString;
	
	public TestClass () {}
	public TestClass (String str){}
	
	public String testMethod () {
		return testString;}
	public void testMethod (String str){}
	
	public static void testStaticMethod () {}
}
