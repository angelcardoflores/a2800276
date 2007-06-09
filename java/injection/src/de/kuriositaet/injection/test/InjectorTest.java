package de.kuriositaet.injection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.kuriositaet.injection.Binding;
import de.kuriositaet.injection.BindingException;
import de.kuriositaet.injection.Configuration;
import de.kuriositaet.injection.Injector;
import de.kuriositaet.injection.Matcher;


public class InjectorTest {
	@Test public void basicTest () {
		Matcher matcher = new Matcher().forConstructors();
		Binding binding = new Binding (String.class);
		binding.bind("123").to(matcher);
		
		Configuration config = new Configuration(binding);
		Injector injector = new Injector(config);
		
		TestClass test = injector.createInstance(TestClass.class);
		assertEquals("123", test.testString);
		
		matcher = new Matcher().forMethods();
		binding = new Binding (String.class).bind("345").to(matcher);
		config = new Configuration(binding);
		injector = new Injector(config);
		test = injector.createInstance(TestClass.class);
		assertEquals("345", test.testString);
		
		matcher = new Matcher().forFields();
		binding = new Binding (String.class).bind("456").to(matcher);
		injector = new Injector(new Configuration(binding));
		test = injector.createInstance(TestClass.class);
		assertEquals("456", test.testString);
		
		matcher = new Matcher().forStaticFields();
		binding = new Binding (String.class).bind("789").to(matcher);
		injector = new Injector(new Configuration(binding));
		test = injector.createInstance(TestClass.class);
		assertEquals("789", TestClass.testStaticString);
		
		//static injections should only take place once.
		matcher = new Matcher().forStaticFields();
		binding = new Binding (String.class).bind("abc").to(matcher);
		injector = new Injector(new Configuration(binding));
		test = injector.createInstance(TestClass.class);
		assertEquals("789", TestClass.testStaticString);	
	}
	
	
	
	@Test public void complexTest () {
		Matcher m = new Matcher().forConstructors();
		Binding binding = new Binding(TestInterface.class).bind(TestClass.class).to(m);
		Binding binding2 = new Binding(String.class).bind("123").to(m);
		
		Matcher m2 = new Matcher().forFields("DEBUG");
		Binding binding3 = new Binding(boolean.class).bind(true).to(m2);
		
		Injector injector = new Injector(new Configuration(binding, binding2, binding3));
		TestClassTwo test = injector.createInstance(TestClassTwo.class);
		assertEquals("123", test.getTestString());
		assertTrue(test.debug());
		
		injector = new Injector(new Configuration(binding, binding2));
		test = injector.createInstance(TestClassTwo.class);
		assertEquals("123", test.getTestString());
		assertFalse(test.debug());
		
	}
	
	@Test (expected = BindingException.class) public void circularBinding () {
		Matcher m = new Matcher().forMethods("setTestInterface");
		// circular injection
		Binding binding = new Binding(TestInterface.class).bind(TestClass.class).to(m);
		Injector injector = new Injector(new Configuration(binding));
		TestClass test = injector.createInstance(TestClass.class);
		
	}
	
	@Test public void circularBindingOK () {
		Matcher m = new Matcher().forMethods("setTestInterface");
		// circular injection
		Binding binding = new Binding(TestInterface.class).bind(new TestClass()).to(m);
		Injector injector = new Injector(new Configuration(binding));
		TestClass test = injector.createInstance(TestClass.class);	
	}
	
	@Test (expected = BindingException.class) public void redundantBinding () {
		Matcher m = new Matcher().forFields("DEBUG");
		Binding binding = new Binding(boolean.class).bind(false).to(m);
		Binding binding2 = new Binding(boolean.class).bind(false).to(m);
		Injector injector = new Injector(new Configuration(binding, binding2));
		TestClass test = injector.createInstance(TestClass.class);
	}
	
	@Test public void singletonTest () {
		Matcher m = new Matcher().forConstructors();
		Binding binding = new Binding(TestInterface.class).bind(TestClass.class).to(m).singleton();
		Injector injector = new Injector(new Configuration(binding));
		TestClassTwo test1 = injector.createInstance(TestClassTwo.class);
		TestClassTwo test2 = injector.createInstance(TestClassTwo.class);
		assertTrue(test1.getTestInterface() == test2.getTestInterface());
		
		binding = new Binding(TestInterface.class).bind(TestClass.class).to(m);
		injector = new Injector(new Configuration(binding));
		test1 = injector.createInstance(TestClassTwo.class);
		test2 = injector.createInstance(TestClassTwo.class);
		assertFalse(test1.getTestInterface() == test2.getTestInterface());
		
		m.forPackage("de.kuriositaet.injection.test");
		binding = new Binding(String.class).bind(String.class).to(m).singleton();
		injector = new Injector(new Configuration(binding));
		TestClass testA = injector.createInstance(TestClass.class);
		TestClass testB = injector.createInstance(TestClass.class);
		assertTrue(testA.testString==testB.testString);
		
		binding = new Binding(String.class).bind(String.class).to(m);
		injector = new Injector(new Configuration(binding));
		testA = injector.createInstance(TestClass.class);
		testB = injector.createInstance(TestClass.class);
		assertFalse(testA.testString==testB.testString);		
	}
	
	@Test public void subBindingTest () {
		Matcher m = new Matcher().forConstructors();
		Class str = String.class;
		Binding b = new Binding (str).bind(str).singleton();
		Binding b2 = new Binding (str).bind(str);
		Binding b3 = new Binding (str, str).bind(b, b2);
		Binding b4 = new Binding (str, str, str).bind(b3, b).to(m);
		Injector inj = new Injector(new Configuration(b4));
		TestClass test1 = inj.createInstance(TestClass.class);
		TestClass test2 = inj.createInstance(TestClass.class);
		assertTrue(test1.getStr1()==test2.getStr1());
		assertFalse(test1.getStr2()==test2.getStr2());
		assertTrue(test1.getStr3()==test2.getStr3());
		
		
	}
	
	@Test public void staticInjection () {
		Matcher m = new Matcher().forStaticFields().forStaticMethods().forClass(TestClassTwo.class);
		Binding b = new Binding(String.class).bind("123").to(m);
		Injector i = new Injector(new Configuration(b));
		TestClassTwo t1 = new TestClassTwo();
		i.injectInstance(t1);
		assertEquals("123", TestClassTwo.staticTest1);
		assertEquals("123", TestClassTwo.staticTest2);
		
		b = new Binding(String.class).bind("abc").to(m);
		TestClassTwo t2 = new TestClassTwo();
		Injector i2 = new Injector(new Configuration(b));
		i2.injectInstance(t2);
		assertEquals("123", TestClassTwo.staticTest1);
		assertEquals("123", TestClassTwo.staticTest2);
		
		m = new Matcher().forMethods().forClass(TestClassTwo.class);
		b.to(m);
		i = new Injector(new Configuration(b));
		assertEquals(null, t2.testString);
		i.injectInstance(t2);
		assertEquals("abc", t2.testString);
		
		
	}
	
}
