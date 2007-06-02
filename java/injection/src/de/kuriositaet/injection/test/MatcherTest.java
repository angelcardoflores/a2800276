package de.kuriositaet.injection.test;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import de.kuriositaet.injection.Matcher;




public class MatcherTest {

	@Test public void simpleMatch () {
		Matcher matcher = new Matcher().forClass(TestClass.class).forConstructors();
		assertTrue(matcher.matches(TestClass.class));
		//assertFalse(matcher.matches(this.getClass()));
		matcher = new Matcher().forConstructors();
		assertTrue(matcher.matches(TestClass.class));
		matcher = new Matcher().forFields();
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		matcher = new Matcher().forStaticFields();
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		matcher = new Matcher().forMethods();
		assertTrue(matcher.matches(TestClass.class));
		matcher = new Matcher().forStaticMethods();
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
	}
	
	@Test public void RegexpMatch () {
		Pattern pattern = Pattern.compile("^test.*");
		Matcher matcher = new Matcher().forFields(pattern);
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		matcher = new Matcher().forStaticFields(pattern);
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		matcher = new Matcher().forMethods(pattern);
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		matcher = new Matcher().forStaticMethods(pattern);
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
	}
	
	@Test public void InheritEtcTest () {
		Matcher matcher = new Matcher().forImplementationsOf(TestInterface.class).forFields();
		assertFalse(matcher.matches(this.getClass()));
		assertTrue(matcher.matches(TestClass.class));
		
		matcher = new Matcher().forPackage("de.kuriositaet.injection.test").forFields();
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		
		matcher = new Matcher().forSubclassesOf(TestBase.class).forFields();
		assertFalse(matcher.matches(this.getClass()));
		assertTrue(matcher.matches(TestClass.class));
	}
}
