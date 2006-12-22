public class AssignTest {

	String eins, zwei, drei = "";
	Other other1, other2, other3 = new Other ("oEins");

	public static void main (String [] args) {
		AssignTest test = new AssignTest ();
		test.eins = "eins";
		test.zwei = "zwei";
		test.drei = "drei";

		test.other2=new Other ("oZwei");

		System.out.println (test.eins+test.zwei+test.drei);
		System.out.println (test.other1);
		System.out.println (test.other2);
		System.out.println (test.other3);
	}

}

class Other {
	String name;
	public Other (String name) {
		this.name = name;
	}
	public String toString() {return name;}
}
