package sqlTools.functions;

import sqlTools.Column;
import sqlTools.Table;
import function.SafeFunction;

public class AddColumn implements SafeFunction {
	Table t;
	Column c;
	public AddColumn (Table t) {
		this.t = t;	
	}	
	public void apply (Object obj) {
		String [] arr = (String[])obj;
		c = new Column (arr[3],t,arr[4],arr[11]);	
	}
}

