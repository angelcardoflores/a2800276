package de.kuriositaet.injection.test;

public class TestClassTwo {

	private TestInterface testInterfaceImpl;

	public TestClassTwo (TestInterface t) {
		this.testInterfaceImpl = t;
	}
	
	public String getTestString () {
		return this.testInterfaceImpl.testMethod();
	}
	
	public boolean debug () {
		return this.testInterfaceImpl.debug();
	}
	
}
