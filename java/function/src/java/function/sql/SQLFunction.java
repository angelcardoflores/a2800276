
package function.sql;
import java.sql.*;

import function.Function;

public abstract class SQLFunction implements Function{

	public final void apply (Object obj) throws SQLException {
		if (obj instanceof ResultSet){
			apply ((ResultSet)obj);
		} else if (obj instanceof Connection) {
			apply ((Connection)obj);	
		} else if (obj instanceof DatabaseMetaData) {
			apply ((DatabaseMetaData)obj);	
		} else if (obj instanceof String[]) {
			apply ((String[])obj);	
		} else if (obj instanceof PreparedStatement) {
			apply ((PreparedStatement)obj);	
		} else {
			System.err.println("[WARNING] wrong SQLFunction usage. Call does nothing!");	
		}
			
	}
//	private static final Class STRING_ARRAY_CLASS = new String[0].getClass();
	
	public void apply (ResultSet obj) throws SQLException { System.out.println("ResultSet");}
	public void apply (Connection obj) throws SQLException { System.out.println("Connection");}
	public void apply (DatabaseMetaData obj) throws SQLException {System.out.println("DatabaseMetaData");}
	public void apply (String[] obj) throws SQLException {System.out.println("String[]");}
	public void apply (PreparedStatement obj) throws SQLException {System.out.println("PreparedStatement");}
	

;;}
