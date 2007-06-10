package de.kuriositaet.injection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import de.kuriositaet.injection.Matcher;
import de.kuriositaet.injection.MatchingException;


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
		matcher = new Matcher().forMethods().forClasses("^Test.*");
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		matcher = new Matcher().forStaticMethods();
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		
		matcher = new Matcher().forConstructors();
		assertTrue(matcher.matches(String.class));
		matcher = new Matcher().forConstructors().forClass(TestClass.class);
		assertFalse(matcher.matches(String.class));
		
		
	}
	
	@Test public void regexpMatch () {
		//Members
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
		
		//Classes
		matcher = new Matcher().forClasses("^Test.*").forFields();
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
	}
	
	@Test public void inheritEtcTest () {
		Matcher matcher = new Matcher().forImplementationsOf(TestInterface.class).forFields();
		assertFalse(matcher.matches(this.getClass()));
		assertTrue(matcher.matches(TestClass.class));
		
		matcher = new Matcher().forPackages("de.kuriositaet.injection.test").forFields();
		assertTrue(matcher.matches(TestClass.class));
		assertFalse(matcher.matches(this.getClass()));
		
		matcher = new Matcher().forSubclassesOf(TestBase.class).forFields();
		assertFalse(matcher.matches(this.getClass()));
		assertTrue(matcher.matches(TestClass.class));
	}
	
	@Test public void matches () throws SecurityException, NoSuchFieldException, NoSuchMethodException {
		Matcher matcher = new Matcher().forFields("^test.*");
		List<Field> list = matcher.matchingFields(TestClass.class);
		Field expected = TestClass.class.getField("testString");
		Field actual = list.get(0);
		assertEquals(expected, actual);
		assertEquals(list.size(), 1);
		
		matcher = new Matcher().forStaticFields("^test.*");
		list = matcher.matchingStaticFields(TestClass.class);
		expected = TestClass.class.getField("testStaticString");
		actual = list.get(0);
		assertEquals(expected, actual);
		assertEquals(list.size(), 2);
		
		matcher = new Matcher().forMethods("^test.*");
		List<Method> listM = matcher.matchingMethods(TestClass.class);
		Method expectedM0 = TestClass.class.getMethod("testMethod");
		Class [] params = new Class[1];
		params[0] = String.class;
		Method expectedM1 = TestClass.class.getMethod("testMethod", params);
		
		assertTrue (listM.contains(expectedM0));
		assertTrue (listM.contains(expectedM1));
		assertEquals(listM.size(), 2);
		
		matcher = new Matcher().forStaticMethods("^test.*");
		listM = matcher.matchingStaticMethods(TestClass.class);
		expectedM0 = TestClass.class.getMethod("testStaticMethod");
		Method actualM = listM.get(0);
		assertEquals(expectedM0, actualM);
		assertEquals(1, listM.size());
		
	}
	
	@Test (expected = MatchingException.class) public void matchingExceptionInterface () {
		Matcher matcher = new Matcher().forImplementationsOf(this.getClass());
	}
	@Test (expected = MatchingException.class) public void matchingExceptionSubclass () {
		Matcher matcher = new Matcher().forSubclassesOf(String.class);
	}
	@Test (expected = MatchingException.class) public void matchingExceptionExplicitInterface () {
		Matcher matcher = new Matcher().forClass(TestInterface.class);
	}
	@Test (expected = MatchingException.class) public void matchingExceptionExplicitAbstract () {
		Matcher matcher = new Matcher().forClass(TestBase.class);
	}

	
}
