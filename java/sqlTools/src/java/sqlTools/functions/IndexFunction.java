package sqlTools.functions;
import sqlTools.*;
import function.*;

public abstract class IndexFunction implements SafeFunction {
	
	Table t;
	Columns cols;
	String name;
	
	public IndexFunction (Table t) {
		this.t = t;	
		cols = new Columns(t);
	}
	
	public void apply (Object obj){
		String [] arr = (String[])obj;
		if (getName(arr)==null) return;
		
		if (!getName(arr).equals(name)) {
			makeConstraint();
		}

		name=getName(arr);
		cols.apply(getColumnName(arr));
		doSpecifics (arr);
		
	}
	public void finish (){
		if (name != null) makeConstraint();		
	}
	abstract String getName (String [] columns);
	abstract String getColumnName (String [] columns);
	/** make whatever type of constraint necessary out of table t and Columns cols*/
	abstract void makeConstraint ();
	/** implement this in case you need to extract other field from the array*/
 	void doSpecifics (String [] args){
		
	}
	
		
}
