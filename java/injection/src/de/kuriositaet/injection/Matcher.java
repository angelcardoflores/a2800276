package de.kuriositaet.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Matcher {
	private boolean matchClasses;

	private boolean matchSubclasses;

	private boolean matchPackage;

	private boolean matchImplementation;

	private boolean matchConstructors;

	private boolean matchFields;

	private boolean matchStaticFields;

	private boolean matchMethods;

	private boolean matchStaticMethods;

	private List<Class> explicitClassMatches;

	private List<Pattern> classPatterns;

	private boolean matchExplicitClasses;

	private List<Class> superclasses;

	private List<String> packages;

	private List<Class> interfaces;

	private List<Pattern> fieldsPatterns;

	private List<Pattern> staticFieldPatterns;

	private List<Pattern> methodPatterns;

	private List<Pattern> staticMethodPatterns;
	
	public Matcher () {
		
		this.explicitClassMatches 	= new LinkedList<Class>();
		this.classPatterns			= new LinkedList<Pattern>();
		this.superclasses			= new LinkedList<Class>();
		this.packages				= new LinkedList<String>();
		this.interfaces				= new LinkedList<Class>();
		this.fieldsPatterns			= new LinkedList<Pattern>();
		this.staticFieldPatterns	= new LinkedList<Pattern>();
		this.methodPatterns			= new LinkedList<Pattern>();
		this.staticMethodPatterns	= new LinkedList<Pattern>();
	}
	
	private static <T> void addToList (List<T> list, T [] objs) {
		for(T obj : objs) {
			list.add(obj);
		}
	}

	/**
	 * Classes added through this methods are explicitly matched, any classes
	 * added by this method are gurannteed to match.
	 * 
	 * @param clazz
	 * @return
	 */
	public Matcher forClass(Class... classes) {
		this.matchExplicitClasses = true;
		addToList (this.explicitClassMatches, classes);
		return this;
	}

	/**
	 * Classes whose name matches the passed regular expression will match. This
	 * match will be combined with the results of other tests, e.g. if a package
	 * is provided, a class will have to be in one of the provided packages AND
	 * match the pattern.
	 * 
	 * @param pattern
	 * @return
	 */
	public Matcher forClasses(Pattern... patterns) {
		this.matchClasses = true;
		addToList(this.classPatterns, patterns);
		return this;
	}

	/**
	 * Classes need to be a subclass of one of the provided classes and match
	 * the other provided filters.
	 * 
	 * @param clazz
	 * @return
	 */
	public Matcher forSubclassesOf(Class... superclasses) {
		this.matchSubclasses = true;
		addToList(this.superclasses, superclasses);
		return this;
	}

	

	public Matcher forPackage(String... packages) {
		this.matchPackage = true;
		addToList(this.packages, packages);
		return this;
	}

	

	public Matcher forImplementationsOf(Class... interfaces) {
		this.matchImplementation = true;
		addToList(this.interfaces, interfaces);
		return this;
	}

	public Matcher forConstructors() {
		this.matchConstructors = true;
		return this;
	}

	public Matcher forFields() {
		this.matchFields = true;
		return this;
	}

	public Matcher forFields(Pattern... patterns) {
		this.matchFields = true;
		addToList(this.fieldsPatterns, patterns);
		return this;
	}

	public Matcher forStaticFields() {
		this.matchStaticFields = true;
		return this;
	}

	public Matcher forStaticFields(Pattern... patterns) {
		this.matchStaticFields = true;
		addToList(this.staticFieldPatterns, patterns);
		return this;
	}

	public Matcher forMethods() {
		this.matchMethods = true;
		return this;
	}

	public Matcher forMethods(Pattern... patterns) {
		this.matchMethods = true;
		addToList(this.methodPatterns, patterns);
		return this;
	}

	public Matcher forStaticMethods() {
		this.matchStaticMethods = true;
		return this;
	}

	public Matcher forStaticMethods(Pattern... patterns) {
		this.matchStaticMethods = true;
		addToList(this.staticMethodPatterns, patterns);
		return this;
	}

	public boolean matches(Class clazz) {
		if (classMatches(clazz)){
			if (fieldsMatch(clazz)) return true;
			if (constructorsMatch(clazz)) return true;
			if (methodsMatch(clazz)) return true;
		}
		return false;
	}
	private boolean classMatches(Class clazz) {
		if (this.explicitClassMatches.contains(clazz))
			return true;
		if (matchPackage(clazz)){
			if (matchSubclass(clazz)){
				if (matchInterface(clazz)){
					if(matchClassPattern(clazz)){
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean matchPackage (Class clazz) {
		if (!this.matchPackage) return true; // not matching packages.
		String packageName = clazz.getPackage().getName();
		return this.packages.contains(packageName);
	}
	private boolean matchSubclass(Class clazz) {
		if (!this.matchSubclasses) return true;
		for (Class c : this.superclasses) {
			if (isSubclassOf(c, clazz)) {
				return true;
			}
		}
		return false;
	}
	private static boolean isSubclassOf(Class superC, Class test) {
		Class c = null;
		while ((c=test.getSuperclass()) != null) {
			if (c==superC)
				return true;
		}
		return false;
	}
	private boolean matchInterface(Class clazz) {
		if (!this.matchImplementation) return true;
		for (Class interf : clazz.getInterfaces()) {
			if(this.interfaces.contains(interf)){
				return true;
			}
		}
		return false;
	}
	private boolean matchClassPattern(Class clazz) {
		if (!this.matchClasses) return true;
		String className = clazz.getSimpleName();
		return matchesPattern(this.classPatterns, className);
	}
	
	private boolean fieldsMatch(Class clazz) {
		if (this.matchFields) {
			if (internalFieldMatch(clazz, this.fieldsPatterns)){
				return true;
			}
		}
		
		if (this.matchStaticFields){
			if (internalFieldMatch(clazz, this.staticFieldPatterns)) {
				return true;
			}
		}
		return false;
		
	}
	
	private boolean methodsMatch (Class clazz) {
		if (this.matchMethods) {
			
		}
		
		if (this.matchStaticMethods){
			
		}
		
		return false
	}
	
	private boolean constructorsMatch (Class clazz) {
		if (!matchConstructors) return false;
		for (Constructor c :  clazz.getConstructors()) {
			int modifier = c.getModifiers();
			if (Modifier.isPublic(modifier) && c.getParameterTypes().length != 0) {
				return true;
			}
		}
		return false;
	}

	private boolean internalFieldMatch(Class clazz, List<Pattern> patterns) {
		if (patterns.size() == 0) {
			if (hasPublicFields(clazz)) return true;
		} else {
			for (Field field: clazz.getDeclaredFields()) {
				int modifier = field.getModifiers();
				if (!Modifier.isStatic(modifier) && Modifier.isPublic(modifier)) {
					if (matchesPattern(patterns, field.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static boolean hasPublicFields (Class clazz) {
		for (Field field: clazz.getDeclaredFields()){
			int modifiers = field.getModifiers();
			if (!Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
				return true;
			}
		}
	}
	
	private static boolean matchesPattern (List<Pattern> list, String str) {
		java.util.regex.Matcher m = null;
		for (Pattern pattern : list) {
			m = pattern.matcher(str);
			if (m.matches()) {
				return true;
			}
		}
	}
}
