
package function.sql;

import java.sql.*;
import javax.sql.*;

import function.Function;
import function.SafeFunction;
import utils.*;

/**
 * Class to simplify Database Access using the `Function` framework.
 * @author tim
 */
		
public class SQLExecuter  {
	
	String driver;
	String url;
	String user;
	String password;

	DataSource dataSource;
	
	/**
	 * Create an SQLExecuter from the passed driver configuration parameters.
	 * @param driver
	 * @param url
	 * @param user
	 * @param password
	 */
	public SQLExecuter (String driver, String url, String user, String password) {
		this.driver=driver;
		this.url=url;
		this.user=user;
		this.password=password;
	}
	
	/**
	 * Create an SQLExecuter from a DataSource
	 * @param dataSource
	 */
	public SQLExecuter (DataSource dataSource) {
		this.dataSource = dataSource;	
	}

	/**
	 * Create an SqlExecuter form a DBCommandline.
	 * @param cmd
	 */
	public SQLExecuter (DBCmdLine cmd) {
		this(cmd.getDriver(), cmd.getUrl(), cmd.getUser(), cmd.getPassword());	
	}

	
	/**
	 * Execute the SQL statement passed to the function. The function passed 
	 * in is applied to the `ResultSet` that's returned by the Database.
	 * @param sql the statement to execute
	 * @param func a function taking a ResultSet as a parameter.
	 */
	public String execute (String sql, SQLFunction func) {
		String ret = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection ();
			stmt = conn.createStatement();
			rset = stmt.executeQuery (sql);
			func.apply (rset);			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			ret = sqle.getMessage();	
		} finally {
			if (rset!=null) try {rset.close();}catch (Throwable t) {}
			if (stmt!=null) try {stmt.close();}catch (Throwable t) {}
			returnConnection (conn);
		}
		return ret;
	}
	
	/**
	 * Executes the SQL statement and returns the first `int` parameter in the
	 * ResultSet.
	 * @see #getInt(String, int)
	 * @param sql The sql statement to execute
	 * @return -1 if the statement doesn't return an int, no rows are returned or
	 * an error occured.
	 */
	public int getInt (String sql) {
		return getInt(sql, -1);	
	}
	
	/**
	 * Executes the SQL statement and returns the first `int` parameter in the
	 * ResultSet.
	 * @param sql The sql statement to execute
	 * @param notFound the `int` value to return in case of error ...
	 * @return
	 */
	public int getInt (String sql, int notFound) {
		int ret=0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection ();
			stmt = conn.createStatement();
			rset = stmt.executeQuery (sql);
			if (!rset.next()){
				ret = notFound;	
			} else {
				ret = rset.getInt(1);	
			}
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			ret = notFound;	
		} finally {
			if (rset!=null) try {rset.close();}catch (Throwable t) {}
			if (stmt!=null) try {stmt.close();}catch (Throwable t) {}
			returnConnection (conn);
		}
		return ret;
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public String getString (String sql) {
		String ret=null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = getConnection ();
			stmt = conn.createStatement();
			rset = stmt.executeQuery (sql);
			if (!rset.next()){
				ret = "[SQLExecuter.getString] Found no result to your query: "+sql;	
			} else {
				ret = rset.getString(1);	
			}
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			ret = sqle.getMessage();	
		} finally {
			if (rset!=null) try {rset.close();}catch (Throwable t) {}
			if (stmt!=null) try {stmt.close();}catch (Throwable t) {}
			returnConnection (conn);
		}
		return ret;
	}

	
	public String executeUpdate (String sql) {
		String ret = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection ();
			stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			ret = sqle.getMessage();	
		} finally {
			if (stmt!=null) try {stmt.close();}catch (Throwable t) {}
			returnConnection (conn);
		}
		return ret;
		
	}

	public String executePreparedStatement (String sql, SQLFunction func) {
		String ret = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection ();
			pstmt = conn.prepareStatement(sql);
			func.apply(pstmt);	
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			ret = sqle.getMessage();	
		} finally {
			if (pstmt!=null) try {pstmt.close();}catch (Throwable t) {}
			returnConnection (conn);
		}
		return ret;
		
	}
	
	
	/**
		Function object gets passed the java.sql.Connection, e.g. to
		manipulate DatabaseMetaData.
	*/
	public String connection (SQLFunction func) {
		String ret = null;
		Connection conn = null;
		try {
			conn = getConnection ();
			func.apply (conn);			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			ret = sqle.getMessage();	
		} finally {
			returnConnection (conn);
		}
		return ret;

	}


	public String metaData (final SQLFunction func) {
		return connection (new SQLFunction (){
			public void apply (Connection obj) throws SQLException {
				func.apply (obj.getMetaData());
			}	
		});
	}

	/**
		executes the provided SQL Statement an invokes func once for
		each row in the result, providing an array of String as parameter.
		The passed array contains the content of each column of row.
	*/
	public String executeEachRowAsArray (String sql, final SafeFunction func) {
		return execute (sql, new SQLFunction () {
			public void apply (ResultSet rset) throws SQLException {
				int numCols = rset.getMetaData().getColumnCount();
				String [] row = new String [numCols];
				numCols++;
				while (rset.next()) {
					for (int i=1; i!=numCols; i++) {
						row[i-1] = rset.getString(i);
					}
					func.apply (row);
				}
			}			
		});	
	}
	
	/**
		Each row in the ResultSet is transformed into a String array 
		and passed to func.
	*/
	public static String eachRowAsArray (ResultSet rset, final Function func) {
		String res = null;
		try {
			int numCols = rset.getMetaData().getColumnCount();
			String [] row = new String [numCols];
			++numCols;
			while (rset.next()) {
				for (int i=1; i!=numCols; i++) {
					row[i-1] = rset.getString(i);
				}
				func.apply (row);
			}

		} catch (Throwable sqle){
			res = sqle.getMessage();	
		} finally {
			if (rset!=null)
				try {
					rset.close();	
				}catch (Throwable t){
					res += t.getMessage();
				}
					
		}
		return res;
	}
	/**
		Each row in the ResultSet is transformed into a String array 
		and passed to func.

		Not really necessary.
		
		
	*/ /*
	public static String eachRowAsArraySQLException (ResultSet rset, final SQLFunction func)  {
		String res = null;
		try {
			int numCols = rset.getMetaData().getColumnCount();
			String [] row = new String [numCols];
			++numCols;
			while (rset.next()) {
				for (int i=1; i!=numCols; i++) {
					row[i-1] = rset.getString(i);
				}
				func.apply (row);
			}

		} catch (SQLException sqle){
			res = sqle.getMessage();	
		}
		return res;
	}
	*/

	/**	
		Override this to provide e.g. Connection Pooling.
		though these are synchronised, they are IN NO WAY THREAD
		SAFE!  (obviously.)

		The default implementation of this method shares one
		connection for all callers.

		@see #returnConnection
		@see #close
	*/
	protected synchronized Connection getConnection () throws SQLException {
		if (_conn == null) {
			try {
				if (dataSource != null) {
					_conn = dataSource.getConnection();		
				} else {
					Class.forName (driver);
					_conn = DriverManager.getConnection (url, user, password);
				}
			} catch (ClassNotFoundException cnfe) {
				System.err.println("arg");
				throw new SQLException ("Driver-Class not found: "+driver);
			} 		
		}
		return _conn;
			
	
	}
	
	/**
		Although these are synchronised, they are IN NO WAY
		THREAD SAFE!  (obviously.) You might need to extend this
		class and override this method. The default
		implementation shares one java.sql.Connection among all
		callers and does nothing when return is called.

		@see #getConnection
		@see #close
	*/
	protected synchronized void returnConnection (Connection conn) {
			
	}

	/**
		Closes available connections.
		@see getConnection
		@see returnConnection
	*/
	public void close () {
		if (_conn != null) {
			try {
				_conn.close();	
				_conn = null;
			} catch (Throwable t) {
				t.printStackTrace();	
			}
		}		
	}

	protected void finalize () throws Throwable {
		close();
//		if (_conn != null)
//			_conn.close();
	}
	
	private Connection _conn;
}
