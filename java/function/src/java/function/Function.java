
package function;

/**
 * The base interface of the functional library. The interface is meant to 
 * be as generic as possible, without using generics...
 * 
 * @author tim
 *
 */
public interface Function {
	
	/**
		Apply the Function to the provided object.
	*/
	public void apply (Object obj) throws Throwable;

}
