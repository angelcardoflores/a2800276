package sqlTools.functions;

import sqlTools.ContainerObject;
import sqlTools.ForeignKey;
import sqlTools.ReferencedKey;
import sqlTools.Table;
public class AddFK extends IndexFunction {
	
	ContainerObject [] containers;
	String rk_table;
	String rk_schema;
	String rk_name;

	/**
		objs - the schema or catalogs to search for
		keys referenced by the foreign key.
	*/
	public AddFK (Table t, ContainerObject [] objs) {
		super (t);
		containers = objs;
	}
	
	String getName (String [] columns) {
		return columns[11];	
	}

	String getColumnName (String [] columns){
		return columns[7];	
	}

	void doSpecifics (String [] args) {
		rk_schema = args[1];
		rk_name = args[12];
		rk_table = args[2];
	}

	void makeConstraint () {
		try {
			new ForeignKey (name, cols.getCols(), getRK(), false);	
		} catch (Throwable t){
				
		}
	}

	ReferencedKey getRK () {
		ContainerObject cont = null;
		for (int i=0; i!=containers.length;++i){
			if (containers[i].getName().equals(rk_schema)){
				cont = containers[i];
				break;
			}	
			
		}
//		System.err.println(name+"\n");
//		if (cont!=null)
//			System.err.println("getting RK: "+rk_schema+" - "+cont.getName());
		Table t = cont.getTable(rk_table);
//		if (t!=null)
//			System.err.println("getting RK: "+rk_table+" - "+t.getName());
		return t.getReferencedKey(rk_name);
	}


}
