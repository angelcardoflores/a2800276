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
	
	@Test (expected = BindingException.class) public void bindObjectsException () {
		Binding binding = new Binding(String.class).bind("");
		Binding binding2 = new Binding(String.class).bind(binding).bind("");
	}
	
	@Test (expected = BindingException.class) public void bindBindingsException () {
		Binding binding = new Binding(String.class).bind("");
		Binding binding2 = new Binding(String.class).bind("").bind(binding);
	}
	
	@Test (expected = BindingException.class) public void bindDoesntMatchException () {
		Binding b = new Binding(String.class).bind("");
		Binding b2 = new Binding(String.class).bind(b, b);
	}
	
	@Test (expected = BindingException.class) public void subbindingTest() {
		Binding b = new Binding(TestInterface.class).bind(TestClass.class);
		Binding b2 = new Binding(String.class).bind("");
		Binding b3 = new Binding(String.class, TestInterface.class, String.class).bind(b2, b, b2);
		assertTrue(true);
		b3.bind(b2);
	}
	
	@Test (expected = BindingException.class) public void subbindingSignatureNoMatch(){
		Binding b = new Binding(TestInterface.class).bind(TestClass.class);
		Binding b2 = new Binding(TestInterface.class).bind(b);
		assertTrue(true);
		Binding b3 = new Binding(String.class).bind(b2);
	}
}
