public class IntCharTest {


		
	

	public static void main (String [] args) {
		for (char i='0'; i<='9'; i++) {
			System.out.print (i);
			System.out.print (":");
			System.out.print ((int)i);
			System.out.print (":");
			System.out.println (Integer.toString((int)i,16));
		}

		System.out.println (\u0030<'0');
	}

}
