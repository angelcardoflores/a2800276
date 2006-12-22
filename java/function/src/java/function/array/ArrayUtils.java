package function.array;

import java.util.LinkedList;
import java.util.List;
import function.*;

public class ArrayUtils {

	/**
	 * Checks if value is contained in 'array'
	 * @return false if value == null
	 */
	public static boolean in(String value, String[] array) {
		if (value == null)
			return false;
		for (int i = 0; i != array.length; ++i) {
			if ( value.equals(array[i]) )
				return true;
		}
		return false; 
	}
	
	/**
	 * Check whether the passed value is contained in the Array,
	 * comparison is done using `equalsIgnoreCase`
	 * @param value
	 * @param array
	 * @return
	 */
	public static boolean inIgnoreCase(String value, String[] array) {
		if (value == null)
			return false;
		for (int i = 0; i != array.length; ++i) {
			if ( value.equalsIgnoreCase(array[i]) )
				return true;
		}
		return false;

	}
	
	/**
	 * Convert an Array into a List.
	 * @param arr
	 * @return
	 */
	public static List toList(Object[] arr) {
		final LinkedList list = new LinkedList();
		ArrayTraversal.traverse(arr, new SafeFunction() {
			public void apply(Object obj) {
				list.add(obj);
			}
		});
		return list;
	}

	/**
	 * Append the contents of an Array to the end of an existing list.
	 * @param arr
	 * @param list
	 */
	public static void addToList(Object[] arr, final List list) {
		ArrayTraversal.traverse(arr, new SafeFunction() {
			public void apply(Object obj) {
				list.add(obj);
			}
		});
	}
}
