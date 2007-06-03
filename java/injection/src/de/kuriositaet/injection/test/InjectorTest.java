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
import de.kuriositaet.injection.MatchingException;


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
	
	@Test (expected = BindingException.class) public void errorTest () {
		Matcher m = new Matcher().forMethods("setTestInterface");
		// circular injection
		Binding binding = new Binding(TestInterface.class).bind(TestClass.class).to(m);
		Injector injector = new Injector(new Configuration(binding));
		TestClass test = injector.createInstance(TestClass.class);
	}
}
