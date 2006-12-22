package classloader;

import java.net.URL;
import java.util.jar.*;
import java.util.LinkedList;

public class JarClassLoader {

	static URL url;
	static LinkedList jarFiles;
	
	static {
		url = JarClassLoader.class.getProtectionDomain().getCodeSource().getLocation();
		loadJarFiles();
	}

	private static void loadJarFiles () {
		JarInputStream jStream = null;
		try {
			System.out.println("here!");
			jStream = new JarInputStream(url.openStream());
			JarEntry entry = null;
			do {
				entry = jStream.getNextJarEntry();
				System.out.println(entry);
			} while (entry != null);
		} catch (Throwable t) {
			t.printStackTrace();	
		} finally {
			if (jStream != null) {
				try {jStream.close();} catch (Throwable t) {t.printStackTrace();}	
			}	
		}
	}

	protected Class findClass (String name) {
		return null;		
	}

	public static void main (String [] args) {
		
	}
}
