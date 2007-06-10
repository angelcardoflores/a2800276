package de.kuriositaet.injection.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import de.kuriositaet.injection.Configuration;
import de.kuriositaet.injection.Injector;
import de.kuriositaet.injection.PropertyConfiguration;
import de.kuriositaet.injection.properties.MultiProperties;


public class PropertyConfigurationTest {
	@Test public void basicTest () {
		MultiProperties props = new MultiProperties();
		InputStream is = this.getClass().getResourceAsStream("/de/kuriositaet/injection/test/test1.properties");
		try {
			props.load(is);
		} catch (IOException e) {
			assertTrue(false);
		}
		PropertyConfiguration config = new PropertyConfiguration(props);
		Injector i = new Injector(config);
		TestClass test = i.createInstance(TestClass.class);
		assertEquals("123", test.testString);
		
		config = new PropertyConfiguration("/de/kuriositaet/injection/test/test2.properties");
		i = new Injector(config);
		test = i.createInstance(TestClass.class);
		assertEquals("345", test.testString);
		
		config = new PropertyConfiguration("/de/kuriositaet/injection/test/test3.properties");
		i = new Injector(config);
		test = i.createInstance(TestClass.class);
		assertEquals("456", test.testString);
		
		config = new PropertyConfiguration("/de/kuriositaet/injection/test/test4.properties");
		i = new Injector(config);
		test = i.createInstance(TestClass.class);
		assertEquals("789", test.testStaticString);
	}
	
	@Test public void complexTest () {
		Configuration config = new PropertyConfiguration("/de/kuriositaet/injection/test/test5.properties");
		Injector injector = new Injector(config);
		TestClassTwo test = injector.createInstance(TestClassTwo.class);
		assertEquals("123", test.getTestString());
		assertTrue(test.debug());
	}
}
