import java.lang.reflect.*;


public class AnonClass {

	private String str;
	public AnonClass (String str) {
		this.str = str;
	}

	public static AnonClass getMeOne (String str) {

		return new AnonClass (str) {
			public boolean isNil(){
				return true;
			}
		};
	}
	public static void main (String [] args) {
		AnonClass klass = getMeOne ("Tim");
		Class cl = klass.getClass();
		Method []meth = cl.getDeclaredMethods();
		for (int i=0; i!=meth.length; i++){
			System.out.println (meth[i]);
		}

		meth = cl.getMethods ();

		for (int i=0; i!=meth.length; i++){
			System.out.println (meth[i]);
		}
	}
	

}
