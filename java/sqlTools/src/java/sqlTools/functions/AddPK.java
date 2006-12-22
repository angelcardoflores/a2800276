package sqlTools.functions;

import sqlTools.PrimaryKey;
import sqlTools.Table;
public class AddPK extends IndexFunction {
	public AddPK (Table t) {
		super(t);
	}

	String getName (String [] columns) {
		return columns[5];	
	}
	
	String getColumnName (String [] columns){
		return columns[3];	
	}

	void makeConstraint () {
		try {
			new PrimaryKey (name, cols.getCols());	
		} catch  (Throwable t) {
			
		}
	}

}
