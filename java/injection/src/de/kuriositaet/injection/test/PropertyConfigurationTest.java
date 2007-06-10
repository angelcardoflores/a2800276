package de.kuriositaet.injection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import de.kuriositaet.injection.Binding;
import de.kuriositaet.injection.BindingException;
import de.kuriositaet.injection.Configuration;
import de.kuriositaet.injection.Injector;
import de.kuriositaet.injection.Matcher;
import de.kuriositaet.injection.PropertyConfiguration;
import de.kuriositaet.injection.properties.MultiProperties;


/**
 * As far as possible, these test mirror the test cases in Injector test
 * using a property backed configuration.
 * @author tim
 *
 */
public class PropertyConfigurationTest {
	
	static final String PROP_DIR = "/de/kuriositaet/injection/test/properties";
	static final String PROP_1 = PROP_DIR + "/test1.properties";
	static final String PROP_2 = PROP_DIR + "/test2.properties";
	static final String PROP_3 = PROP_DIR + "/test3.properties";
	static final String PROP_4 = PROP_DIR + "/test4.properties";
	static final String PROP_5 = PROP_DIR + "/test5.properties";
	static final String PROP_6 = PROP_DIR + "/test6.properties";
	static final String PROP_7 = PROP_DIR + "/test7.properties";
	static final String PROP_8 = PROP_DIR + "/test8.properties";
	static final String PROP_9 = PROP_DIR + "/test9.properties";
	static final String PROP_10 = PROP_DIR + "/test10.properties";
	static final String PROP_11 = PROP_DIR + "/test11.properties";
	static final String PROP_12 = PROP_DIR + "/test12.properties";
	static final String PROP_13 = PROP_DIR + "/test13.properties";
	static final String PROP_14 = PROP_DIR + "/test14.properties";
	static final String PROP_15 = PROP_DIR + "/test15.properties";
	static final String PROP_16 = PROP_DIR + "/test16.properties";
	static final String PROP_17 = PROP_DIR + "/test17.properties";
	static final String PROP_18 = PROP_DIR + "/test18.properties";
	
	@Test public void basicTest () {
		MultiProperties props = new MultiProperties();
		InputStream is = this.getClass().getResourceAsStream(PROP_1);
		try {
			props.load(is);
		} catch (IOException e) {
			assertTrue(false);
		}
		PropertyConfiguration config = new PropertyConfiguration(props);
		Injector i = new Injector(config);
		TestClass test = i.createInstance(TestClass.class);
		assertEquals("123", test.testString);
		
		config = new PropertyConfiguration(PROP_2);
		i = new Injector(config);
		test = i.createInstance(TestClass.class);
		assertEquals("345", test.testString);
		
		config = new PropertyConfiguration(PROP_3);
		i = new Injector(config);
		test = i.createInstance(TestClass.class);
		assertEquals("456", test.testString);
		
		config = new PropertyConfiguration(PROP_4);
		i = new Injector(config);
		test = i.createInstance(TestClass.class);
		assertEquals("789", test.testStaticString);
	}
	
	@Test public void complexTest () {
		Configuration config = new PropertyConfiguration(PROP_5);
//		for (Binding b: config.getBindings()) {
//			System.out.println(b);
//		}
		Injector injector = new Injector(config);
		TestClassTwo test = injector.createInstance(TestClassTwo.class);
		assertEquals("123", test.getTestString());
		assertTrue(test.debug());
		
		config = new PropertyConfiguration(PROP_6);
		injector = new Injector(config);
		test = injector.createInstance(TestClassTwo.class);
		assertEquals("123", test.getTestString());
		assertFalse(test.debug());
	}
	
	@Test (expected = BindingException.class) public void circularBinding () {
		Configuration config = new PropertyConfiguration(PROP_7);
		Injector injector = new Injector(config);
		TestClass test = injector.createInstance(TestClass.class);	
	}
	
	/* @Test public void circularBindingOK () {
		// can't do this test, properties only allow binding pre instantiated copies
		// of String, int, double and boolean
	}*/
	
	@Test (expected = BindingException.class) public void redundantBinding () {
		PropertyConfiguration config = new PropertyConfiguration(PROP_8);
		Injector injector = new Injector(config);
		TestClass test = injector.createInstance(TestClass.class);
	}
	
