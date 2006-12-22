package sqlTools.functions;

import sqlTools.Index;
import sqlTools.Table;
public class AddIndices extends IndexFunction {
	boolean unique;

	public AddIndices (Table t) {
		super(t);
	}

	String getName (String [] columns) {
		return columns[5];	
	}

	String getColumnName (String [] columns){
		return columns[8];	
	}

	void makeConstraint () {
		try {
			new Index (name, cols.getCols(), unique);	
		} catch  (Throwable t) {
			
		}
	}

	void doSpecifics (String [] args) {
		unique = "1".equals(args[3]);
	}


}

