package de.kuriositaet.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A Binding describes a signature of a class member and what concrete values
 * should be bound to it. The 'signature' component of the binding is normally
 * defined in the constructor. E.g. creating a Binding like this:
 * 
 * <pre>
 * 		Binding binding  = new Binding(String.class); // A
 * 		Binding binding2 = new Binding(TestInterface.class, String.class) // B
 * </pre>
 * 
 * creates bindings that could be applied to public (static) String fields, or
 * public (static) methods and constructors that take a single String as their
 * parameter in the case of Example A. Example B could only be applied to
 * constructors or methods because it has two parameters and thus can't be
 * applied to fields.
 * 
 * Next the Binding object needs to be told what should be bound to the
 * signature. This can be done in a number of ways. The easiest is to tell the
 * binding which classes provide the implementation for the 'signature',
 * following the above example:
 * 
 * <pre>
 * 		binding.bind(String.class); // A
 * 		binding2.bind(TestClass.class, String.class); // B
 * </pre>
 * 
 * In Example A, a new instance of java.lang.String will be bound to whatever
 * member (field, method or constructor) the binding matches. The new instance
 * of String will be constructed by the injection framework and have all it's
 * bindings (if defined) injected automatically. In Example B, TestClass, which
 * implements TestInterface will be bound to the first element, and String to
 * the second.
 * 
 * It's also possible to inject complete instances or mix the two:
 * 
 * <pre>
 * 		binding.bind(&quot;123&quot;); // A
 * 		binding2.bind(new TestClass(), String.class); // B
 * </pre>
 * 
 * Example A will always inject the same instance of String ("123"). Example B
 * will always inject the same instance of TestClass, but create new instances
 * of String via the injection framework for each injection.
 * 
 * Using preinstantiated instances has the advantage of not wasting resources,
 * but at the cost of losing the advantages of injection itself. In order for
 * the injected values to be constructed by the injection framework and still
 * only inject single instances, a binder can be made singleton.
 * 
 * <pre>
 * 		binding.bind(String.class).singleton();
 * </pre>
 * 
 * In the example above, the framework will instantiate a new String object and
 * inject all dependancies, but it will only create it once an reuse the
 * instance in further injections.
 * 
 * If you wish to have one injected parameter be singleton and another to be
 * freshly created upon each injection, you have to bind Bindings instead of
 * classes or preinitialized instances of classes:
 * 
 * <pre>
 * 		Binding b1 = new Binding(String.class).bind(String.class);
 * 		Binding b2 = new Binding(String.class).bind(&quot;123&quot;);
 * 		Binding b3 = new Binding(String.class).bind(String.class).singleton();
 * 		Binding binding = new Binding(String.class, String.class, String.class).bind(b1, b2, b3);
 * </pre>
 * 
 * The binding in the example above could be apllied to any member that takes
 * three String parameters. The first parameter would be injected with an
 * instance of String that's created with each injection. The second parameter
 * would always be passed "123" and the third String would be created by the
 * injection framework at the first injection and would be subsequently reused
 * for each further injection.
 * 
 * Finally, the binding needs to be told what members of which classes it should
 * be applied to. To achieve this, one or more instances of Matcher need to be
 * passed to the to(...) method.
 * 
 * @author tim
 * 
 */
public class Binding {
	private Class[] signature;

	private Object[] boundValues;

	private List<Binding> subBindings;

	private boolean usingBindings;

	private List<Matcher> matchers;

	/**
	 * Used to store object instances if this Binding is scoped Singleton.
	 */
	private Map<Class, Object> instances;

	private boolean singleton;

	/**
	 * The constructor is passed the <i>signature</i> of the members we're
	 * interested in binding. In the case of field injection, the signature is
	 * the type of the field we'd like to inject, in case of constructor or
	 * method injection, the signature is that of the method or constructor.
	 * 
	 * @param classes
	 */
	public Binding(Class... classes) {
		if (classes == null || classes.length == 0)
			throw new BindingException(
					"Can't create binding for 0 length signature.");
		this.signature = classes;
		this.matchers = new LinkedList<Matcher>();
	}

