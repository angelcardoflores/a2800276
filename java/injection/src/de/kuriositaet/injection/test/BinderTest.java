package de.kuriositaet.injection.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.kuriositaet.injection.Binding;
import de.kuriositaet.injection.BindingException;
import de.kuriositaet.injection.Matcher;


public class BinderTest {
	@Test public void basicTest () {
		Matcher matcher = new Matcher().forConstructors();
		Binding binding = new Binding (String.class);
		binding.bind("123").to(matcher);
		assertTrue(binding.matches(TestClass.class));
		assertTrue(binding.hasMatchingConstructor(TestClass.class));
		
		matcher = new Matcher().forMethods();
		binding = new Binding (String.class);
		binding.bind("123").to(matcher);
		assertTrue(binding.matches(TestClass.class));
		assertFalse(binding.hasMatchingConstructor(TestClass.class));
		
		binding = new Binding (String.class, String.class);
		binding.bind("123", "345").to(matcher);
		assertFalse(binding.matches(TestClass.class));
		assertFalse(binding.hasMatchingConstructor(TestClass.class));
		
		
	}
	
	@Test (expected = BindingException.class) public void bindException () {
		Matcher matcher = new Matcher().forConstructors();
		Binding binding = new Binding (String.class, String.class);
		// binding doesn't match signature.
		binding.bind("123").to(matcher);
	}
	
	@Test (expected = BindingException.class) public void noSignature () {
		Binding binding = new Binding();
	}
}
