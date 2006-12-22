package html.parse;

import html.*;

public class Utils {

	public static String tagPath (Tag t) {
		StringBuffer buf = new StringBuffer();
		buf.append (t.getName());
		if (t.getParent()!=null && t.getParent()!=t) {
			buf.insert (0, ":");
			buf.insert (0, Utils.tagPath(t.getParent()));
		}
		return buf.toString();
	}
}
