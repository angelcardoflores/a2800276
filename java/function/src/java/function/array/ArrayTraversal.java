package function.array;

import function.SafeFunction;

/**
 * Utilities to iterate over Arrays.
 * @author tim
 *
 */
public class ArrayTraversal {
	
	/**
	 * Apply the provided function to each element of the Array.
	 * @param obj
	 * @param func
	 */
	public static void traverse (Object [] obj, SafeFunction func) {
		if (obj == null)
			return;
		traverse (obj, func, 0, obj.length);		
	}

	/**
	 * Apply the provided function to each element in the array. Restricted to the range 
	 * defined by the to and from parameter.
	 * @param obj
	 * @param func
	 * @param from
	 * @param to
	 */
	public static void traverse (Object [] obj, SafeFunction func, int from, int to) {
		if (obj == null)
			return;
		if (from < 0 || from > obj.length)
			return;
		if (to < from || to> obj.length)
			return;
		for (int i=from; i!=to; i++) {
			func.apply (obj[i]);	
		}
	}

	public static void main (String [] args) {
		String [] str =  {
		"eins", "zwei", "drei", "vier"	
		};
		ArrayTraversal.traverse (str, new SafeFunction (){
			public void apply (Object obj) {
				System.out.println (obj);	
			}	
		});
	}
}
