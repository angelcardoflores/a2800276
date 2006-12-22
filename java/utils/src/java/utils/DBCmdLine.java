package utils;

/**
 * A specific extension to `CmdLine` that retireves the parameters
 * necessary for a JDBC Connection.
 * @see CmdLine
 * @author tim
 *
 */
public class DBCmdLine extends CmdLine {

	/**
	 * Contructor take the `args` Array passed to `main`
	 * @param args
	 */
	public DBCmdLine (String [] args) {
		super (args);	
	}

	/**
	 * Returns the DB parameter usage fragment.
	 * @return
	 */
	public static String usage () {
		return 	"\t -driver <driver>\n"+
			"\t -url <url>\n"+
			"\t -user <user>\n"+
			"\t -password <password>";
	
	}

	public String getDriver () {
		return get("-driver");	
	}
	public String getUrl (){
		return get("-url");	
	}
	public String getUser(){
		return get("-user");	
	}
	public String getPassword() {
		return get("-password");	
	}
	private static final String [] all = {
		"-driver", "-user", "-url", "-password"
	};
	public boolean complete () {
		for (int i=0; i!=all.length; ++i){
			if (!exists(all[i])) return false;	
		}
		return true;
	}
}
