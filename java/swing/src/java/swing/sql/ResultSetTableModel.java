package swing.sql;

import javax.swing.table.*;
import java.sql.*;


public class ResultSetTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ResultSet rset;
	public ResultSetTableModel (ResultSet rset) {
		this.rset = rset;	
	}
	public int getRowCount() {
		final W result = new W();
		execute (new F () {
			void apply (ResultSet rset) throws SQLException {
				rset.last();
				result.set(rset.getRow());
				rset.beforeFirst();
			}	
		});
		return result.i();
	}

	public int getColumnCount() {
		final W result= new W();
		execute (new F() {
			void apply (ResultSet rset) throws SQLException {
				ResultSetMetaData meta = rset.getMetaData();
				result.set(meta.getColumnCount());
			}	
		});
		return result.i();
		
	}
			
	public Object getValueAt(int row, int column) {
		final W str = new W();
		final int r=row;
		final int c=column;
		execute (new F(){
			void apply (ResultSet rset) throws SQLException {
				rset.absolute(r);
				str.set(rset.getString(c));
			}	
		});	
		return str.s();
	}

	void execute (F func) {
		try {
			func.apply(rset);	
		} catch (SQLException sqle) {
			sqle.printStackTrace();	
		}
	}

}

class W {
	int i;
	String s;
	int i () {
		return i;	
	}
	String s() {
		return s;	
	}
	void set (int i){
		this.i=i;	
	}
	void set (String s) {
		this.s=s;	
	}

	
}

abstract class F {
	abstract void apply (ResultSet rset) throws SQLException;
}
