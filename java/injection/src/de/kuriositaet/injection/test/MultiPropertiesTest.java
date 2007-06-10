package de.kuriositaet.injection.test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import de.kuriositaet.injection.properties.MultiProperties;


public class MultiPropertiesTest {

	
	@Test public void basicTest () {
		MultiProperties mp = new MultiProperties();
		String a = "123";
		mp.setProperty("test.dingens.a", a);
		mp.setProperty("test.dingens.b", a);
		mp.setProperty("test.dingens.c", a);
		mp.setProperty("test.dingens.d", a);
		mp.setProperty("test.dingens.e", a);
		mp.setProperty("test.dingens.f", a);
		mp.setProperty("test.bums.a", a);
		mp.setProperty("test.bums.b", a);
		mp.setProperty("test.bums.c", a);
		mp.setProperty("test.bums.d", a);
		mp.setProperty("test.bums.e", a);
		mp.setProperty("test.bums.f", a);
		mp.setProperty("test2.dingens.a", a);
		mp.setProperty("test2.dingens.b", a);
		mp.setProperty("test2.dingens.c", a);
		mp.setProperty("test2.dingens.d", a);
		mp.setProperty("test2.dingens.e", a);
		mp.setProperty("test2.bums.a", a);
		mp.setProperty("test2.bums.b", a);
		mp.setProperty("test2.bums.c", a);
		mp.setProperty("test2.bums.d", a);
		mp.setProperty("test2.bums.e", a);
		
		MultiProperties mp2 = mp.getSubProperties("test");
		assertEquals(12, mp2.size());
		MultiProperties bums = mp2.getSubProperties("bums");
		assertEquals(6, bums.size());
		
		mp2 = mp.getSubProperties("test2");
		assertEquals(10, mp2.size());
		MultiProperties dingens = mp2.getSubProperties("dingens");
		assertEquals(5, dingens.size());
		
		Set<String> skeys = mp.getSubPropertyKeys();
		assertEquals(2, skeys.size());
		assertTrue(skeys.contains("test"));
		assertTrue(skeys.contains("test2"));
		
		MultiProperties subkeys = new MultiProperties();
		subkeys.setProperty("test.a.a", a);
		subkeys.setProperty("test.b.a", a);
		subkeys.setProperty("test.c.a", a);
		subkeys.setProperty("test.d.a", a);
		subkeys.setProperty("test.e.a", a);
		subkeys.setProperty("test.f.a", a);
		subkeys.setProperty("test.g.a", a);
		skeys = subkeys.getSubProperties("test").getSubPropertyKeys();
		assertEquals(7, skeys.size());
		assertTrue(skeys.contains("a"));
		assertTrue(skeys.contains("b"));
		assertTrue(skeys.contains("c"));
		assertTrue(skeys.contains("d"));
		assertTrue(skeys.contains("e"));
		assertTrue(skeys.contains("f"));
		assertTrue(skeys.contains("g"));
		
	}
	
}
