
public class ProfileTest{
	

	static void test () {
		int j = 0;
		for (int i=0; i!=100000; i++) {
			j+=i;
			j*=i;
		}	
	}

	public static void main (String [] args) {
		for (int i=0; i!=1000; i++)
			test ();	
	}
}
