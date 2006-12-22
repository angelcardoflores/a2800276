//package com.brokat.usr.util;

//import com.brokat.usr.*;
/**
	Implements Base64 encoding and decoding as defined in
	section 5.2 of RFC 1341.<p>
	Basically, three 8bit bytes are converted into 
	four 6bit ascii characters like so:
	<p>
	<pre>
		0 1 2 3 4 5 6 7 | 0 1 2 3 4 5 6 7 | 0 1 2 3 4 5 6 7
		
		0 1 2 3 4 5|0 1   2 3 4 5|0 1 2 3   4 5|0 1 2 3 4 5
	</pre>

	<p>
	Each six-bit value is used as an index into the following lookup
	table array:
	
	<table border = 1>
	<tr><th> Value <th> Encoding <th> Value <th> Encoding <th> Value <th> Encoding <th> Value <th> Encoding
	<tr><td> 0 <td> A <td>17 <td>R <td>34 <td>i <td>51 <td>z
	<tr><td> 1 <td> B <td> 18 <td> S <td> 35 <td> j <td> 52 <td> 0
	<tr><td> 2 <td> C <td> 19 <td> T <td> 36 <td> k <td> 53 <td> 1
	<tr><td> 3 <td> D <td> 20 <td> U <td> 37 <td> l <td> 54 <td> 2
	<tr><td> 4 <td> E <td> 21 <td> V <td> 38 <td> m <td> 55 <td> 3
	<tr><td> 5 <td> F <td> 22 <td> W <td> 39 <td> n <td> 56 <td> 4
	<tr><td> 6 <td> G <td> 23 <td> X <td> 40 <td> o <td> 57 <td> 5
	<tr><td> 7 <td> H <td> 24 <td> Y <td> 41 <td> p <td> 58 <td> 6
	<tr><td> 8 <td> I <td> 25 <td> Z <td> 42 <td> q <td> 59 <td> 7
	<tr><td> 9 <td> J <td> 26 <td> a <td> 43 <td> r <td> 60 <td> 8
	<tr><td> 10 <td> K <td> 27 <td> b <td> 44 <td> s <td> 61 <td> 9
	<tr><td> 11 <td> L <td> 28 <td> c <td> 45 <td> t <td> 62 <td> +
	<tr><td> 12 <td> M <td> 29 <td> d <td> 46 <td> u <td> 63 <td> /
	<tr><td> 13 <td> N <td> 30 <td> e <td> 47 <td> v
	<tr><td> 14 <td> O <td> 31 <td> f <td> 48 <td> w <td> (pad) <td> =
	<tr><td> 15 <td> P <td> 32 <td> g <td> 49 <td> x
	<tr><td> 16 <td> Q <td> 33 <td> h <td> 50 <td> y
	</table>

	<p>

	The padding character is used in case the length of the original
	byte array is not a multiple of three.
	<p>
	If you need to base64 en/decode large amounts of data, you should probably
	consider wrapping some kind of stream around this class and converting
	the data chunk for chunk.
	
*/
public class Base64 {


	/**
		lookup table for  base64 _en_coding (bytes -> ascii)
	*/
	private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

	/**
		lookup table for base64 _de_coding (ascii -> bytes), 
		BYTES[ascii+FIRST_ASCII_VALUE] contains the byte representing the 6-bit
		pattern associated with that character. The FIRST_ASCII_VALUE constant
		is necessary so that we store only those ascii values that are actually
		needed for base64.<p>
		See the commented out static block below to see how this array is 
		constructed.
	*/
	private static final byte [] BYTES = {62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1,
2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, };

	/**
		the value of the first ascii value that can be used in base64
		(0x2B, '+')
	*/
	private static final int FIRST_ASCII_VALUE = 43;
	/**
		lookup table for the integer shifts during encoding.
		maps 0-4 to 18,12,6,0 respectavely. Of course there's
		a more elegant mathamatical way to do it, but I couldn't 
		be asked to figure it out.
	*/
	private static final int[] shiftLookup = {18, 12, 6, 0};
	
