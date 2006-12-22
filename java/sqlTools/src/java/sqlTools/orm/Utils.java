package sqlTools.orm;



public class Utils {
	
	public static String convertToDBName (String name) {
		char [] chars = name.substring(name.lastIndexOf('.')+1, name.length()).toCharArray();
		StringBuffer buf = new StringBuffer();
		for (int i=0; i!=chars.length; ++i) {
			if (i!=0 && Character.isUpperCase(chars[i])) {
				buf.append ('_');	
			}
			buf.append(Character.toUpperCase(chars[i]));
		}
		return buf.toString();
	}

	public static String convertToJavaName (String name) {
		char [] chars = name.toCharArray();
		StringBuffer buf = new StringBuffer();
		boolean upper = false;
		for (int i=0; i!=chars.length; ++i) {
			if (chars[i]=='_' && chars.length>i+1 && !Character.isDigit(chars[i+1])){
				upper=true;
				continue;
			}
			buf.append( upper ? Character.toUpperCase(chars[i]) : Character.toLowerCase(chars[i]));
			upper=false;
		}
		return buf.toString();
	}
	
	public static String convertToJavaClassName (String name) {
		StringBuffer  buf = new StringBuffer(convertToJavaName(name));
		buf.setCharAt(0,Character.toUpperCase(buf.charAt(0)));
		
		return buf.toString();
	}

	public static String getClassName (Class clazz) {
		String name = clazz.getName();
		return name.substring(name.lastIndexOf('.')+1, name.length());
		
	}

	public static void main (String [] args) {
		for (int i=0; i!=args.length; ++i) {
			System.out.println (convertToDBName(args[i]));	
		}	
	}
}
