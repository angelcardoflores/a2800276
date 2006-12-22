package function.io;

import java.io.File;
import java.io.FilenameFilter;

import function.array.ArrayUtils;


/**
 * Some utilies to deal with files and directories.
 * @author tim
 *
 */
public class FileUtils {

	/**
	 * Returns an array containing `Files` in a specified 
	 * directory that have a specific extension. 
	 * @param dir the directory to search
	 * @param extensions an array containing the accepted extensions.
	*/
	public static File [] getFiles (File dir, final String [] extensions) {
		return dir.listFiles (new FilenameFilter () {
			public boolean accept (File dir, String filename) {
				int last = filename.lastIndexOf('.');
				if (last == -1)
					return false;
				String ext = filename.substring(++last);
				return ArrayUtils.inIgnoreCase(ext,extensions);
			}	
		});

	}
	
	/**
	 * Same as `getFiles` but takes a single extension instead of an Array.
	 * @see #getFiles(File, String[])
	 * @param dir
	 * @param extension
	 * @return
	 */
	public static File [] getFiles (File dir, String extension) {
		final String [] exts = {extension};
		return getFiles (dir, exts);
	}
	
	/**
	 * Same as `getFiles` but implicitly uses the current directory.
	 * @see #getFiles(File, String)
	 * @param extension
	 * @return
	 */
	public static File [] getFiles (String extension) {
		return getFiles(new File("."), extension);
	}
	
	/**
	 * Same as `getFiles` but using the current directory.
	 * @see #getFiles(File, String[])
	 * @param extensions
	 * @return
	 */
	public static File [] getFiles (String [] extensions) {
		return getFiles(new File("."), extensions);
	}

	public static void main (String [] args) {
		String [] ext = {
			"java"	
		};
		System.out.println(getFiles(new File("."), ext));	
	}
}
