package oraDataDictionary;

import html.ATag;
import function.SafeFunction;
import function.sql.SQLExecuter;

public class OraDataDictionaryDoc {
	
	final static String selectTables = "SELECT table_name, comments FROM dictionary  order by table_name  ";
	final static String selectCols = "select column_name, comments from dict_columns where table_name ='";
	
	Formatter format = new Formatter("./oraDoc", "8.1.7");

	SQLExecuter exe;
	
	OraDataDictionaryDoc (String driver, String url, String user, String password) {
		exe = new SQLExecuter (driver, url, user, password);
	}

	void go () {
		System.out.println ("Starting");
		String result = exe.executeEachRowAsArray (selectTables, new Tables());	
		format.finish();
		if (result != null)
			System.err.println (result);
//		try {
//			index.writeToDisc();
//		} catch (Throwable t) {
//			t.printStackTrace();	
//		}
	}

		

	/**
		Will recieve a table_name, comment array.
	*/
	class Tables implements SafeFunction {
		
		
		/**
			Is passed an array for each DDTable. Array 
			contains arr[0] tablename arr[1] tableComments
		*/
		public void apply (Object obj) {
			String [] str = (String []) obj;
			System.out.println ("Processing: "+str[0]);
			
			DDTable table = new DDTable (str [0], str [1]);
			
			String select = selectCols+str[0]+"' order by column_name";
			exe.executeEachRowAsArray (select, new Columns (table));
			
			format (table);
			
				
		}
	}

	class Columns implements SafeFunction {
		DDTable table;
		Columns (DDTable table) {
			this.table=table;
		}

		public void apply (Object obj) {
			String [] cols = (String[])obj;
			table.addColumn(cols[0], cols[1]);
		}
	}

	static ATag makeLink (String tableName) {
		ATag tag = new ATag (tableName);
		tag.setHref (tableName+".html");
		tag.setAnchor (tableName);
		return tag;
	}

	void format (DDTable table) {
		// Overview (alphabetical)
		// Table page
		// index -> each col
		// index thematically, table, col
		this.format.formatTable(table);
		
	}
	

	
	private static void usage () {
		System.err.println ("[jre] OraDataDictionary <driver> <url> <user> <password>");
		System.exit(0);
	}
	
	public static void main (String [] args) {
		if (args.length !=4)
			usage();
		OraDataDictionaryDoc ora= new OraDataDictionaryDoc (args[0], args[1], args[2], args[3]);
		ora.go();	
		
		
	}
}

