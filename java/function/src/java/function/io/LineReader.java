package function.io;
import java.io.*;

/**
 * Example usage of the Functional framework. This class provides easy file reading
 * capabilites using Function.
 * @author tim
 *
 */
public class LineReader {
	
	/**
	 * Simple function to read a text file. Each line of the file specified by the 
	 * filename parameter is applied to the function argument.
	 * @param fileName the name of the file to read
	 * @param func this function takes a String and gets called for every line in the file
	 * @return text of an exception in case of difficulites, else null.
	 */
	public static String readFile (String fileName, IOFunction func) {
		String retVal = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader (new FileReader (fileName));
			String currLine = null;
			while ((currLine=reader.readLine())!=null)
				func.apply(currLine);
			
		} catch (IOException ioe) {
			retVal = ioe.getMessage();
			//ioe.printStackTrace();
			
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (Throwable t){}
		}
		return retVal;
	}

	/**
	 * Same as `readFile` but takes a Stream.
	 * @see #readFile(String, IOFunction)
	 * @param ios
	 * @param func
	 * @return
	 */
	public static String readStream (InputStream ios, IOFunction func) {
		String retVal = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader (new InputStreamReader (ios));
			String currLine = null;
			while ((currLine=reader.readLine())!=null)
				func.apply(currLine);
			
		} catch (IOException ioe) {
			retVal = ioe.getMessage();
			//ioe.printStackTrace();
			
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (Throwable t){}
		}
		return retVal;

	}
	
	private static void usage () {
		System.err.println ("usage: [jre] LineReader <fileName>");
		System.exit(1);
	}
	
	public static void main (String [] args) {
		if (args.length != 1)
			usage();
		String retVal = readFile (args[0], new IOFunction () {
			
			public void apply (Object obj) throws IOException{
				System.out.println (obj);		
			}	
		});
		if (retVal != null)
			System.err.println("An Error occured: "+retVal);
	}
}
