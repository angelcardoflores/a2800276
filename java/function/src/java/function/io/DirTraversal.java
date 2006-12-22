
package function.io;

import java.io.File;
import java.io.FileFilter;

import function.Function;

/**
 * Utility to traverse filesystem directories.
 * @author tim
 *
 */
public class DirTraversal {

	/**
	 * Traverses the filesystem starting at the parameter `start`. Each directory 
	 * is applied to the function argument. The tree is traversed depth first.
	 * 
	 * @param start
	 * @param func
	 * @return an error message in case of error.
	 */
	public static String traverse (File start, Function func) {
		
		try {
			func.apply (start);
		} catch (Throwable t) {
			t.printStackTrace();
			return t.getMessage();	
		}

		File [] files = start.listFiles (
			new FileFilter () {
				public boolean accept(File f2) {
					return f2.isDirectory();
				}
			}
		);

		for (int i = 0; i!= files.length; ++i) {	
			traverse (files[i], func);	
		}
		return "";

	}
}
