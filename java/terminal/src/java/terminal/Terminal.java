package terminal;

public class Terminal implements ASCII {
	
	
	
	final static byte [] CURSOR_MOVE = {ESC,LBRACK,ONE,ZERO,ZERO,SEMICOLON,FIVE,ZERO,ZERO,H};
	final static byte [] CURSOR_POS = {ESC,LBRACK,SIX,n};
	final static byte [] NLarr = {LF};


	public static void send (byte [] control) {
		for (int i = 0; i!= control.length; ++i) {
			System.out.write (control[i]);	
		}	
		System.out.flush();
	}
	
	public static void main (String [] args) {
		send (CURSOR_MOVE);
		send (CURSOR_POS);

		try {
			int i = 0;
			System.out.println("");
			System.out.println(System.in.getClass());	
			send (CURSOR_MOVE);
			while ((i=System.in.read())!=-1)
				System.out.println (i);
//			Thread.sleep(5*1000);
		} catch (Throwable t) {
			t.printStackTrace();	
		}
	}
	
}