	/*
	static {
		// + => ascii 43
		// z => ascii 122
		BYTES = new byte [123-FIRST_ASCII_VALUE];
		for (int i=0; i!=BYTES.length; i++) { // init
			BYTES [i]=-1;		
		}
		char start = 'A'-FIRST_ASCII_VALUE;
		char end = 'Z'-FIRST_ASCII_VALUE+1;
		byte b = 0;

		for (char i=start; i!= end; ++i) {
			BYTES[i]=b++;
		}
		start = 'a'-FIRST_ASCII_VALUE;
		end = 'z'-FIRST_ASCII_VALUE+1;
		for (int i=start; i!=end; ++i) {
			BYTES[i]=b++;
		}
		
		start = '0'-FIRST_ASCII_VALUE;
		end = '9'-FIRST_ASCII_VALUE+1;
		
		for (int i=start; i!=end; ++i) {
			BYTES[i]=b++;
		}

		BYTES['+'-FIRST_ASCII_VALUE]=b++;
		BYTES['/'-FIRST_ASCII_VALUE]=b++;
		//BYTES['='-FIRST_ASCII_VALUE]=0;
		System.out.print ("private static final byte [] BYTES = {");
		for (int i=0; i!=BYTES.length; i++) {
			System.out.print (BYTES[i]);
			System.out.print (", ");
		}
		System.out.println ("};");
	}*/

	/**
		create a base64 encoded String of the byte array provided
		as argument to the method, startnig at <code>off</code>
		for <code>len</code> bytes.
	*/
	public static String encode (byte [] bytes, int off, int len) {
		byte [] mine = new byte [len];
		System.arraycopy (bytes, off, mine, 0, len);
		return encode (mine);
	}

	/**
		create a base64 encoded String from the provided byte array.
	*/
	public static String encode(byte[] bytes) {
		StringBuffer buf = new StringBuffer((bytes.length * 4) / 3);

		for (int i = 0; i < bytes.length; i += 3) {
			buf.append(encode3(bytes, i));
		}
		return buf.toString();	
	}

	/**
		takes the 3 bytes from the provided array starting at
		<code>start</code> and converts them to an array
		of four base64 encoded char's. If less than 3 bytes are 
		available after start, the last one or two char's
		will be the base64 pad character '='.
	*/
	private static char[] encode3(byte[] bytes, int start) {
		int end = start + 2;
		int eqs = 0; // number of equal signs to append as padding to the end
		if (end > bytes.length - 1) {
			eqs = end - (bytes.length - 1);
			end = bytes.length - 1;
		}

		char[] ret = new char[4];
		int val =0;
		for (int i = start; i <= end; i++) {
			val <<= 8;
			int signedValuesSuck = bytes[i]<0?bytes[i]+256:bytes[i]; // correct neg. Values to positive int values.
			val += signedValuesSuck;
		}
		val <<= (8*eqs); // need to shift in in case there were only one or two bytes left to process
		for (int i = 0; i != 4; i++) {
			ret[i] = CHARS[(val >> shiftLookup[i]) & 0x3f];
		}
		if (eqs > 0) ret[3] = '=';
		if (eqs == 2) ret[2] = '=';
		return ret;
	}

	/**
		decodes the provided String from base64 into a
		byte array
		@throws Exception if the length of the String is
		not a multiple of 4 or if a non base64 character is
		provided in the input.
	*/
	public static byte [] decode (String str) throws Exception {
		return decode (str.toCharArray());
	}

	/**
		decodes the provided char Array from base64 into a
		byte array
		@throws Exception if the length of the String is
		not a multiple of 4 or if a non base64 character is
		provided in the input.
	*/
	public static byte[] decode(char[] chars) throws Exception {
		chars = removeNewLine (chars);	
		//System.err.println (new String(chars));
		if ((chars.length%4)!=0)
			throw new Exception ("[Base64.decode] decode length is not a multiple of 4!");
		int len = (chars.length/4)*3;
		if (chars[chars.length-1] == '=') {
			--len;
		}
		if (chars[chars.length-2] == '=') {
			--len;
		}
		byte [] bytes = new byte [len];
		//System.err.println(len);
		int tmp=0;
		int byteCount = 0;
		int index = 0;
		for (int i = 0; i<chars.length-1; ) {
			for (int j = 0; j!=4; j++) {
				tmp <<=6;
				index = chars[i]-FIRST_ASCII_VALUE; // calculate indes into lookup table
				if (index > BYTES.length-1) throw new Exception
					("[Base64.decode] Illegal character: '"+chars[i]+"' in String pos: "+i);
				if (BYTES[index]==-1) { // check that we have a valid character
					if ((i==chars.length-1||i==chars.length-2)&&(chars[i]=='=')){
						continue;
					}//ok
					else throw new Exception 
						("[Base64.decode] Illegal character: '"+chars[i]+"' in String pos: "+i);
				}
				tmp += BYTES [index];
				++i;
			}
			if (chars[i]=='=' && tmp == 0) break;
			// three byte are now in tmp. need to shift them out.
				for (int k = 16; k>=0&&byteCount<bytes.length; k-=8) {
//for (int k = 16; k>=0; k-=8) {
				System.out.println ("\nbcb: "+byteCount);
				System.out.println (tmp);
				bytes [byteCount++] = (byte)((tmp >> k) & 0xFF);
				if (byteCount == bytes.length) break;
				System.out.println ("bc:"+byteCount);	
				System.out.println ("len:"+bytes.length);	
			}
			tmp =0;	
		}		
		return bytes;
	}
	
