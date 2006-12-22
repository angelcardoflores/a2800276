

public class InnerClass {


	public static void main (String [] args) {
		Inner in = new Inner() {
			static void print () {
				System.out.println ("hello inner world");
			}
		};

		in.print();
	}
}

class Inner {
	static void print (){}

}
