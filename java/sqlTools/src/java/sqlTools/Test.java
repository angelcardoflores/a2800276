package sqlTools;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DBCmdLine;
import function.sql.SQLExecuter;
import function.sql.SQLFunction;

public class Test {
	
	public static void main (String [] args) {
		DBCmdLine cmd = new DBCmdLine(args);
		SQLExecuter exe = new SQLExecuter(cmd.getDriver(), cmd.getUrl(), cmd.getUser(), cmd.getPassword());
		exe.metaData (new SQLFunction () {
			public void apply (DatabaseMetaData meta) throws SQLException {
				System.out.println ("*************getExportedKeys***************");
				System.out.println("XXX_TIM_1");
				printResultSet(meta.getExportedKeys(null, null, "XXX_TIM_1"));
				System.out.println("XXX_TIM_2");
				printResultSet(meta.getExportedKeys(null, null, "XXX_TIM_2"));
				System.out.println ("*************getImportedKeys***************");
				System.out.println("XXX_TIM_1");
				printResultSet(meta.getImportedKeys(null, null, "XXX_TIM_1"));
				System.out.println("XXX_TIM_2");
				printResultSet(meta.getImportedKeys(null, null, "XXX_TIM_2"));
			}
		});
	}

	static final void printResultSet(ResultSet rset) throws SQLException {	
		int i = rset.getMetaData().getColumnCount();
		while (rset.next()) {
			for (int j=0; j!=i; ++j) {
				System.out.print(rset.getString(j+1));
				System.out.print(" - ");
			}
			System.out.println();
		}	
	}
}
