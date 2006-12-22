package utils;

/**
 * Utility class to aid creating SQL Insert Statements.
 * 
 * Usage:
 * <pre>
 * 		SqlInsert ins = new SqlInsert ("testTable");
		ins.addQuoted ("one", "1");
		ins.addQuoted ("two", "two");
		ins.addQuoted ("thee", "drei");
		ins.addQuoted ("leer", "");
		ins.addQuoted ("null", null);
		System.out.println(ins.getInsertStatement());
	</pre>
 * @author tim
 *
 */
public class SqlInsert {

	private String tableName;
	private StringBuffer cols = new StringBuffer();
	private StringBuffer vals = new StringBuffer();
	private boolean comma;
	
	/**
	 * 
	 * @param tableName the name of the table to create the statement for.
	 */
	public SqlInsert (String tableName) {
		this.tableName = tableName;	
	}
	
	/**
	 * Add a value for a column.
	 * @param columnName
	 * @param value
	 */
	public void add (String columnName, String value) {
		if (value == null || value.trim().length()==0)
			return;
			
		if (comma){
			cols.append(", ");
			vals.append(", ");
		} else {
			comma = true;	
		}
		
		cols.append (columnName);
		vals.append (value);
// difficulty: with quoted values, if the value is equals null or
// an empty string, I can just ignore it and not add anything, but what 
// to do in case of non-quoted, ie. numeric data.
		

	}

	/**
	 * Add a value for a column. Also wraps the value in SQL single quotes.
	 * @param columnName
	 * @param value
	 */
	public void addQuoted (String columnName, String value) {
		if (value == null || value.trim().length()==0)
			return;
			
		add (columnName, "'"+value+"'");
	}

	/**
	 * Returns true if the value for at least one column has been set.
	 * @return
	 */
	public boolean valid () {
		if (cols.length()!=0)
			return true;
		return false;
	}
	
	/**
	 * retrieve the generated INSERT statement.
	 * @return
	 */
	public String getInsertStatement () {
		if (!valid())
			return "-- no values";
		StringBuffer buf = new StringBuffer();
		buf.append ("INSERT INTO ");
		buf.append (tableName);
		buf.append (" (");
		buf.append (cols);
		buf.append (") VALUES (");
		buf.append (vals);
		buf.append (")");
		return buf.toString();
	}



	public static void main (String [] args) {
		SqlInsert ins = new SqlInsert ("testTable");
		ins.addQuoted ("one", "1");
		ins.addQuoted ("two", "two");
		ins.addQuoted ("thee", "drei");
		ins.addQuoted ("leer", "");
		ins.addQuoted ("null", null);
		System.out.println(ins.getInsertStatement());
	}
}
