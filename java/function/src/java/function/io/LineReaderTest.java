package function.io;
import java.io.*;

/**
 * Example usage of the LineReader.
 * @author tim
 *
 */
public class LineReaderTest {
	
	//private int i;
	private String fileName;
	
	public LineReaderTest (String fileName) {
		this.fileName = fileName;	
	}

	public void test () {
		final StringBuffer buf = new StringBuffer();
		String retVal = LineReader.readFile (this.fileName, new IOFunction () {
			int i = 0;
			public void apply (Object obj) throws IOException {
				//i += Integer.parseInt((String)obj);
				buf.append(i++);
				buf.append(":");
				buf.append(obj);
			}	
		});
		if (retVal != null) {
			System.err.println(retVal);
			return;
		}
			
		//this.contents = buf.toString();
	}

	public static void main (String [] args) {
		LineReaderTest test = new LineReaderTest(args[0]);
		test.test();
		//System.out.println(test.contents);
		//System.out.println (test.i);
	}
}
