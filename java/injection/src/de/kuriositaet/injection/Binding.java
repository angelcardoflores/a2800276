package de.kuriositaet.injection;

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
	
	public Binding bind (Object [] objs) {
		if (objs.length != signature.length)
			throw new BindingException ("Trying to bind values of length:"+objs.length+" to signature of length: "+signature.length);
		this.boundValues = objs;
		return this;
	}
	
	public Binding to (Matcher matcher) {
		this.matchers.add(matcher);
		return this;
	}
	
	public boolean bindingAvailable (Class clazz) {
		for (Matcher matcher: matchers) {
			if (matcher.matches(clazz)) return true;
		}
		return false;
	}
}
