package utils;

/**
 * Extension to `java.util.StringTokenizer that has a `toArray` function.
 * @author tim
 *
 */
public class StringTokenizer extends java.util.StringTokenizer {
	public StringTokenizer(String str) {
		super (str);	
	}	
	public StringTokenizer(String str, String delim) {
		super (str, delim);	
	}
	public StringTokenizer(String str, String delim, boolean returnDelims){
		super (str, delim, returnDelims);	
	}

	public String[] toArray () {
		String [] ret = new String [countTokens()];
		for (int i=0; hasMoreTokens(); ++i){
			ret[i]=nextToken();	
		}
		return ret;
	}
}
