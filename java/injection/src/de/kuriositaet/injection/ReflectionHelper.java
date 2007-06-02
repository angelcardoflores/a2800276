package de.kuriositaet.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class ReflectionHelper {
	
	public static List<Method> getPublicStaticMethods (Class clazz) {
		List<Method> list = new LinkedList<Method>();
		for (Method m : clazz.getMethods()) {
			if (Modifier.isStatic(m.getModifiers())&&Modifier.isPublic(m.getModifiers())){
				list.add(m);
			}
		}
		return list;
	}
	public static List<Method> getPublicInstanceMethods (Class clazz) {
		List<Method> list = new LinkedList<Method>();
		for (Method m : clazz.getMethods()) {
			if (!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())){
				list.add(m);
			}
		}
		return list;
	}
	
	public static List<Constructor> getPublicConstructors (Class clazz) {
		List<Constructor> list = new LinkedList<Constructor>();
		for (Constructor c : clazz.getConstructors()) {
			if (Modifier.isPublic(c.getModifiers())){
				list.add(c);
			}
		}
		return list;
	}
}
