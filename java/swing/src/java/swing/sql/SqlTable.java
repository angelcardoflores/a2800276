package swing.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.swing.JTable;

import swing.MainFrame;
import utils.DBCmdLine;

public class SqlTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String driver;
	String url;
	String user;
	String password;

	DataSource dataSource;

	
	public SqlTable (String driver, String url, String user, String password) {
		super(5,5);
		this.driver=driver;
		this.url=url;
		this.user=user;
		this.password=password;

	}
	public SqlTable (DataSource ds) {
		super();
		this.dataSource=ds;	
	}

	public void display (String sql) {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt=getConnection().createStatement();
			rset=stmt.executeQuery(sql);
			setModel(new ResultSetTableModel(rset));
		} catch (SQLException sqle){
			sqle.printStackTrace();	
		}
		
	}
	
	private Connection conn;
	protected synchronized Connection getConnection () throws SQLException {
		if (conn == null) {
			try {
				if (dataSource != null) {
					conn = dataSource.getConnection();		
				} else {
					Class.forName (driver);
					conn = DriverManager.getConnection (url, user, password);
				}
			} catch (ClassNotFoundException cnfe) {
				System.err.println("arg");
				throw new SQLException ("Driver-Class not found: "+driver);
			} 		
		}
		return conn;
			
	
	}

	public static void main (String [] args) {
		MainFrame mf = new MainFrame();
		DBCmdLine cmd = new DBCmdLine(args);
		mf.add(new SqlTable(cmd.getDriver(), cmd.getUrl(), cmd.getUser(), cmd.getPassword()));
	}

}
