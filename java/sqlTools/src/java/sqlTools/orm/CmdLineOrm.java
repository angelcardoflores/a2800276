package sqlTools.orm;

import utils.DBCmdLine;
import function.sql.SQLExecuter;

public class CmdLineOrm extends FullServiceORM {
	
	private static SQLExecuter exe;
	public CmdLineOrm () {
		if (exe==null) {
			throw new RuntimeException("[CmdLineOrm] not initialised, call setCmdLine() with the commandline arguments first!");	
		};
	}

	public SQLExecuter getSQLExecuter() {
		return exe;	
	}

	public static void setCmdLine(DBCmdLine cmd) {
		if (!cmd.complete()) {
			throw new RuntimeException("[CmdLineOrm] incomplete login"+DBCmdLine.usage());	
		};
		exe = new SQLExecuter(cmd.getDriver(), cmd.getUrl(), cmd.getUser(), cmd.getPassword());	
	}

}
