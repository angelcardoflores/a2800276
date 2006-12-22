package reflectionPanel;
import javax.swing.*;


public class Inputs {
	static void error (String t, String s) {
		System.err.println (t+":"+s);	
	}	
}

class BooleanField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4618186072640868173L;
	
	
}

class ByteField extends JTextField{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7096780749322623447L;
	int radix = 10;
	
	public void setRadix (int radix) {
		this.radix=radix;	
	}
	
	public void setText (String t) {
		try {
			Byte.parseByte (t);	
		} catch (NumberFormatException nfe) {
			Inputs.error(t, "byte");	
		}
		super.setText(t);
	}

}

class CharField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2972907621420260826L;}

class ShortField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7262977269280926899L;}

class IntField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2777965702511357207L;}

class LongField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8415593745683023826L;}

class DoubleField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2258435917498286662L;}

class FloatField extends JTextField{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7507500434888377358L;}
