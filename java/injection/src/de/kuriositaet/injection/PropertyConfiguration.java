package de.kuriositaet.injection;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.kuriositaet.injection.properties.MultiProperties;
/**
 * 		matcher.name.classes.explicit = 'com.example.Example', 'com.example.Example2'
		matcher.name.classes.children = <class names for subclasses>
		matcher.name.classes.regexp 
		matcher.name.classes.implementations

		matcher.name.members.constructors
		matcher.name.members.methods
		matcher.name.members.staticMethods
		matcher.name.members.fields
		matcher.name.members.staticFields

		binding.name.signature
		binding.name.values = "test", 123, 1.3423, true, 'com.example.Example'
		binding.name.matchers
		binding.name.bindings

 * @author tim
 *
 */
public class PropertyConfiguration extends Configuration {

	private MultiProperties properties;
	private Map<String, Matcher> matchers;
	private Map<String, Binding> bindings;
	private List<Binding> exportedBindings;

	public PropertyConfiguration(MultiProperties properties) {
		this.properties = properties;
		createMatchers();
		this.exportedBindings=new LinkedList<Binding>();
		createBindings();
		Binding [] cast = new Binding[0];
		this.setBindings(exportedBindings.toArray(cast));
	}

	private void createBindings() {
		MultiProperties bindings = this.properties.getSubProperties("binding");
		if (bindings.size() == 0) {
			throw new BindingException("no bindings defined in properties");
		}
		
		this.bindings = new HashMap<String, Binding>();
		for (String name : bindings.getSubPropertyKeys()) {
			this.bindings.put(name, createBinding(bindings.getSubProperties(name)));
		}
		
	}

	private Binding createBinding(MultiProperties subProperties) {
		
		String signature = subProperties.getProperty("signature");
		Binding binding = null;
		try {
			binding = Binding.class.newInstance();
			binding.setSignature(getClasses(signature));
		} catch (Exception e) {
			throw new BindingException("Couldn't instantiate binding...");
		} 
		
		String [] matcherNames = getStrings(subProperties.getProperty("matchers"));
		for (String matcher : matcherNames) {
			binding.to(this.matchers.get(matcher));
		}
		
		
		String [] bindingNames = getStrings(subProperties.getProperty("bindings"));
		for (String name : bindingNames) {
			Binding b = this.bindings.get(name);
			if (b==null) {
				throw new BindingException("Binding name: "+name+" not defined in Properties");
			}
			binding.bind(b);
		}
		
		String [] values = getStrings(subProperties.getProperty("values"));
		Object [] objs = new Object[values.length];
		for (int i=0; i!=values.length; ++i) {
			if (values[i].startsWith("\"")) {
				// it's a string.
				objs[i] = values[i].replaceAll("\"", "");
			} else {
				try {
					objs[i] = Integer.parseInt(values[i]);
				} catch (NumberFormatException nfe) {
					// not and integer....
				}
				try {
					objs[i] = Double.parseDouble(values[i]);
				} catch (NumberFormatException nfe) {
					// not a Double either
				}
				
				//finally for types, try boolean
				if ("true".equalsIgnoreCase(values[i]) || "false".equalsIgnoreCase(values[i])) {
					objs[i] = "true".equalsIgnoreCase(values[i]);
				} else {
					// at last, we have to treat it as a classname
					
					try {
						objs[i] = Class.forName(values[i]);
					} catch (ClassNotFoundException e) {
						throw new BindingException("Can't handle binding value: "+values[i]);
					}
					
				}	
			} // for
			binding.bind(objs);
		}
		
		
		if (binding.hasMatchers()) {
			this.exportedBindings.add(binding);
		}
		return binding;
	}
	

	private void createMatchers() {
		MultiProperties matchers = this.properties.getSubProperties("matcher");
		if (matchers.size() == 0) {
			throw new BindingException("no matcher defined in properties");
		}
		this.matchers = new HashMap<String, Matcher>();
		for (String name : matchers.getSubPropertyKeys()) {
			this.matchers.put(name, createMatcher(matchers.getSubProperties(name)));
		}
		
	}

	/**
	 * called by createMatchers.
	 * @param subProperties
	 */
	private Matcher createMatcher(MultiProperties subProperties) {
		Matcher matcher = new Matcher();
		MultiProperties classProperties=subProperties.getSubProperties("classes");
		String explicit = classProperties.getProperty("explicit");
		String children = classProperties.getProperty("children");
		String regexp = classProperties.getProperty("regexp");
		String implementation = classProperties.getProperty("implementation");
		
		matcher.forClass(getClasses(explicit));
		matcher.forSubclassesOf(getClasses(children));
		matcher.forImplementationsOf(getClasses(implementation));
		matcher.forClasses(getRegexps(regexp));
		
		MultiProperties memberProperties = subProperties.getSubProperties("members");
		String constructors = memberProperties.getProperty("constructors");
		String methods = memberProperties.getProperty("methods");
		String staticMethods = memberProperties.getProperty("staticMethods");
		String fields = memberProperties.getProperty("fields");
		String staticFields = memberProperties.getProperty("staticFields");
		
		if ("true".equalsIgnoreCase(constructors)) {
			matcher.forConstructors();
		}
		if ("true".equalsIgnoreCase(methods)) {
			matcher.forMethods();
		} else {
			if (null != methods) {
				matcher.forMethods(getRegexps(methods));
			}
		}
		
		if ("true".equalsIgnoreCase(staticMethods)) {
			matcher.forStaticMethods();
		} else {
			if (null != staticMethods) {
				matcher.forStaticMethods(getRegexps(staticMethods));
			}
		}
		
		if ("true".equalsIgnoreCase(fields)) {
			matcher.forFields();
		} else {
			if (null != fields) {
				matcher.forFields(getRegexps(fields));
			}
		}
		if ("true".equalsIgnoreCase(staticFields)) {
			matcher.forStaticFields();
		} else {
			if (null != staticFields) {
				matcher.forStaticFields(getRegexps(staticFields));
			}
		}
		return matcher;
		
		
		
		
		
	}

	private Pattern[] getRegexps(String regexp) {
		String [] regs = getStrings(regexp);
		Pattern [] patterns = new Pattern [regs.length];
		for(int i = 0; i!=regs.length; ++i) {
			patterns[i] = Pattern.compile(regs[i]);
		}
		return patterns;
	}

	private Class [] getClasses(String str) {
		String [] classNames = getStrings(str);
		Class [] classes = new Class[classNames.length];
		String className = null;
		for (int i=0; i!=classes.length; ++i) {
			try {
				classes[i] = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new BindingException("Class: "+className+"not found.", e);
			}
		}
		return classes;
	}
	private String [] getStrings (String str) {
		String [] each = str.split(",");
		String [] noQuotes = new String[each.length];
		for (int i=0; i!=each.length; ++i) {
			noQuotes[i] = each[i].replaceAll(",", "");
		}
		return noQuotes;
	}

}
