package sqlTools.functions;

import sqlTools.*;
import function.*;
import java.util.*;
public class Columns implements SafeFunction {

	Table t;
	LinkedList list = new LinkedList();
	public Columns (Table t) {
		this.t=t;	
	}
	public Column [] getCols () {
		Column []cols= (Column [])list.toArray(new Column[0]);
		list = new LinkedList();
		return cols;
	}

	public void apply (Object colName) {
		list.add (t.getColumn((String)colName));	
	}
}

