package tools;


public class Tools {
	
	/**
		returns how the class would be represented in a
		method signature or return declaration. E.g. [[[[[[Ljava.langObject
		would return java.lang Object [][][][][][]
	*/
	public static String getDeclaration (Class c) {
		StringBuffer buf = new StringBuffer ();
		// brute force
		String name = c.getName();
		int dim = name.lastIndexOf("[");
		++dim; // dimensions of array

		name = name.substring(dim);
/*
 B            byte
 C            char
 D            double
 F            float
 I            int
 J            long
 Lclassname;  class or interface
 S            short
 Z            boolean
*/
		if (name.equals("B")) buf.append ("byte");
		else if (name.equals("C")) buf.append ("char");
		else if (name.equals("D")) buf.append ("double");
		else if (name.equals("F")) buf.append ("float");
		else if (name.equals("I")) buf.append ("int");
		else if (name.equals("J")) buf.append ("long");
		else if (name.equals("S")) buf.append ("short");
		else if (name.equals("Z")) buf.append ("boolean");
		else {
						
			name = (dim==0) ? name:name.substring(1,name.length()-1);
			buf.append(name);
		}

		for (int i=0; i!=dim; i++) {
			buf.append (" []");	
		}

		return buf.toString ();
		
	}

	public static void main (String [] args) {
		for (int i=0; i!=args.length; i++) {
			try {
				System.out.println (getDeclaration(Class.forName(args[i])));		
				System.out.println (getDeclaration(new String [1][2][3].getClass()));
				System.out.println (getDeclaration(new short [1].getClass()));
			} catch (Throwable t) {
			t.printStackTrace();
					
			}
		}	
	}

}
