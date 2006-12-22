
package function.io;
import java.io.*;

import function.Function;

/**
 * Extension of the basic Function interface to restrict 
 * exceptions to IOExceptions.
 * @author tim
 *
 */
public interface IOFunction extends Function {

	public void apply (Object obj) throws IOException;
}
