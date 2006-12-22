public class ASCIITest {
	
static String str = "Gültigkeitsbereicheäö";
	public static void main (String [] args) {
		try {
			byte [] b = str.getBytes ("ASCII");
			byte [] c = str.getBytes ("ISO8859_1");
			for (int i=0; i!= b.length; i++) {
				System.out.println (str.charAt(i)+"-"+b[i]+" "+c[i]);	
			}
		} catch (Throwable t){
			t.printStackTrace();	
		}
	}
}
