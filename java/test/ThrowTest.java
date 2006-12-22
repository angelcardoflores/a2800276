public class ThrowTest {

	public static void main (String [] args) {
		
		try {
			throw new Exception ("Oh no!");
		} catch (Exception e) {
			System.out.println ("caught!");
		} finally {
			System.out.println ("puh!, finally!");
		}
	}

}
