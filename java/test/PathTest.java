
import java.io.*;
public class PathTest {

	public static void main (String [] args) {
		File file = new File (args[0]);
		try {
		System.out.println ("getPath"+file.getPath());
		System.out.println ("getAbsolutePath"+file.getAbsolutePath());
		System.out.println ("getCanonicalPath"+file.getCanonicalPath());
		System.out.println ("getParent"+file.getParent());
		} catch (Exception e) {e.printStackTrace();}
	}

}