	/**
		removes newline and carriagereturn characters from input.
	*/
	private static char [] removeNewLine (char [] arr) {
		//System.err.println (arr.length);
		char [] tmp = new char [arr.length];
		int numNL = 0;
		
		for (int i=0, j=0; i!= arr.length; i++) {
			if (arr[i]=='\n'||arr[i]=='\r') {
				++numNL;
				continue;
			}
			tmp [j++]=arr[i];
		}
		char [] ret = new char [arr.length-numNL];
		System.arraycopy (tmp, 0, ret, 0, ret.length);
		//System.err.println (ret.length);
		return ret;
	
	}
	/**
		Stupid trick to display byte values of int's to help in debugging.
	*/
	private static String bits (int val) {
		StringBuffer buf = new StringBuffer (32);
		for (int i=31; i!=-1; i--) {
			if ((i+1)%8==0) buf.append (" ");
			char c = ((val >>> i) & 1)==1?'1':'0';
			buf.append (c);
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		byte [] bytes = {1,2,3,4,5,6,7,8,9,10};
		try {
			System.out.println (Base64.encode(bytes));
		Base64.decode(Base64.encode(bytes));
		}catch (Throwable t) {
			t.printStackTrace();	
		}
		//byte [] b = {0xCA, 0xFE, 0xBA, 0xBE};
		//byte [] b = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
		//System.out.println (encode());
		
	/*	try {	
				
		byte [] tmp = new byte [600];
		int len = 0;
		int total = 0;
		
		long start = System.currentTimeMillis();
		while ((len = System.in.read(tmp, 0,600))!=-1) {
			//long start = System.currentTimeMillis();
			//String enc = encode (tmp,0,len);
			//System.out.println (System.currentTimeMillis()-start);
			//System.out.print (enc);
			//total += len;
		}
			//System.out.println (System.currentTimeMillis()-start);
		//System.out.println (total);
		}catch (Throwable t) {
			t.printStackTrace();
		}
	*/	
/*		
		String test = 
			"yv66vgADAC0AHQcAAgEABEJhc2UHAAQBABBqYXZhL2xhbmcvT2JqZWN0AQALZG9Tb21ldGhp\n" +
			"bmcBAAMoKVYBAA5kb0Fub3RoZXJUaGluZwEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUMAAsA" +
			"DAEAA291dAEAFUxqYXZhL2lvL1ByaW50U3RyZWFtOwkADgAKBwAPAQAQamF2YS9sYW5nL1N5" +
			"c3RlbQgAEQEACkRvbmUsIGJhc2UMABMAFAEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJp" +
			"bmc7KVYKABYAEgcAFwEAE2phdmEvaW8vUHJpbnRTdHJlYW0BAAY8aW5pdD4MABgABgoAAwAZ" +
			"AQAKU291cmNlRmlsZQEAEUFic3RyYWN0VGVzdC5qYXZhBCAAAQADAAAAAAADBAAABQAGAAAA" +
			"CAAHAAYAAQAIAAAAJQACAAAAAAAJsgANEhC2ABWxAAAAAQAJAAAACgACAAAADwAIABAAAAAY" +
			"AAYAAQAIAAAAIQABAAEAAAAFKrcAGrEAAAABAAkAAAAKAAIAAAALAAQACwABABsAAAACABw=";
		//System.err.print (decode(test).length);	
		try {
		byte [] bytes1 = decode(test);
		System.out.write (bytes1, 0, bytes1.length);
		} catch (Throwable t) {
			System.err.println ("GASP!");
			t.printStackTrace();
		}
		//for (int i=-10; i!=10; i++) {
		//	System.out.println (i+" : "+bits(i));
		//}

		//for (int i=0; i!=BYTES.length; i++) {
	//		System.err.print (i+" : "+BYTES[i]+" : ");
	//		System.err.println ("\t"+(char)(i+FIRST_ASCII_VALUE));
	//	}
*/		
	}
	//A B C D E F G H I J K L M N O P Q R S T U V W X Y Z a b c d e f g h i j h l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9 + /
}