	/**
	 * This constructor is used internally in conjunction with `setSignature` in
	 * cases where Bindings are automatically constructed. Currently, this is
	 * the case in PropertyConfiguration.
	 */
	protected Binding() {
		this.matchers = new LinkedList<Matcher>();
	}

	/**
	 * This methods is used internally in conjunction with the protected default
	 * constructor in cases where Bindings are automatically constructed.
	 * Currently, this is the case in PropertyConfiguration.
	 */
	protected void setSignature(Class... classes) {
		if (this.signature != null) {
			throw new BindingException("Signature already initialized");
		}
		if (null == classes || classes.length == 0) {
			throw new BindingException(
					"Can't create binding for 0 length signature.");
		}

		this.signature = classes;
	}

	/**
	 * Inform the binding what types of objects it should inject into the
	 * signature. This can be a mix of Class objects, which are automatically
	 * instanitated by the framework and preinstantiated instances of object
	 * that should be injected.
	 * 
	 * N.B. you can only bind Bindings OR bind objects and classes, you can't
	 * mix the two in a single binding.
	 * 
	 * @param objs
	 * @throws BindingException
	 *             if attempting to mix binding Objects and binding subbindings
	 * @throws BindingException
	 *             if the binding more subbindings than is the length of the
	 *             signature
	 * @return
	 */
	public Binding bind(Object... objs) {
		if (this.usingBindings) {
			throw new BindingException(
					"Can't mix binding Classes and binding Bindings.");
		}
		if (objs.length != signature.length)
			throw new BindingException("Trying to bind values of length:"
					+ objs.length + " to signature of length: "
					+ signature.length);
		// TODO check types coincide with signature ... ?
		this.boundValues = objs;
		return this;
	}

	/**
	 * This variant of the the `bind` method can be used to combine various
	 * Bindings. N.B. you can only bind Bindings OR bind objects and classes,
	 * you can't mix the two in a single binding.
	 * 
	 * @param bindings
	 * @throws BindingException
	 *             if attempting to mix binding Objects and binding subbindings
	 * @throws BindingException
	 *             if the binding more subbindings than is the length of the
	 *             signature
	 * @return
	 */
	public Binding bind(Binding... bindings) {
		if (this.boundValues != null) {
			throw new BindingException(
					"Can't mix binding Classes and binding Bindings.");
		}

		if (this.subBindings == null) {
			this.subBindings = new LinkedList<Binding>();
		}

		// add to (existing) list of subbindings.
		for (Binding b : bindings) {
			this.subBindings.add(b);
		}

		int sigSize = 0;
		for (Binding b : this.subBindings) {
			sigSize += b.getSignature().length;
		}

		if (sigSize > this.getSignature().length) {
			throw new BindingException("Signatures of subbindings too long.");
		}

		// check that new list of subbindings corresponds to signature.

		int i = 0;
		for (Binding b : this.subBindings) {
			for (Class c : b.getSignature()) {
				if (!c.equals(this.getSignature()[i++])) {
					throw new BindingException(
							"Signature of subbindings doesn't match. Found:"
									+ c.getSimpleName() + " expected:"
									+ this.getSignature()[--i]);
				}
			}
		}

		this.usingBindings = true;
		return this;
	}
	
	/**
	 * Specify a filter to select which classes and members this binding is to be
	 * applied to.
	 * @param matcher
	 * @return
	 */
	public Binding to(Matcher matcher) {
		this.matchers.add(matcher);
		return this;
	}

	/**
	 * Makes this binding singleton. I.e. a single instance of the class to
	 * inject is created through the injection framework and will be reused for
	 * all further injections.
	 * 
	 * @return
	 */
	public Binding singleton() {
		this.singleton = true;
		this.instances = new HashMap<Class, Object>();
		return this;
	}

