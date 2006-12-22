

public class EBCDICTest {
	private static final char [] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	public static String encodeHex (byte b) {
		String hex = "";
		hex += hexChars[(b>>>4)&0x0f];
		hex += hexChars[(b&0x0f)];
		return hex;
	}

	public static void main (String [] args){
		try {
				
			String str = "abcXYZ1234   ";
			byte [] b= str.getBytes("Cp500");
			for ( int i=0;i!=b.length; i++){
				System.out.println (encodeHex(b[i]));
			}
		}
		catch (Throwable e) {
			e.printStackTrace();	
		}
	}
}
