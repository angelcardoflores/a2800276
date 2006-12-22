

public class CloneVsReflectionTest implements Cloneable{
	private String eins;
	private int zwei;
	public short drei;
	public Integer vier;
	
	public static void main (String [] args) {
		long i = System.currentTimeMillis();
		Class c = CloneVsReflectionTest.class;
		for (int j = 0; j!=10; ++j) {

			for (int k=0; k!=100000; ++k) {
				try {
					CloneVsReflectionTest test = (CloneVsReflectionTest)c.newInstance();
				} catch (Throwable t) {
					t.printStackTrace();	
				}
			}

			System.out.println ("Reflection: "+(System.currentTimeMillis()-i));

			i = System.currentTimeMillis();
			CloneVsReflectionTest test2 = new CloneVsReflectionTest();
			for (int k=0; k!=100000; ++k) {
				try {
					CloneVsReflectionTest test = (CloneVsReflectionTest)test2.clone();
				} catch (Throwable t) {
					t.printStackTrace();	
				}
			}

			System.out.println ("clone: "+(System.currentTimeMillis()-i));
		}
	}
}