	/**
	 * Check whether the parameter class matches this bindings matchers and has
	 * members with the appropriate signature. I.e. is the class eligible for
	 * injection via this binding.
	 * @param clazz
	 * @return
	 */
	public boolean matches(Class clazz) {
		for (Matcher matcher : matchers) {
			if (matcher.matches(clazz)) {
				if (matchesSignature(matcher, clazz))
					return true;
			}
		}
		return false;
	}

	private boolean matchesSignature(Matcher matcher, Class clazz) {
		if (matchesConstrutor(matcher, clazz))
			return true;
		if (matchesFields(matcher, clazz))
			return true;
		if (matchesStaticFields(matcher, clazz))
			return true;
		if (matchesMethods(matcher, clazz))
			return true;
		if (matchesStaticMethods(matcher, clazz))
			return true;
		return false;
	}

	/**
	 * Check whether the class has a constructor that matches this binding's
	 * matchers and whether that constructor has an approriate signature.
	 * @param clazz
	 * @return
	 */
	public boolean hasMatchingConstructor(Class clazz) {

		for (Matcher m : this.matchers) {
			if (!m.matches(clazz))
				return false;
			if (matchesConstrutor(m, clazz))
				return true;
		}
		return false;
	}

	private boolean matchesStaticFields(Matcher matcher, Class clazz) {

		for (Field field : clazz.getDeclaredFields()) {
			Class[] sig = new Class[1];
			sig[0] = field.getType();
			if (!Modifier.isStatic(field.getModifiers())
					&& matchesSignature(sig)) {
				return true;
			}
		}

		return false;
	}

