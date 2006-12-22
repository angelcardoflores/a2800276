
package function;

/**
 * Extension of `Function` that doesn't declare exceptions.
 * @see Function
 * @author tim
 *
 */
public interface SafeFunction extends Function{

	public void apply (Object obj);

}
