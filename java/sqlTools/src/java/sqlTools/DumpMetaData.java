package sqlTools;

import utils.*;
import function.*;
import function.sql.SQLExecuter;
import function.sql.SQLFunction;

import java.sql.*;
import javax.naming.*;

public class DumpMetaData {
	
	static String driver, url, user, password, dataSourceName;
	static DatabaseMetaData meta;

	private static void usage () {
		String usage = 	"usage: [jre] sqlTools.DumpMetaData \n"+
				"\t [-driver <driver>\n"+
				"\t  -url <url>\n"+
				"\t  -user <user>\n"+
				"\t  -password <password>]\n"+
				"\t|[-jndi <dataSourceName>]\n"+
				"if using JNDI, be sure the properties: java.naming.factory.initial and\n"+
				"java.naming.provider.url are set properly.";
		System.err.println(usage);
		System.exit(1);
	}
	
	public static void main (String [] args) {
		CmdLine cmd = new CmdLine (args);
		driver = cmd.get("-driver");
		url = cmd.get("-url");
		user = cmd.get ("-user");
		password = cmd.get("-password");
		dataSourceName = cmd.get("-jndi");
		
		if (
			(dataSourceName == null) &&
			(
			driver == null ||
			url == null ||
			user == null ||
			password == null 
			)
		) {
			usage();	
		}
		
		SQLExecuter exe = null;
		if(dataSourceName == null)
			exe = new SQLExecuter (driver, url, user, password);
		else {
			try {
				Context ctx=new InitialContext();
				exe = new SQLExecuter ((javax.sql.DataSource)ctx.lookup(dataSourceName));
			} catch (NamingException ne) {
				ne.printStackTrace();
				System.exit(1);
			}
		}

		final SafeFunction printArray = new SafeFunction () {
			public void apply (Object obj) {
				String [] arr = (String[])obj;
				for (int i=0; i!=arr.length; ++i) {
					System.out.print (arr[i]+" | ");	
				}
				System.out.print("\n");
			}	
		};

		final SQLFunction printTableWithColumns = new SQLFunction () {
			public void apply (String [] arr) throws SQLException{
				for (int i=0; i!=arr.length; ++i) {
					System.out.print (arr[i]+" | ");	
				}
				System.out.println("---");
				SQLExecuter.eachRowAsArray (meta.getColumns(null, user.toUpperCase(), arr[2], "%"), printArray);

			}	
		};
		
		exe.metaData (new SQLFunction () {
			public void apply (DatabaseMetaData meta) throws SQLException {
				System.out.println ("User: "+meta.getUserName());
				System.out.println ("Catalog: "+meta.getCatalogTerm());
				System.out.println ("Catalogs: ");
				SQLExecuter.eachRowAsArray (meta.getCatalogs(), printArray);
				
				System.out.println("DatabaseProductName :"+meta.getDatabaseProductName());
				System.out.println("DatabaseProductVersion :"+meta.getDatabaseProductVersion());
				System.out.println("DriverMajorVersion :"+meta.getDriverMajorVersion());
				System.out.println("DriverMinorVersion :"+meta.getDriverMinorVersion());
				System.out.println("DriverName :"+meta.getDriverName());
				System.out.println("DriverVersion :"+meta.getDriverVersion());
				System.out.println("SchemaTerm :"+meta.getSchemaTerm());

				System.out.println("Schemas :");
				SQLExecuter.eachRowAsArray (meta.getSchemas(), printArray);

				System.out.println("Tables :");
				SQLExecuter.eachRowAsArray(meta.getTables(null, meta.getUserName(), "%", null), printArray);

				System.out.println("Columns :");
				SQLExecuter.eachRowAsArray (meta.getColumns(null, "%", "PSP_KKE", null), printArray);
				
				
				System.out.println ("TypeInfo :");
				SQLExecuter.eachRowAsArray (meta.getTypeInfo(), printArray);

//				System.out.println ("Indices :");
//				SQLExecuter.eachRowAsArray (meta.getIndexInfo(null, "TWBROKAT", "ZVT_FRAUD", false, true), printArray);
//				
//				System.out.println ("Exported :");
//				SQLExecuter.eachRowAsArray (meta.getExportedKeys(null, null, "TXN"), printArray);
//				
//				System.out.println ("Exported p :");
//				SQLExecuter.eachRowAsArray (meta.getExportedKeys(null, null, "PSP_KKE"), printArray);
//
//				System.out.println ("Imported p :");
//				SQLExecuter.eachRowAsArray (meta.getImportedKeys(null, null, "PSP_KKE"), printArray);
//
//				System.out.println ("Prims: ");
//				SQLExecuter.eachRowAsArray (meta.getPrimaryKeys(null, "TWBROKAT", "ZVT_FRAUD"), printArray);

					
			}
		}
		);
		

		
		
	}
}
