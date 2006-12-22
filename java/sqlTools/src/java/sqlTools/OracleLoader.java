package sqlTools;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import sqlTools.functions.AddColumn;
import sqlTools.functions.AddFK;
import sqlTools.functions.AddIndices;
import sqlTools.functions.AddPK;
import sqlTools.functions.AddRef;
import sqlTools.functions.IndexFunction;
import sqlTools.functions.ToSchemaArray;
import utils.CmdLine;
import dot.Graph;
import function.Function;
import function.SafeFunction;
import function.sql.SQLExecuter;
import function.sql.SQLFunction;


public class OracleLoader {

	private static Schema [] all_schema;

	public static Schema [] getSchema (final SQLExecuter exe) {
		
		final ToSchemaArray func = new ToSchemaArray();
		exe.metaData (new SQLFunction (){
			
			public void apply (DatabaseMetaData meta) throws SQLException {
				// num Schema?
				SQLExecuter.eachRowAsArray (meta.getSchemas(), func);
			} 
		
		}
		);	
		return func.toArray();
	}

	public static void loadTables (final Schema [] s, final SQLExecuter exe) {
		System.err.println("loading Schemas");
		// for each schema
		exe.metaData (new SQLFunction () {
			public void apply (DatabaseMetaData meta) throws SQLException {
				for (int i=0; i!=s.length; ++i) {
					// get Tables
					System.err.println ("getting MetaData for: "+s[i]+" with: "+meta);
					//meta.getTables(null, s[i].getName(), "%", null);
					loadTables_internal (exe, meta, s[i]);
				}

			}	
		});
	}

	

	private static void loadTables_internal (final SQLExecuter exe, final DatabaseMetaData meta, final Schema schema) throws SQLException {
		SQLExecuter.eachRowAsArray (meta.getTables(null, schema.getName(), "%", null), new SafeFunction () {
			public void apply (Object obj) {
				String [] arr = (String[])obj;
				schema.addTable(new Table(arr[2], schema, null, arr[3], arr[4]));	
			}	
		});
		
		schema.eachTable (new Function() {
			public void apply (Object obj) throws Throwable{
				Table t = (Table)obj;
				SQLExecuter.eachRowAsArray (
					meta.getColumns(null, schema.getName(),t.getName(),"%"),
					new AddColumn(t)
				);		
				
			}	
		});
	}

	private static void loadConstraints (final SQLExecuter exe, final Table t) {
		System.err.println("Loading constraints for: "+t.getName());
		if (!t.getType().equals("TABLE")) return;
			
		//loadIndices
		exe.metaData (new SQLFunction () {
			public void apply (DatabaseMetaData meta) throws SQLException {
				AddIndices func = new AddIndices(t);
				SQLExecuter.eachRowAsArray (
					meta.getIndexInfo (null, t.getSchema().getName(), t.getName(), false, true),
					//meta.getIndexInfo (null, null, t.getName(), false, true),
					func	
				);
				func.finish();

				// PK
				//addPK(exe, t);
				addConstraint (
					exe,
					meta.getPrimaryKeys (null, t.getSchema().getName(), t.getName()),
					new AddPK(t)
				);
				// ReferencedKeys
				addConstraint (
					exe,
					meta.getExportedKeys (null, t.getSchema().getName(), t.getName()),
					new AddRef(t)
				);
				
				
			}	
		});
	}
	
	private static void loadFK (final SQLExecuter exe, final Table t) {
		if (!t.getType().equals("TABLE")) return;
		System.err.println("Loading Foreign Keys for: "+t.getName());
			
		//loadIndices
		exe.metaData (new SQLFunction () {
			public void apply (DatabaseMetaData meta) throws SQLException {
				
			

				// ForeignKeys
				//addForeigenKeys(exe,t);
				addConstraint (
					exe,
					meta.getImportedKeys (null, t.getSchema().getName(), t.getName()),
					new AddFK(t, all_schema)
				);
			}
		});

	}
	private static void addConstraint (final SQLExecuter exe, final ResultSet rset, final IndexFunction func) {
		SQLExecuter.eachRowAsArray (rset,func);
		func.finish();
	}
	
	
	
		
	
	

	static class KeyValues implements SafeFunction {
		
		HashMap map = new HashMap();
		Object [] cast;
		public KeyValues (Object [] obj){
			cast = obj;	
		}
		public void apply (Object obj) {
			Object [] arr = (Object[])obj;
			LinkedList list = (LinkedList)map.get(arr[0]);
			if (list == null) {
				list = new LinkedList();
				map.put(arr[0],list);
			}
			list.add(arr[1]);
		}

		public String each (Function func) {
			try {
				for (Iterator it = map.keySet().iterator(); it.hasNext();) {
					Object obj = it.next();
					LinkedList list = (LinkedList)map.get(obj);
					Object [] arr = new Object[2];
					arr[0]=obj;
					arr[1]=list.toArray(cast);
					func.apply (arr);
									
				}
					
			}catch (Throwable t) {
				t.printStackTrace();
				return t.getMessage();	
			}
			return null;
		}

		
	}
	
	
	

	
	private static void usage () {
		String usage = 	"usage: [jre] sqlTools.OracleLoader \n"+
				"\t -driver <driver>\n"+
				"\t -url <url>\n"+
				"\t -user <user>\n"+
				"\t -password <password>";
		System.err.println(usage);
		System.exit(1);
	}
	public static void main (String [] args) {
		CmdLine cmd = new CmdLine (args);
		String driver = cmd.get("-driver");
		String url = cmd.get("-url");
		String user = cmd.get ("-user");
		String password = cmd.get("-password");
		
		if (
			driver == null ||
			url == null ||
			user == null ||
			password == null 
		) {
			usage();	
		}

		System.err.println("About to connect: "+driver+" : "+url+" : "+user +" : "+password);		
		final SQLExecuter exe = new SQLExecuter (driver, url, user, password);
			
//		exe.metaData(new SQLFunction() {
//			public void apply (DatabaseMetaData meta) throws SQLException {
//				ResultSet rset = meta.getSchemas();
//				while (rset.next()){
//					System.out.println(rset.getString(1));	
//				}
//			}	
//		});
		
		//Schema [] schemata = getSchema(exe);
		Schema [] schemata = new Schema[1];
		schemata[0]=new Schema("BOB");
		all_schema = schemata;
		loadTables (schemata, exe);	

		
		System.err.println("loaded tables: "+schemata[0].numTables());		
		schemata[0].eachTable (new SafeFunction () {
			public void apply(Object obj) {
				System.out.println(obj);	
			}	
		});
		System.exit(0);
		
		
		for (int i=0; i!=schemata.length; ++i){
			schemata[i].eachTable (new SafeFunction () {
				public void apply (Object obj) {
					loadConstraints (exe, (Table)obj);	
				}	
			});
			schemata[i].eachTable (new SafeFunction () {
				public void apply (Object obj) {
					loadFK(exe, (Table)obj);	
				}	
			});
			//System.out.println (schemata[i]);	
		}
	//******************** AD HOC STUFF********************  	
		System.err.println(schemata[0].numTables());		
		Graph g = sqlTools.dot.DotDB.getGraph(schemata[0]);
		System.out.println(g.pack());

	}//main
}
