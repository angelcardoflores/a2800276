package de.kuriositaet.injection.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.kuriositaet.injection.Binding;
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
		assertEquals(test.testString, "123");
		
		matcher = new Matcher().forMethods();
		binding = new Binding (String.class).bind("345").to(matcher);
		config = new Configuration(binding);
		injector = new Injector(config);
		test = injector.createInstance(TestClass.class);
		assertEquals(test.testString, "345");
		
		matcher = new Matcher().forFields();
		binding = new Binding (String.class).bind("456").to(matcher);
		injector = new Injector(new Configuration(binding));
		test = injector.createInstance(TestClass.class);
		assertEquals(test.testString, "456");
		
		matcher = new Matcher().forStaticFields();
		binding = new Binding (String.class).bind("789").to(matcher);
		injector = new Injector(new Configuration(binding));
		test = injector.createInstance(TestClass.class);
		assertEquals("789", test.testStaticString);
		
		matcher = new Matcher().forStaticFields();
		binding = new Binding (String.class).bind("abc").to(matcher);
		injector = new Injector(new Configuration(binding));
		test = injector.createInstance(TestClass.class);
		assertEquals("789", test.testStaticString);
		
		
		
	}
}
