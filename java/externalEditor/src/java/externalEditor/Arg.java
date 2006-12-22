package externalEditor;

public class Arg {
	
	private String rep;
	private int num;
	private String str;
	private boolean isNum;
	public static final String TXT = "TXT";
	public static final String NUM = "NUM";
	public static final String BOOL = "BOOL";
	public static final String OPTNUM = "OPTNUM";
	
	public void setBoolean (boolean val) {
		rep = val?"T":"F";
		isNum = false;
	}
	
	public boolean getBoolean () {
		if (!isBooleanArg()){
			throw new RuntimeException("Argument is no boolean! "+rep);	
		}	
		return "T".equals(rep);
	}
	
	public boolean isBooleanArg () {
		return ("T".equals(rep) || "F".equals(rep));	
	}
	
	public void setNumber (String in) {
		int i=0;
		try {
			i=Integer.parseInt(in);
		} catch (NumberFormatException nfe) {
			throw nfe;
		}
		this.num = i;
		this.isNum = true;
		this.rep = in;
	}
	
	public void setNumber (int i) {
		rep = Integer.toString(i);
		num = i;
		isNum = true;
	}

	public int getNumber () {
		return num;	
	}

	public boolean isNumberArg () {
		return isNum;
	}

	public void setUnescapedString (String str) {
		this.rep = escape(str);	
		this.str = str;
		this.isNum = false;
	}

	public void setEscapedString (String str) {
		this.rep = str;
		this.str = unescape(str);
		this.isNum = false;
	}

	public String getString () {
		if (!isStringArg())
			throw new RuntimeException ("Argument is not a String!");
		return str;	
	}

	public boolean isStringArg () {
		if (rep==null || !rep.startsWith("\""))
			return false;
		return true;
		
	}

	public String getArgType () {
		if (isStringArg()){
			return TXT;
		} 
		if (isNumberArg()) {
			return NUM;	
		}
		if (isBooleanArg()) {
			return BOOL;
		}
		return "???";
	}

	public String toString () {
		return "type:"+getArgType()+" "+rep;	
	}

	public static Arg parseArg (String rep) {
		if (rep == null || rep.length() == 0)
			return null;
			
		Arg a = new Arg();
		char [] cs = rep.toCharArray();
		char c = cs[0];
		if (c=='T'||c=='F'){
			a.setBoolean(c=='T'?true:false);
			return a;
		}
		else if (c=='"'){
			a.setEscapedString(rep);
			return a;
		}
		else if (c=='+' || c=='-' || Character.isDigit(c)){
			a.setNumber(rep);
			return a;
			
		}
		return null;
		
	}

	public static String escape (String str) {
		StringBuffer buf = new StringBuffer();
		buf.append ('"');
		char [] charArr = str.toCharArray();
		for (int i=0; i!=charArr.length; ++i){
			char c = charArr[i];
			if (c=='\n') {
				buf.append ("\\n");
				continue;
			} else if (c=='\r'){
				buf.append ("\\r");	
				continue;
			} else if (c=='"' || c=='\\'){
				buf.append('\\');	
			}
			buf.append(c);
		}
		buf.append ('"');
		return buf.toString();
	}

	public static String unescape (String str) {
		StringBuffer buf = new StringBuffer();
		char [] charArr = str.toCharArray();
		if (charArr[0]!='"' || charArr[charArr.length-1]!='"'){
			throw new RuntimeException ("Arg not properly escaped: "+str);	
		}
		for (int i=1; i!=charArr.length-1; ++i){
			char c = charArr[i];
			if (c=='\\'){
				++i;
				c = charArr[i];
				if (c=='n') buf.append('\n');
				else if (c=='r') buf.append('\r');
				else buf.append(c);
				continue;
			}
			buf.append(c);
		}
		return buf.toString();

	}

	

	
	
}
