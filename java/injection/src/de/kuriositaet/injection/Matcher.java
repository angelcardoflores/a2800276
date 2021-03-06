package de.kuriositaet.injection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Arrays;

/**
 * 
 * The Matcher class is used to define a set of members. Use `matches(Class)` to
 * determine whether a class contains members that match the definition of a
 * matcher. The matching members can be accessed using the methods:
 * `matchingConstructors(Class)`, `matchingFunctions(Class)`, etc.
 * 
 * Matcher contains two (three?) types of methods to define the match. The
 * methods `forConstructors`, `forFields(...)`, `forStaticFields(...)`,
 * `forMethods(...)` and `forStaticMethods(...)` define which types of members
 * to match. These methods can be called without parameters in order to match
 * any member, e.g. `forMethods()` will match any public instance method. These
 * methods may also be called with one or more regular expression patterns which
 * restrict the match to members who's names match the regular expression.
 * 
 * By default, all classes containing these members will be eligible for
 * matching. The second type of filter methods restricts the matches to specific
 * classes. These methods are `forClasses`, `forSubclassesOf(...)`,
 * `forPackage(...)` and `forImplemenationsOf(...)`. Each filter methods per se
 * will combine it's arguments with a boolean OR. E.g. if three interfaces are
 * passed to `forImplementationsOf(...)` classes matching any of the interfaces
 * match. Different filter methods are combined with a boolean AND. E.g. If
 * `forImplemenationsOf(...)` is called with interfaces A and B and
 * `forPackage(...)` with package 'com.example.example' any class implementing A
 * or B is matched as long as it is contained in the package
 * 'com.example.example'.
 * 
 * The (arguably) third type of class filter is the `forClass(...)` filter which
 * will match all specifically enumerated classes whether they match one of the
 * other class filters or not. The behaviour might be a little confusing: if
 * only explicit class matches and no filter like `forImplementationsOf(...)`
 * are provided the matcher only takes the specific matches into account. If
 * filters are provided, the matcher matches the explicitly named classes AND
 * any other class matching the filter. I.e. the filters aren't applied to the
 * named classes.
 * 
 * Note the the Matcher defines a set of matching members regardless of their
 * signature. The instance(s) of Binding using this matcher will only take
 * matches with the correct signature into account.
 * 
 * 
 * @author tim
 * 
 */
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

	private List<Class> superclasses;

	private List<String> packages;

	private List<Class> interfaces;

	private List<Pattern> fieldsPatterns;

	private List<Pattern> staticFieldPatterns;

	private List<Pattern> methodPatterns;

	private List<Pattern> staticMethodPatterns;

	public Matcher() {

		this.explicitClassMatches = new LinkedList<Class>();
		this.classPatterns = new LinkedList<Pattern>();
		this.superclasses = new LinkedList<Class>();
		this.packages = new LinkedList<String>();
		this.interfaces = new LinkedList<Class>();
		this.fieldsPatterns = new LinkedList<Pattern>();
		this.staticFieldPatterns = new LinkedList<Pattern>();
		this.methodPatterns = new LinkedList<Pattern>();
		this.staticMethodPatterns = new LinkedList<Pattern>();
	}

	/**
	 * Classes added through this methods are explicitly matched, any classes
	 * added by this method are guranteed to match.
	 * 
	 * @param clazz
	 * @return
	 */
	public Matcher forClass(Class... classes) {
		for (Class clazz : classes) {
			int mod = clazz.getModifiers();
			if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
				throw new MatchingException(
						clazz.getSimpleName()
								+ "is abstract or an interface and not eligible for explicit matching");
			}
		}
		this.explicitClassMatches.addAll(Arrays.asList(classes));
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
	 * restricts the match to classes whose names match any of the
	 * provided patterns.
	 * @param patterns
	 * @return
	 */
	public Matcher forClasses(String... patterns) {
		return this.forClasses(toPattern(patterns));
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
		for (Class clazz : superclasses) {
			if (Modifier.isFinal(clazz.getModifiers())) {
				throw new MatchingException(clazz.getSimpleName()
						+ " is final, can't match subclasses.");
			}
		}
		addToList(this.superclasses, superclasses);
		return this;
	}

	/**
	 * Restricts the matcher to classes in the provided packages.
	 * 
	 * @param packages
	 * @return
	 */
	public Matcher forPackages(String... packages) {
		this.matchPackage = true;
		addToList(this.packages, packages);
		return this;
	}

	/**
	 * restricts the matcher to implemenations of the passed interfaces.
	 * 
	 * @param interfaces
	 * @return
	 */
	public Matcher forImplementationsOf(Class... interfaces) {
		this.matchImplementation = true;
		for (Class inter : interfaces) {
			if (!inter.isInterface()) {
				throw new MatchingException(inter.toString()
						+ " is not an interface, can't match implementations.");
			}
		}
		addToList(this.interfaces, interfaces);
		return this;
	}

	/**
	 * finds public constructors (regardless of their signature) in matching
	 * classes.
	 * 
	 * @return
	 */
	public Matcher forConstructors() {
		this.matchConstructors = true;
		return this;
	}

	/**
	 * Finds public fields (regardless of their type) in matching classes.
	 * 
	 * @return
	 */
	public Matcher forFields() {
		this.matchFields = true;
		return this;
	}

	/**
	 * Finds public fields matching one of the passed regular expressions
	 * regardless of the fields type in matching classes
	 * 
	 * @param patterns
	 * @return
	 */
	public Matcher forFields(Pattern... patterns) {
		this.matchFields = true;
		addToList(this.fieldsPatterns, patterns);
		return this;
	}

	/**
	 * Finds public fields matching one of the passed regular expressions
	 * regardless of the fields type in matching classes
	 * 
	 * @param patterns
	 * @return
	 */
	public Matcher forFields(String... strings) {
		return forFields(toPattern(strings));
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

	public Matcher forStaticFields(String... strings) {
		return forStaticFields(toPattern(strings));
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

	public Matcher forMethods(String... strings) {
		return forMethods(toPattern(strings));
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

	public Matcher forStaticMethods(String... strings) {
		return forStaticMethods(toPattern(strings));
	}

	/**
	 * Check whether the provided class matches the definition of this Matcher.
	 * 
	 * @param clazz
	 * @return
	 */
	public boolean matches(Class clazz) {
		if (classMatches(clazz)) {
			if (fieldsMatch(clazz))
				return true;
			if (constructorsMatch(clazz))
				return true;
			if (methodsMatch(clazz))
				return true;
		}
		return false;
	}

	/**
	 * Return a list of all matching Constructors from the class. All public
	 * constructors are matching.
	 * 
	 * @param clazz
	 * @return empty array if the class itself doesn't match of no applicable
	 *         constructors are available.
	 */
	public List<Constructor> matchingConstructors(Class clazz) {
		List<Constructor> list = new LinkedList<Constructor>();
		if (!matchConstructors)
			return list;
		if (!classMatches(clazz) || !constructorsMatch(clazz))
			return list;
		for (Constructor con : clazz.getConstructors()) {
			if (Modifier.isPublic(con.getModifiers())) {
				list.add(con);
			}
		}
		return list;
	}

	/**
	 * Return a list of all non-static, public fields matching the definitions
	 * of this matcher.
	 * 
	 * @param clazz
	 * @return empty List if the class itself doesn't match or no matching
	 *         fields are available.
	 */
	public List<Field> matchingFields(Class clazz) {
		List<Field> list = new LinkedList<Field>();
		if (!matchFields)
			return list;
		if (!classMatches(clazz) || !fieldsMatch(clazz))
			return list;
		for (Field field : clazz.getDeclaredFields()) {
			int modifier = field.getModifiers();
			if (Modifier.isPublic(modifier) && !Modifier.isStatic(modifier)) {
				if (matchesPattern(this.fieldsPatterns, field.getName())) {
					list.add(field);
				}
			}
		}
		return list;
	}

	/**
	 * Returns a list of all public static fields matching the definition of
	 * this Matcher.
	 * 
	 * @param clazz
	 * @return empty List if the class itself doesn't match or no approriate
	 *         fields are available
	 */
	public List<Field> matchingStaticFields(Class clazz) {
		List<Field> list = new LinkedList<Field>();
		if (!matchStaticFields)
			return list;
		if (!classMatches(clazz) || !fieldsMatch(clazz))
			return list;
		for (Field field : clazz.getDeclaredFields()) {
			int modifier = field.getModifiers();
			if (Modifier.isPublic(modifier) && Modifier.isStatic(modifier)) {
				if (matchesPattern(this.staticFieldPatterns, field.getName())) {
					list.add(field);
				}
			}
		}
		return list;
	}

	/**
	 * Returns a list of all matching nonstatic, public methods in the class.
	 * 
	 * @param clazz
	 * @return empty list in case the class itself doesn't match or no
	 *         approriate methods are available
	 */
	public List<Method> matchingMethods(Class clazz) {
		List<Method> list = new LinkedList<Method>();
		if (!matchMethods)
			return list;
		if (!classMatches(clazz) || !methodsMatch(clazz))
			return list;
		for (Method method : clazz.getDeclaredMethods()) {
			int modifier = method.getModifiers();
			if (Modifier.isPublic(modifier) && !Modifier.isStatic(modifier)) {
				if (matchesPattern(this.methodPatterns, method.getName())) {
					list.add(method);
				}
			}
		}
		return list;
	}

	/**
	 * Returns a list of all matching, static public methods available in the
	 * class.
	 * 
	 * @param clazz
	 * @return empty list in case the class itself doesn't match or no
	 *         approriate static methods are present in the class.
	 */
	public List<Method> matchingStaticMethods(Class clazz) {
		List<Method> list = new LinkedList<Method>();
		if (!matchStaticMethods)
			return list;
		if (!classMatches(clazz) || !methodsMatch(clazz))
			return list;
		for (Method method : clazz.getDeclaredMethods()) {
			int modifier = method.getModifiers();
			if (Modifier.isPublic(modifier) && Modifier.isStatic(modifier)) {
				if (matchesPattern(this.staticMethodPatterns, method.getName())) {
					list.add(method);
				}
			}
		}
		return list;
	}

	/**
	 * internal, determine if the class is eligible, doesn't take into account
	 * if matching fields, constructors or methods are available.
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean classMatches(Class clazz) {
		if (this.explicitClassMatches.contains(clazz))
			return true;
		if (this.explicitClassMatches.size() > 0
				&& !(this.matchPackage || this.matchSubclasses
						|| this.matchImplementation || this.matchClasses)) {
			// if this matcher specifies specific classes to match and no pattern for
			// other matches, 
			return false;
		}
			if (matchPackage(clazz)) {
				if (matchSubclass(clazz)) {
					if (matchInterface(clazz)) {
						if (matchClassPattern(clazz)) {
							return true;
						}
					}
				}
			}
		return false;
	}

	/**
	 * internal, determine if the class meets the defined package criteria.
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean matchPackage(Class clazz) {
		if (!this.matchPackage)
			return true; // not matching packages.
		String packageName = clazz.getPackage().getName();
		return this.packages.contains(packageName);
	}

	/**
	 * internal, determine whether the class matches the defined subclass
	 * criteria.
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean matchSubclass(Class clazz) {
		if (!this.matchSubclasses)
			return true;

		for (Class c : this.superclasses) {
			if (isSubclassOf(c, clazz)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * internal utilty: determine whether `test` is a subclass of `superC`
	 * 
	 * @param superC
	 * @param test
	 * @return
	 */
	private static boolean isSubclassOf(Class superC, Class test) {

		Class c = null;
		while ((c = test.getSuperclass()) != null) {
			if (c.equals(superC))
				return true;
			test = c;
		}
		return false;
	}

	/**
	 * internal, determine whether the class meets the defined interface
	 * criteria.
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean matchInterface(Class clazz) {
		if (!this.matchImplementation)
			return true;
		for (Class interf : clazz.getInterfaces()) {
			if (this.interfaces.contains(interf)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * internal, determine whether the class' name matches one of the possible
	 * regular expressions.
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean matchClassPattern(Class clazz) {
		if (!this.matchClasses)
			return true;
		String className = clazz.getSimpleName();
		return matchesPattern(this.classPatterns, className);
	}

	private boolean fieldsMatch(Class clazz) {
		if (this.matchFields) {
			if (internalFieldMatch(clazz, this.fieldsPatterns, false)) {
				return true;
			}
		}

		if (this.matchStaticFields) {
			if (internalFieldMatch(clazz, this.staticFieldPatterns, true)) {
				return true;
			}
		}
		return false;

	}

	private boolean methodsMatch(Class clazz) {
		if (this.matchMethods) {
			if (internalMethodMatch(clazz, this.methodPatterns, false)) {
				return true;
			}
		}

		if (this.matchStaticMethods) {
			if (internalMethodMatch(clazz, this.staticMethodPatterns, true)) {
				return true;
			}
		}

		return false;
	}

	private boolean constructorsMatch(Class clazz) {
		if (!matchConstructors)
			return false;
		for (Constructor c : clazz.getConstructors()) {
			int modifier = c.getModifiers();
			if (Modifier.isPublic(modifier)) {
				return true;
			}
		}
		return false;
	}

	private boolean internalFieldMatch(Class clazz, List<Pattern> patterns,
			boolean checkStatic) {
		if (patterns.size() == 0) {
			if (hasPublicFields(clazz, checkStatic))
				return true;
		} else {
			for (Field field : clazz.getDeclaredFields()) {
				int modifier = field.getModifiers();
				boolean st = checkStatic ? Modifier.isStatic(modifier)
						: !Modifier.isStatic(modifier);
				if (st && Modifier.isPublic(modifier)) {
					if (matchesPattern(patterns, field.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean internalMethodMatch(Class clazz, List<Pattern> patterns,
			boolean checkStatic) {
		if (patterns.size() == 0) {
			if (hasPublicMethods(clazz, checkStatic))
				return true;
		} else {
			for (Method method : clazz.getDeclaredMethods()) {
				int modifier = method.getModifiers();
				boolean st = checkStatic ? Modifier.isStatic(modifier)
						: !Modifier.isStatic(modifier);
				if (st && Modifier.isPublic(modifier)) {
					//System.out.println("here: "+method+" : "+Modifier.isStatic(modifier)+":"+st+" : cs:"+checkStatic);
					if (matchesPattern(patterns, method.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static Pattern[] toPattern(String[] str) {
		Pattern[] patterns = new Pattern[str.length];
		for (int i = 0; i != str.length; ++i) {
			patterns[i] = Pattern.compile(str[i]);
		}
		return patterns;
	}

	/**
	 * Utility to add all elements of an array to a list.
	 * 
	 * @param <T>
	 * @param list
	 * @param objs
	 */
	private static <T> void addToList(List<T> list, T[] objs) {
		for (T obj : objs) {
			list.add(obj);
		}
	}

	private static boolean hasPublicFields(Class clazz, boolean checkStatic) {
		for (Field field : clazz.getDeclaredFields()) {
			int modifiers = field.getModifiers();
			boolean st = checkStatic ? Modifier.isStatic(modifiers) : !Modifier
					.isStatic(modifiers);
			if (st && Modifier.isPublic(modifiers)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasPublicMethods(Class clazz, boolean checkStatic) {
		for (Method method : clazz.getDeclaredMethods()) {
			int modifiers = method.getModifiers();
			boolean st = checkStatic ? Modifier.isStatic(modifiers) : !Modifier
					.isStatic(modifiers);
			if (st && Modifier.isPublic(modifiers)) {
				return true;
			}

		}
		return false;
	}

	private static boolean matchesPattern(List<Pattern> list, String str) {
		if (list.size() == 0)
			return true;
		java.util.regex.Matcher m = null;
		for (Pattern pattern : list) {

			m = pattern.matcher(str);
			//System.out.println(pattern+":"+str+":"+m.matches());
			
			if (m.matches()) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		String str = "";
		str += "Matcher:";
		str += "\n\tforConstructors? "	+this.matchConstructors;
		str += "\n\tforFields? "		+this.matchFields;
		str += "\n\tforStaticFields? "	+this.matchStaticFields;
		str += "\n\tforMethods? "		+this.matchMethods;
		str += "\n\tforStaticMethods? "	+this.matchStaticMethods;
		str += "\n\tforPackages? "		+this.matchPackage;
		str += "\n";
		if (this.matchFields && this.fieldsPatterns.size() > 0) {
			str += "Field Patterns: \n";
			str += "---------------";
			for (Pattern p : this.fieldsPatterns) {
				str +="\n\t"+p;
			}
			str += "\n---------------";
		}
		if (this.matchStaticFields && this.staticFieldPatterns.size() > 0) {
			str += "Static Field Patterns: \n";
			str += "----------------------";
			for (Pattern p : this.staticFieldPatterns) {
				str +="\n\t"+p;
			}
			str += "\n----------------------";
		}
		if (this.matchMethods && this.methodPatterns.size() >0) {
			str += "Method Patterns: \n";
			str += "---------------";
			for (Pattern p : this.methodPatterns) {
				str +="\n\t"+p;
			}
			str += "\n---------------";
		}
		if (this.matchStaticMethods && this.staticMethodPatterns.size() >0) {
			str += "Static Method Patterns: \n";
			str += "-----------------------";
			for (Pattern p : this.staticMethodPatterns) {
				str +="\n\t"+p;
			}
			str += "\n-----------------------";
		}
		if (this.matchPackage && this.packages.size() >0) {
			str += "Packages: \n";
			str += "-----------------------";
			for (String p : this.packages) {
				str +="\n\t"+p;
			}
			str += "\n-----------------------";
		}
		return str;
	}
}
