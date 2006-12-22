package sqlTools.functions;

import sqlTools.ReferencedKey;
import sqlTools.Table;
public class AddRef extends IndexFunction {
	
	public AddRef (Table t) {
		super(t);
	}
	String getName (String [] columns) {
		return columns[12];	
	}

	String getColumnName (String [] columns){
		return columns[3];	
	}

	void makeConstraint () {
		try {
			new ReferencedKey(name, cols.getCols());	
		} catch (Throwable t){
			
		}	
	}
}
