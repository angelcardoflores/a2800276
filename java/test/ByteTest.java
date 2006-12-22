

public class ByteTest {
	
	static byte stx = 002;
	static byte etx = 003;
	static byte fs	= 034;
	static byte ff = -128;

	public static void main (String [] args) {
		System.out.println ((char)stx);
		System.out.println (etx);
		System.out.println (fs);
		System.out.println (ff);
		int i = ff + 256;
		char c = (char)i;
		System.out.println(ByteUtils.showBits (ff));
		//System.out.println (Character.getNumericValue(c));
		
	
 byte b = (byte)0xFF; int notSigned = b < 0 ? b+256 : b;
 System.out.println(notSigned);
		
	}

	
}
