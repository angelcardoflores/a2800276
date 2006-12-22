package swing.sql;

import java.sql.ResultSet;

import javax.swing.JTable;

public class ResultSetTable extends JTable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResultSetTable (ResultSet rset) {
		super (new ResultSetTableModel(rset));	
	}
}