	private boolean matchesMethods(Matcher matcher, Class clazz) {
		for (Method method : matcher.matchingMethods(clazz)) {
			if (!Modifier.isStatic(method.getModifiers())
					&& matchesSignature(method.getParameterTypes())) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesFields(Matcher matcher, Class clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Class[] sig = new Class[1];
			sig[0] = field.getType();
			if (!Modifier.isStatic(field.getModifiers())
					&& matchesSignature(sig)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesConstrutor(Matcher matcher, Class clazz) {
		for (Constructor constructor : matcher.matchingConstructors(clazz)) {
			if (matchesSignature(constructor.getParameterTypes())) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesStaticMethods(Matcher matcher, Class clazz) {
		for (Method method : matcher.matchingMethods(clazz)) {
			if (Modifier.isStatic(method.getModifiers())
					&& matchesSignature(method.getParameterTypes())) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesSignature(Class... signature) {
		return Arrays.equals(this.signature, signature);
	}

	protected <T> T createInstance(Injector i, Class<T> clazz) {
		if (!matches(clazz)) {
			throw new BindingException("Class:" + clazz.getName()
					+ " does not match. Cannot create instance.");
		}

		if (!hasMatchingConstructor(clazz)) {
			throw new BindingException("Class:" + clazz.getName()
					+ " has no matching Construtor. Cannot create instance.");
		}

		T instance = null;
		for (Matcher matcher : this.matchers) {
			List<Constructor> constructors = matcher
					.matchingConstructors(clazz);
			for (Constructor cons : constructors) {
				if (matchesSignature(cons.getParameterTypes())) {
					Object[] parameters = instantiateBoundValues(i);
					try {
						instance = (T) cons.newInstance(parameters);
					} catch (Throwable e) {
						// e.printStackTrace();
						throw new BindingException("Could not instantiate: "
								+ clazz.getSimpleName() + " caught: "
								+ e.toString());
					}
				}
			}
		}
		return instance;

	}

	/**
	 * Instantiate objects to inject. If this binding uses subbindings, object
	 * instantiation is delegated to the subbindings. If preinstantiated objects
	 * were bound, those are returned. Else the binding will create new instances
	 * using the injector that's passed in.
	 * 
	 * @param inj
	 * @return
	 */
	private Object[] instantiateBoundValues(Injector inj) {
		if (this.usingBindings) {
			return instantiateWithBindings(inj);
		}
		Object[] values = new Object[boundValues.length];
		Object obj = null;
		for (int i = 0; i != boundValues.length; ++i) {
			if (boundValues[i] instanceof Class) {
				if (this.singleton
						&& this.instances.containsKey(boundValues[i])) {
					obj = this.instances.get(boundValues[i]);
				} else {
					obj = inj.createInstance((Class) boundValues[i]);
					if (this.singleton) {
						this.instances.put((Class) boundValues[i], obj);
					}
				}
			} else {
				obj = boundValues[i];
			}
			values[i] = obj;
		}
		return values;
	}

	private Object[] instantiateWithBindings(Injector inj) {
		Object[] values = new Object[0];
		for (Binding b : this.subBindings) {
			values = concatenate(values, b.instantiateBoundValues(inj));
		}
		return values;
	}

	/**
	 * Utility, concatenate two arrays.
	 * @param values
	 * @param objects
	 * @return
	 */
	private static Object[] concatenate(Object[] values, Object[] objects) {
		Object[] newArray = new Object[values.length + objects.length];
		System.arraycopy(values, 0, newArray, 0, values.length);
		System.arraycopy(objects, 0, newArray, values.length, objects.length);
		return newArray;

	}

	protected List<Method> injectMethods(Injector injector, Object instance) {
		List<Method> injected = new LinkedList<Method>();
		Class clazz = instance.getClass();
		for (Matcher m : this.matchers) {
			for (Method method : m.matchingMethods(clazz)) {
				if (matchesSignature(method.getParameterTypes())) {
					try {
						method.invoke(instance,
								instantiateBoundValues(injector));
					} catch (Throwable e) {
						// e.printStackTrace();
						throw new BindingException("Could not bind values to :"
								+ method + " : " + e.toString());
					}
					injected.add(method);
				}
			}
		}
		return injected;
	}

	protected List<Method> injectStaticMethods(Injector injector,
			Object instance) {
		List<Method> injected = new LinkedList<Method>();
		Class clazz = instance.getClass();
		for (Matcher m : this.matchers) {
			for (Method method : m.matchingStaticMethods(clazz)) {
				if (matchesSignature(method.getParameterTypes())) {
					try {
						method.invoke(instance,
								instantiateBoundValues(injector));
					} catch (Throwable e) {
						// e.printStackTrace();
						throw new BindingException("Could not bind values to :"
								+ method + " : " + e.toString());
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
						field
								.set(instance,
										instantiateBoundValues(injector)[0]);
					} catch (Throwable e) {
						// e.printStackTrace();
						throw new BindingException("Could not bind values to :"
								+ field + " : " + e.toString());
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
						field
								.set(instance,
										instantiateBoundValues(injector)[0]);
					} catch (Throwable e) {
						// e.printStackTrace();
						throw new BindingException("Could not bind values to :"
								+ field + " : " + e.toString());
					}
					injected.add(field);
				}
			}
		}
		return injected;
	}

	public Class[] getSignature() {
		return signature;
	}

	public boolean hasMatchers() {
		return matchers.size() != 0;
	}

	public String toString() {
		String str = "";
		str += "Binding. Signature:\n";
		for (Class c : this.getSignature()) {
			str += "\t" + c.getSimpleName() + "\n";
		}
		str += "singleton: " + this.singleton + "\n";

		if (this.usingBindings) {
			str += "uses Subbindings:\n";
			str += "-----------------";
			for (Binding b : this.subBindings) {
				str += "\n\t" + b.toString();
			}
			str += "-----------------\n";
		} else {
			str += "uses direct Bindings:\n";
			str += "---------------------";
			for (Object o : this.boundValues) {
				if (o instanceof Class) {
					str += "\n\t" + ((Class) o).getSimpleName();
				} else {
					str += "\n\t" + o.toString() + ":"
							+ (o.getClass().getSimpleName());
				}
			}
			str += "\n---------------------\n";
		}

		if (this.matchers.size() > 0) {
			str += "using matchers:\n";
			str += "---------------";
			for (Matcher m : this.matchers) {
				str += "\n" + m.toString();
			}
			str += "\n---------------\n";
		}

		return str;
	}

}
