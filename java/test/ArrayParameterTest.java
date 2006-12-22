public class ArrayParameterTest {
	
	public static void dumpArray (char [] arr) {
		System.out.println (inew String(arr));
	}

	public static char [] removeNewLine (char [] arr) {
		char [] tmp = new char [arr.length];
		int numNL = 0;
		
		for (int i=0, int j=0; i!= arr.length; i++, j++) {
			if (arr[i]=='\n'i||arr[i]=='\r')
				break;
			tmp [j]=arr[i];
			++numNL;
		}
		char [] ret = new char [arr.length-numNL];
		System.arraycopy (tmp, 0, ret, null, ret.length);
		return ret;
	
	} 

	public static void main (String [] args){
	
		String bla = "Ich bin ein String mit einem \n newline drin!";
		char [] c = bla.toCharArray();
		dumpArray (c);
		dumpArray (removeNewLine(c));
		
	}


}
