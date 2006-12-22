package sqlTools;

import sqlTools.collection.FuncHashMap;
import function.Function;
import function.SafeFunction;

/**
	Catalogs and Schema seem to be basically the same thing, it just
	depends on what the database implementation calls it's way of partitioning
	the database.

	postgres - schema
	oracle - schema

	mysql - catalog

*/
public abstract class ContainerObject extends DBObject {
	
	public  ContainerObject (String name) {
		super (name);	
	}

	FuncHashMap tables = new FuncHashMap();

	public Table getTable (String name) {
		return (Table)tables.get(name);
	}

	public void addTable (Table table) {
		tables.put(table.getName(), table);	
	}

	public Table[] getTables () {
		return (Table[])tables.values().toArray(new Table[0]);
	}

	public int numTables  () {
		return tables.size();	
	}

	public String eachTable (Function func) {
		return tables.eachValue(func);
	}

	public void toString (final StringBuffer buf) {
		buf.append (getName());
		buf.append ("\n");
		eachTable (new SafeFunction () {
			public void apply (Object obj) {
				((Table)obj).toString(buf);	
			}	
		});
	}
}
