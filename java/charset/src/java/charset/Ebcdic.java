package charset;

import html.*;

/**
	Just a little tool that prints out an EBCDIC character table in html.
*/
public class Ebcdic {
	private static final char [] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final String [] hexStr = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

	public static String encodeHex (byte b) {
		String hex = "";
		hex += hexChars[(b>>>4)&0x0f];
		hex += hexChars[(b&0x0f)];
		return hex;
	}

	public static String getHtmlEntity (String s) {
		if (s==null || s.length() == 0)
			return "";

		return getHtmlEntity(s.charAt(0));	
	}
	public static String getHtmlEntity (char c) {
	System.err.print(c);
	System.err.print (" -> ");
	System.err.println ((int)c);
		return "&#"+Integer.toString((int)c)+";";
	}

	public static void main (String [] args) {
		try {


			String [] heading = new String [hexChars.length+1];
			for (int i=-1; i!=hexChars.length; ++i) {
				heading[i+1] = i==-1?" ":hexStr[i];	
			}
			Table table = new Table(heading);
			table.setBorder(1);

			for (int rows = 0; rows!=0x10; ++rows) {
				for (int cols = -1; cols!=0x10; ++cols){
					if (cols == -1)
						heading[cols+1] = "<strong>"+hexStr[rows]+"</strong>";
					else {
						byte [] b = new byte [1];
						b[0] = (byte)(((byte)cols)<<4 | (byte)rows);
						
						heading[cols+1] = getHtmlEntity(new String (b, "CP037"));
					}
				}
				table.add(heading);
			}


			System.out.println(table);
		} catch (Throwable t) {
				
		}
	}
}
