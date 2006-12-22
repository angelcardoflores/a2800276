
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;


public class ResultSetTable { 

	public static void main(String args[]) {
		ResultSetFrame frame = new ResultSetFrame();

		frame.setVisible(true);
	}
}


abstract class ResultSetTableModel extends AbstractTableModel {
	public ResultSetTableModel(ResultSet aResultSet) {
		rs = aResultSet;
		try {
			rsmd = rs.getMetaData();
		} catch (SQLException e) {
			System.out.println("Error :" + e);
		}
	}

	public String getColumnName(int c) {
		try {
			return rsmd.getColumnName(c + 1);
		}catch (SQLException e) {
			System.out.println("Error :" + e);
			return "";
		}
	}

	public int getColumnCount() {
		try {
			return rsmd.getColumnCount();
		}catch (SQLException e) {
			System.out.println("Error :" + e);
			return 0;
		}
	}

	protected ResultSet getResultSet() {
		return rs;
	}
	private ResultSet rs;
	private ResultSetMetaData rsmd;
}


class ScrollingResultSetTableModel extends ResultSetTableModel {
	public ScrollingResultSetTableModel(ResultSet aResultSet) {
		super(aResultSet);
	}

	public Object getValueAt(int r, int c) {
		try {
			ResultSet rs = getResultSet();

			rs.absolute(r + 1);
			return rs.getObject(c + 1);
		}catch (SQLException e) {
			System.out.println("Error " + e);
			return null;
		}
	}

	public int getRowCount() {
		try {
			ResultSet rs = getResultSet();

			rs.last();
			return rs.getRow();
		}catch (SQLException e) {
			System.out.println("Error " + e);
			return 0;
		}
	}
}


class CachingResultSetTableModel extends ResultSetTableModel {
	public CachingResultSetTableModel(ResultSet aResultSet) {
		super(aResultSet);
		try {
			cache = new ArrayList();
			int cols = getColumnCount();
			ResultSet rs = getResultSet();

			while (rs.next()) {
				Object[] row = new Object[cols];

				for (int j = 0;
					j < row.length;
					j++) row[j] = rs.getObject(j + 1);
				cache.add(row);
			}
		}catch (SQLException e) {
			System.out.println("Error " + e);
		}
	}

	public Object getValueAt(int r, int c) {
		if (r < cache.size()) return ((Object[]) cache.get(r))[c];
		else return null;
	}

	public int getRowCount() {
		return cache.size();
	}
	private ArrayList cache;
}


class ResultSetFrame extends JFrame implements ActionListener {
	public ResultSetFrame() {
		setTitle("ResultSet");
		setSize(300, 200);
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
		Container contentPane = getContentPane();

		tableNames = new JComboBox();
		tableNames.addActionListener(this);
		JPanel p = new JPanel();

		p.add(tableNames);
		contentPane.add(p, "North");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}catch (Exception e) {
			System.out.println("Error " + e);
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/mysql", "gk", "gk");

			/*if(SCROLLABLE) { stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			} else{*/ 
			stmt = con.createStatement();

			//} 
			DatabaseMetaData md=con.getMetaData();
			ResultSet mrs = md.getTables(null, null, null, new String[] {"TABLE"}
				);

			while (mrs.next()) tableNames.addItem(mrs.getString(3));
			mrs.close();
		}catch (SQLException e) {
			System.out.println("Error " + e);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == tableNames) {
			if (scrollPane != null) getContentPane().remove(scrollPane);
			try {
				String tableName = (String) tableNames.getSelectedItem();

				if (rs != null) rs.close();
				String query = "select * from " + tableName;

				rs = stmt.executeQuery(query);
				if (SCROLLABLE) model = new ScrollingResultSetTableModel(rs);
				else model = new CachingResultSetTableModel(rs);
				JTable table = new JTable(model);

				scrollPane = new JScrollPane(table);
				getContentPane().add(scrollPane, "Center");
				pack();
				doLayout();
			}catch (SQLException e) {
				System.out.println("Error " + e);
			}
		}
	}
	private JScrollPane scrollPane;
	private ResultSetTableModel model;
	private JComboBox tableNames;
	private JButton nextButton;
	private JButton previousButton;
	private ResultSet rs;
	private Connection con;
	private Statement stmt;
	private static boolean SCROLLABLE = false;
}

