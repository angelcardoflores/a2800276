public class ExceptionTest {

	public int test () {
		return 0;
	}

}

class Child extends ExceptionTest {

	// doesn't work.
	public void test () throws Exception {
		return 0;
	}
}
