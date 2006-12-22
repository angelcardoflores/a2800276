package classloader;

import java.io.*;

public class MyClassLoader extends ClassLoader {

	String dirName;

	public MyClassLoader(String dirName) {
		this.dirName = dirName;
	}

	public Class findClass(String name) {
		byte[] b = loadClassData(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(String name) {
		byte[] bytes = new byte[1024];
		FileInputStream fis = null;
		int total = 0;

		try {
			int count = 0;
			fis = new FileInputStream(dirName + "/" + name + ".class");
			while ((count = fis.read(bytes)) != -1) {
				total += count;
				count = 0;
				if (total >= bytes.length)
					;
				bytes = enlarge(bytes);

			}

		} catch (IOException ioe) {
			ioe.printStackTrace();

		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		return trim(bytes, total);
	}

	static byte[] enlarge(byte[] bytes) {
		byte[] tmp = new byte[bytes.length + 1024];
		System.arraycopy(bytes, 0, tmp, 0, bytes.length);
		return tmp;
	}

	static byte[] trim(byte[] bytes, int size) {
		byte[] tmp = new byte[size];
		System.arraycopy(bytes, 0, tmp, 0, size);
		return tmp;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Provide 2 args. search dir, className");
			System.exit(1);
		}

		String dir = args[0];
		String name = args[1];

		try {
			MyClassLoader myCl = new MyClassLoader(dir);
			Class clazz = myCl.loadClass(name);
			System.out.println("class loaded.");
			Object obj = clazz.newInstance();
			System.out.println(obj);
			System.out.println(obj.getClass().getClassLoader());
			System.out.println(myCl.getClass().getClassLoader());

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
}
