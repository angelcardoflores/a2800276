package utils;

public class StringUtils {

	public static String compressWhiteSpace (String str) {
		String tmp = str.trim();
		StringBuffer buf = new StringBuffer();
		boolean lastCharWS = false;
		for (int i=0; i!=tmp.length();++i){
			char c = tmp.charAt(i);
			if (!Character.isWhitespace(c)){
				if (lastCharWS)
					continue;
				lastCharWS=true;
				buf.append(" ");
				continue;
			}
			lastCharWS=false;
			buf.append (c);
		}
		return buf.toString();
		
	}
}
