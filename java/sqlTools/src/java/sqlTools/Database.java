package sqlTools;

import function.*;
import function.sql.SQLExecuter;
import function.sql.SQLFunction;

import java.util.*;
import java.sql.*;

public class Database {

	private LinkedList catalogs = new LinkedList();
	private static final Catalog [] catalogArray = new Catalog [0];
	
	private LinkedList schema = new LinkedList();
	private static final Schema [] schemaArray = new Schema [0];
	
	private LinkedList tables = new LinkedList();
	private static final Table [] tableArray = new Table [0];
	
	private LinkedList procedures = new LinkedList();
	private static final Procedure [] procedureArray = new Procedure [0];

	private SafeFunction AddCat = new SafeFunction () {
		public void apply (Object obj) {
			String [] arr = (String[])obj;
	System.out.println ("Catalog: "+arr[0]);
			addCatalog(new Catalog(arr[0]));	
		}
	};
	
	private SafeFunction AddSchema = new SafeFunction () {
		public void apply (Object obj) {
			String [] arr = (String[])obj;
	System.out.println ("Schema: "+arr[0]);
			addSchema(new Schema(arr[0]));	
		}
	};

	private SafeFunction AddTab = new SafeFunction () {
		public void apply (Object obj) {
			String [] arr = (String []) obj;
	System.out.println ("table_name: "+ arr[2]);
			addTable (new Table (arr[2], getSchema(arr[1]), getCatalog(arr[0]), arr[3], arr[4]));	
		}	
	};

	private SafeFunction AddProc = new SafeFunction () {
		public void apply (Object obj) {
			String [] arr = (String []) obj;
			addProcedure (new Procedure (arr[2], getSchema(arr[1]), getCatalog(arr[0]), arr[6], Integer.parseInt(arr[7])));
		}	
	};

	
	public Database (final SQLExecuter exe) {
		exe.connection (new SQLFunction () {
			public void apply (DatabaseMetaData meta) throws SQLException {
				
				SQLExecuter.eachRowAsArray (meta.getCatalogs(), AddCat);
					addCatalog(new Catalog("")); // Default 
				
				SQLExecuter.eachRowAsArray (meta.getSchemas(), AddSchema);
					addSchema(new Schema(""));

				SQLExecuter.eachRowAsArray (meta.getTables(null, "information_schema", "applicable_roles", null), AddTab);
				SQLExecuter.eachRowAsArray (meta.getProcedures(null, "%", "%"), AddProc);

				initTable (meta, exe);
			}	
		});
		
	}

	private void initTable(DatabaseMetaData meta, final SQLExecuter exe) throws SQLException {
		final LinkedList list = new LinkedList ();
		SQLExecuter.eachRowAsArray (meta.getTableTypes(), new SafeFunction () {
			public void apply (Object obj){
				list.add (((String[])obj)[0]);
			}
		});
		tTypes = (String [])list.toArray(new String[0]);
		SQLExecuter.eachRowAsArray (meta.getColumns(null, "%", "%", "%"), new SafeFunction () {
			public void apply (Object obj){
				String [] arr = (String [])obj;
System.out.println (": "+arr[1]+ " : "+arr[2]);
				Table tab = getTable (getSchema(arr[1]), arr[2]);
				tab.addColumn(new Column(arr));	
			}	
		});		
		
		
		
	}

	private String [] tTypes;
	public String [] getTableTypes () {
		return this.tTypes;	
	}
	
	public Catalog [] getCatalogs () {
		return (Catalog [])catalogs.toArray(catalogArray);	
	}
	public int numCatalogs () {
		return catalogs.size();	
	}
	
	public Catalog getCatalog (String name) {
		if (name==null)
			name = "";
		for (Iterator it = this.catalogs.iterator(); it.hasNext();){
			Catalog cat = (Catalog)it.next();
			if (cat.getName().equals(name))
				return cat;
		}
		return null;
	}
	
	public void addCatalog (Catalog catalog) {
		catalogs.add (catalog);	
	}

	public Schema [] getSchema () {
		return (Schema [])schema.toArray(schemaArray);
	}

	public int numSchema () {
		return this.schema.size();	
	}

	public void addSchema (Schema schema) {
		this.schema.add(schema);	
	}
	
	private String sCache;
	private Schema sCacheS;
	public Schema getSchema (String name) {
		
		if (name==null)
			name = "";
		
		if (name.equals (sCache))
			return sCacheS;
			
		Schema ret = null;
		for (Iterator it = this.schema.iterator(); it.hasNext();){
			Schema schema = (Schema)it.next();
			if (schema.getName().equals(name)){
				ret = schema;	
				break;
			}
		}

		sCache=name;
		sCacheS=ret;
		return ret;
	}

	public Table [] getTables () {
		return (Table [])tables.toArray(tableArray);
	}

	public void addTable (Table table) {
		this.tables.add(table);	
	}

	public Table [] getTables (Schema schema) {
		LinkedList list = new LinkedList ();
		Table table = null;
		if (schema == null)
			schema = new Schema("");
		for (Iterator it = tables.iterator(); it.hasNext();) {
			table = (Table)it.next();
			//System.out.println ("t: "+table.getName());
//			System.out.println ("t: "+table.getSchema());
			
//			System.out.println ("s: "+schema);
			if (schema.equals(table.getSchema()))
				list.add(table);
		}
		return (Table [])list.toArray(tableArray);
	}

	public Table [] getTables (Catalog catalog) {
		LinkedList list = new LinkedList ();
		Table table = null;
		if (catalog == null)
			catalog = new Catalog("");
		for (Iterator it = tables.iterator(); it.hasNext();) {
			table = (Table)it.next();
			//System.out.println ("t: "+table.getName());
//			System.out.println ("t: "+table.getSchema());
			
//			System.out.println ("s: "+schema);
			if (catalog.equals(table.getCatalog()))
				list.add(table);
		}
		return (Table [])list.toArray(tableArray);
	}
	private String tCache;
	private Schema tCacheS;
	private Table tCacheT;

	public Table getTable (Schema schema, String name) {
		
		if (schema.equals(tCacheS) && name.equals(tCache))
			return tCacheT;
			
		Table[] t =getTables(schema);
		Table ret = null;
		for (int i=0; i!=t.length; ++i){
			if (name.equals(t[i].getName())){
				ret = t[i];	
				break;
			}
		}
		if (ret == null){

			ret = new Table ("unknown: "+name, schema, null, "?", "fuck this shit");
			addTable (ret);
			
		}
		tCacheT = ret;
		tCacheS = schema;
		tCache = name;
		return ret;
	}

	public Procedure [] getProcedures () {
		return (Procedure [])procedures.toArray(procedureArray);	
	}

	public void addProcedure (Procedure proc) {
		this.procedures.add(proc);	
	}

	public Procedure [] getProcedures (Schema schema) {
		LinkedList list = new LinkedList ();
		Procedure proc = null;
		schema = schema ==null?new Schema(""):schema;
		for (Iterator it = procedures.iterator(); it.hasNext();) {
			proc = (Procedure)it.next();
			if (schema.equals(proc.getSchema()))
				list.add(proc);
		}
		return (Procedure [])list.toArray(procedureArray);
	}

	
}
