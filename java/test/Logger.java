package tim;

public class Logger {
	
	private String name;

	private static final int FATAL= 1;
	private static final int ERROR = FAIL << 1;
	private static final int WARN = ERROR << 1;
	private static final int DEBUG = WARN << 1;
	private static final int CHITCHAT = WARN << 1;
	
	//                                |levels |
	//0000 0000 0000 0000   0000 0000 0000 0000
	
	private Logger (String name) {
		this.name = name;
	}

	public static Logger getLogger (Object obj) {
		String key = obj.getClass().getName();
		key = key.substring (key.lastIndexOf('.')+1,key.length());
		return null;
	}
	
	public void printFatal (String mes) {}

	public void printFatal (String mes, Throwable t) {}

	public void printFatal (String mes) {}

	public void printError (String mes) {}

	public void printWarn (String mes) {}

	public void printDebug (String mes) {}

	public void printChitChat (String mes) {}
	
	public static void main (String [] args){
		
		getLogger(System.out);
	}
}
