package utils;

import java.lang.reflect.*;
public class ReflectionPrinter {
	
	/**
	 * Takes an object and dumps the value of all methods 
	 * that return a String.
	 * @param obj
	 * @return
	 */
	public static String toString (Object obj) {
		StringBuffer buf = new StringBuffer ();
		
		try {
			
		Class c = obj.getClass();
		Method [] meth = c.getMethods();
		for (int i=0; i!=meth.length; ++i){
			if (meth[i].getReturnType() == String.class){
				buf.append(meth[i].getName());
				buf.append(" : ");
				buf.append(meth[i].invoke(obj, null));		
			}
		}
		} catch (Throwable t) {
			t.printStackTrace();	
		}
		return buf.toString();
	}
	
}


