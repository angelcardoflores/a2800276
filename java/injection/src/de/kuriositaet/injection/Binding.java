package de.kuriositaet.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import de.kuriositaet.injection.Matcher;

public class Binding {
	private Class[] signature;
	private Object[] boundValues;
	private List<Matcher> matchers;

	public Binding(Class...classes){
		if (classes == null || classes.length == 0)
			throw new BindingException ("Can't create binding for 0 length signature.");
		this.signature = classes;
		this.matchers = new LinkedList<Matcher>();
		
	}
	
	public Binding bind (Object... objs) {
		if (objs.length != signature.length)
			throw new BindingException ("Trying to bind values of length:"+objs.length+" to signature of length: "+signature.length);
		this.boundValues = objs;
		return this;
	}
	
	public Binding to (Matcher matcher) {
		this.matchers.add(matcher);
		return this;
	}
	
	public boolean matches (Class clazz) {
		for (Matcher matcher: matchers) {
			if (matcher.matches(clazz)) {
				if (matchesSignature(matcher, clazz))
					return true;
			}
		}
		return false;
	}

	private boolean matchesSignature(Matcher matcher, Class clazz) {
		if (matchesConstrutor(matcher, clazz)) return true;
		if (matchesFields(matcher, clazz)) return true;
		if (matchesStaticFields(matcher, clazz)) return true;
		if (matchesMethods(matcher, clazz)) return true;
		if (matchesStaticMethods(matcher, clazz)) return true;
		return false;
	}
	
	public boolean hasMatchingConstructor (Class clazz) {
		
		for (Matcher m: this.matchers) {
			if (!m.matches(clazz)) return false; 
			if (matchesConstrutor(m, clazz)) return true;
		}
		return false;
	}

	private boolean matchesStaticFields(Matcher matcher, Class clazz) {
		
		for (Field field : clazz.getDeclaredFields()) {
			Class [] sig = new Class[1];
			sig[0] = field.getType();
			if (!Modifier.isStatic(field.getModifiers())&&matchesSignature(sig)){
				return true;
			}
		}
		
		return false;
	}

	private boolean matchesMethods(Matcher matcher, Class clazz) {
		for (Method method : matcher.matchingMethods(clazz)){
			if (!Modifier.isStatic(method.getModifiers()) && matchesSignature(method.getParameterTypes()) ){
				return true;
			}
		}
		return false;
	}

	private boolean matchesFields(Matcher matcher, Class clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Class [] sig = new Class[1];
			sig[0] = field.getType();
			if (!Modifier.isStatic(field.getModifiers())&&matchesSignature(sig)){
				return true;
			}
		}
		return false;
	}

	private boolean matchesConstrutor(Matcher matcher, Class clazz) {
		for (Constructor constructor : matcher.matchingConstructors(clazz)){
			if (matchesSignature(constructor.getParameterTypes())) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesStaticMethods(Matcher matcher, Class clazz) {
		for (Method method : matcher.matchingMethods(clazz)){
			if (Modifier.isStatic(method.getModifiers()) && matchesSignature(method.getParameterTypes()) ){
				return true;
			}
		}
		return false;
	}
	
	private boolean matchesSignature (Class... signature) {
		return Arrays.equals(this.signature, signature);
	}
	
	protected <T> T createInstance (Injector i, Class<T> clazz) {
		if (!matches(clazz)) {
			throw new BindingException ("Class:"+clazz.getName()+" does not match. Cannot create instance.");
		}
		
		if (!hasMatchingConstructor(clazz)) {
			throw new BindingException ("Class:"+clazz.getName()+" has no matching Construtor. Cannot create instance.");
		}
		
		T instance = null;
		for (Matcher matcher : this.matchers) {
			List<Constructor> constructors = matcher.matchingConstructors(clazz);
			for (Constructor cons: constructors){
				if (matchesSignature(cons.getParameterTypes())) {
					Object [] parameters = instantiateBoundValues(i);
					try {
						instance = (T) cons.newInstance(parameters);
					} catch (Throwable e) {
						e.printStackTrace();
						throw new BindingException("Could not instantiate: "+clazz.getSimpleName()+" caught: "+e.toString());
					}
				}
			}
		}
		return instance;
		
	}
	
	/**
	 * Instantiate ojects to inject.
	 * @param inj
	 * @return
	 */
	private Object[] instantiateBoundValues(Injector inj) {
		Object [] values = new Object[boundValues.length];
		Object obj = null;
		for (int i = 0 ; i!= boundValues.length; ++i) {
			if (boundValues[i] instanceof Class) {
				obj = inj.createInstance((Class)boundValues[i]);
			} else {
				obj = boundValues[i];
			}
			values[i] = obj;
		}
		return values;
	}

	protected List<Method> injectMethods(Injector injector, Object instance) {
		List<Method> injected = new LinkedList<Method>();
		Class clazz = instance.getClass();
		for (Matcher m : this.matchers) {
			for (Method method : m.matchingMethods(clazz)) {
				if (matchesSignature(method.getParameterTypes())) {
					try {
						method.invoke(instance, instantiateBoundValues(injector));
					} catch (Throwable e) {
						e.printStackTrace();
						throw new BindingException ("Could not bind values to :"+method+" : "+e.toString());
					} 
					injected.add(method);
				}
			}
		}
		return injected;
	}
	protected List<Method> injectStaticMethods(Injector injector, Object instance) {
		List<Method> injected = new LinkedList<Method>();
		Class clazz = instance.getClass();
		for (Matcher m : this.matchers) {
			for (Method method : m.matchingStaticMethods(clazz)) {
				if (matchesSignature(method.getParameterTypes())) {
					try {
						method.invoke(instance, instantiateBoundValues(injector));
					} catch (Throwable e) {
						e.printStackTrace();
						throw new BindingException ("Could not bind values to :"+method+" : "+e.toString());
					} 
					injected.add(method);
				}
			}
		}
		return injected;
	}

	protected List<Field> injectFields(Injector injector, Object instance) {
		List<Field> injected = new LinkedList<Field>();
		Class clazz = instance.getClass();
		for (Matcher m : this.matchers) {
			for (Field field : m.matchingFields(clazz)) {
				if (matchesSignature(field.getType())) {
					try {
						field.set(instance, instantiateBoundValues(injector)[0]);
					} catch (Throwable e) {
						e.printStackTrace();
						throw new BindingException ("Could not bind values to :"+field+" : "+e.toString());
					} 
					injected.add(field);
				}
			}
		}
		return injected;
	}
	
	protected List<Field> injectStaticFields(Injector injector, Object instance) {
		List<Field> injected = new LinkedList<Field>();
		Class clazz = instance.getClass();
		for (Matcher m : this.matchers) {
			for (Field field : m.matchingStaticFields(clazz)) {
				if (matchesSignature(field.getType())) {
					try {
						field.set(instance, instantiateBoundValues(injector)[0]);
					} catch (Throwable e) {
						e.printStackTrace();
						throw new BindingException ("Could not bind values to :"+field+" : "+e.toString());
					} 
					injected.add(field);
				}
			}
		}
		return injected;
	}
	

}