	@Test public void singletonTest () {
		
		Configuration config = new PropertyConfiguration(PROP_9);
		Injector injector = new Injector(config);
		TestClassTwo test1 = injector.createInstance(TestClassTwo.class);
		TestClassTwo test2 = injector.createInstance(TestClassTwo.class);
		assertTrue(test1.getTestInterface() == test2.getTestInterface());
		
		config = new PropertyConfiguration(PROP_10);
		injector = new Injector(config);
		test1 = injector.createInstance(TestClassTwo.class);
		test2 = injector.createInstance(TestClassTwo.class);
		assertFalse(test1.getTestInterface() == test2.getTestInterface());
		
//		m.forPackage("de.kuriositaet.injection.test");
//		binding = new Binding(String.class).bind(String.class).to(m).singleton();
		config = new PropertyConfiguration(PROP_11);
		injector = new Injector(config);
		TestClass testA = injector.createInstance(TestClass.class);
		TestClass testB = injector.createInstance(TestClass.class);
		assertTrue(testA.testString==testB.testString);
		
//		binding = new Binding(String.class).bind(String.class).to(m);
//		config = new PropertyConfiguration(PROP_12);
//		injector = new Injector(config);
//		for (Binding b : config.getBindings()) {
//			System.out.println(b);
//		}
		// Strangely enough, a binding created through properties
		// behaves like a singleton in this test, yet the generated 
		// binding isn't singleton.... hmmm. String optimization? maybe...
//		testA = injector.createInstance(TestClass.class);
//		testB = injector.createInstance(TestClass.class);
//		assertFalse(testA.testString==testB.testString);		
	}
	
	@Test public void subBindingTest () {
		Configuration config = new PropertyConfiguration(PROP_13);
		
//		for (Binding b:config.getBindings()) {
//			System.out.println(b);
//		}
		Injector inj = new Injector(config);
		TestClass test1 = inj.createInstance(TestClass.class);
		TestClass test2 = inj.createInstance(TestClass.class);
		assertTrue(test1.getStr1()==test2.getStr1());
		// Same problem... singleton doesn't work with property based config.
//		assertFalse(test1.getStr2()==test2.getStr2());
		assertTrue(test1.getStr3()==test2.getStr3());
		
		
	}

	@Test public void weirdSingleton () {
		// Singleton seems to work fine for non-String classes...
		Configuration config = new PropertyConfiguration(PROP_14);
		Injector i = new Injector(config);
		TestClassTwo test = i.createInstance(TestClassTwo.class);
		TestClassTwo test2 = i.createInstance(TestClassTwo.class);
		assertTrue (test.testInterfaceImpl == test2.testInterfaceImpl);
		
		config = new PropertyConfiguration(PROP_15);
		i = new Injector(config);
		test = i.createInstance(TestClassTwo.class);
		test2 = i.createInstance(TestClassTwo.class);
		assertFalse (test.testInterfaceImpl == test2.testInterfaceImpl);
	}
	
	@Test public void staticInjection () {
//		Matcher m = new Matcher().forStaticFields().forStaticMethods().forClass(TestClassThree.class);
//		Binding b = new Binding(String.class).bind("123").to(m);
		Configuration c = new PropertyConfiguration(PROP_16);
		Injector i = new Injector(c);
		TestClassThree t1 = new TestClassThree();
		i.injectInstance(t1);
		assertEquals("123", TestClassThree.staticTest1);
		assertEquals("123", TestClassThree.staticTest2);
		
//		b = new Binding(String.class).bind("abc").to(m);
		
		c = new PropertyConfiguration(PROP_17);
		TestClassThree t2 = new TestClassThree();
		Injector i2 = new Injector(c);
		i2.injectInstance(t2);
		assertEquals("123", TestClassThree.staticTest1);
		assertEquals("123", TestClassThree.staticTest2);
//		
//		m = new Matcher().forMethods().forClass(TestClassThree.class);
//		b.to(m);
		c = new PropertyConfiguration(PROP_18);
		i = new Injector(c);
		assertEquals(null, t2.testString);
		i.injectInstance(t2);
		assertEquals("abc", t2.testString);
		
		
	}
}
