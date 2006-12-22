package sqlTools.dot;

import dot.*;
import sqlTools.*;
import java.sql.*;

public class TestDot {
	

	public static void main (String [] args) {
		try {
			
		
		Table t = new Table ("TestTable", null, null, "Table", null);
		
		Column col1 = new Column ("Column1", t, Types.VARCHAR, null);
		Column col2 = new Column ("Column2", t, Types.ARRAY, null);
		
		t.addColumn (col1);
		t.addColumn (col2);
		t.addColumn (new Column ("Column3", t, Types.BLOB, null));

		Column [] cols = {
			col1, col2	
		};
		
		ReferencedKey rk = new ReferencedKey("RK_001", cols);

		

		Table t1 = new Table ("TestTable2", null, null, "Table", null);
		
		Column col3 = new Column ("2Column1", t1, Types.VARCHAR, null);
		Column col4 = new Column ("2Column2", t1, Types.ARRAY, null);
		
		t1.addColumn (col3);
		t1.addColumn (col4);
		
		cols=new Column[2];
		cols[0] = col3;
		cols[1] = col4;
		
		t1.addColumn (col3);
		t1.addColumn (col4);
		ForeignKey fk = new ForeignKey ("FK_001", cols, rk, true);
		t1.addColumn (new Column ("2Column3", t, Types.BLOB, null));
		
		Schema s = new Schema("Test");
		s.addTable (t);
		s.addTable (t1);
		
		
		Graph g = DotDB.getGraph(s);
		System.out.println(g.pack());
		
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
	}
}
