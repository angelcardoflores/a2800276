package de.kuriositaet.injection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Injector {
	// private Configuration[] configurations;
	private List<Binding> bindings;

	private Stack<Class> stack;

	private static List<Class> initializedClasses = new LinkedList<Class>();

	public Injector(Configuration... configurations) {
		// this.configurations = configurations;
		this.bindings = new LinkedList<Binding>();
		this.stack = new Stack<Class>();

		for (Configuration config : configurations) {
			for (Binding b : config.getBindings()) {
				this.bindings.add(b);
			}
		}
	}

	public <T> T createInstance(Class<T> clazz) {
		if (this.stack.contains(clazz)) {
			throw new BindingException("Circular binding definition for: "+clazz.getSimpleName());
		} else {
			stack.push(clazz);
		}
		
		T instance = null;
		for (Binding b : this.bindings) {
			if (b.hasMatchingConstructor(clazz)) {
				if (instance != null) {
					throw new BindingException(
							"Redundant contructor matching in: " + b);
				}
				instance = b.createInstance(this, clazz);
			}
		}

		// didn't find a constructor for injection
		if (instance == null) {
			try {
				instance = clazz.newInstance();
			} catch (Throwable e) {
				//e.printStackTrace();
				throw new BindingException("Could not instantiate: "
						+ clazz.getSimpleName() + " caught: " + e.toString());
			}
		}

		if (!staticMembersInitialized(clazz)) {
			boolean f = injectStaticFields(instance);
			boolean m = injectStaticMethods(instance);
			if (f || m) initializedClasses.add(clazz);
		}

		injectFields(instance);
		injectMethods(instance);
		
		stack.pop();
		return instance;
	}
	
	

	private boolean staticMembersInitialized(Class clazz) {
		//System.out.println(clazz + "--" +initializedClasses.contains(clazz));
		
		return initializedClasses.contains(clazz);
	}

	private boolean injectStaticFields(Object instance) {
		List<Field> injectedFields = new LinkedList<Field>();
		boolean injection = false;
		for (Binding binding : this.bindings) {
			List<Field> injected = binding.injectStaticFields(this, instance);
			if (contains(injectedFields, injected)) {
				throw new BindingException("Redundant static field binding in:"
						+ binding);
			} else {
				injectedFields.addAll(injected);
				injection = injected.size() > 0;
			}
		}
		return injection;
	}

	private boolean injectStaticMethods(Object instance) {
		List<Method> injectedMethods = new LinkedList<Method>();
		boolean injection = false;
		for (Binding binding : this.bindings) {
			List<Method> injected = binding.injectStaticMethods(this, instance);
			if (contains(injectedMethods, injected)) {
				throw new BindingException("Redundant static method binding in:"
						+ binding);
			} else {
				injectedMethods.addAll(injected);
				injection = injected.size()>0;
			}
		}
		return injection;
	}

	private void injectFields(Object instance) {
		List<Field> injectedFields = new LinkedList<Field>();
		for (Binding binding : this.bindings) {
			List<Field> injected = binding.injectFields(this, instance);
			if (contains(injectedFields, injected)) {
				throw new BindingException("Redundant static field binding in:"
						+ binding);
			} else {
				injectedFields.addAll(injected);
			}
		}
	}

	private void injectMethods(Object instance) {

		List<Method> injectedMethods = new LinkedList<Method>();
		for (Binding binding : this.bindings) {
			List<Method> injected = binding.injectMethods(this, instance);
			if (contains(injectedMethods, injected)) {
				throw new BindingException("Redundant static method binding in:"
						+ binding);
			} else {
				injectedMethods.addAll(injected);
			}
		}
	}

	private static boolean contains(List oldList, List newList) {
		for (Object obj : newList) {
			if (oldList.contains(obj))
				return true;
		}
		return false;
	}

}
