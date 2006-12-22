package sqlTools.orm.generator;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import sqlTools.TypesUtil;
import sqlTools.orm.Utils;
import utils.DBCmdLine;
import function.sql.SQLExecuter;
import function.sql.SQLFunction;
import generator.Field;

public class Generator {
	
	String table;
	String pckg;
	SQLExecuter exe;
	boolean protect;
	boolean get;
	boolean set;
	
	public Generator (String table, SQLExecuter exe) {
		this.table = table;	
		this.exe = exe;
	}

	public void setPackage (String packageName) {
		this.pckg = packageName;
	}

	public void setProtected (boolean protect){
		this.protect = protect;	
	}

	public void setGet (boolean get) {
		this.get = get;	
	}
	public void setSet (boolean set) {
		this.set = set;	
	}
	

	public String getPackage(){
		return this.pckg;	
	}
	public String getTable(){
		return table;	
	}

	
	public String toString () {
		

		generator.Class clazz = new generator.Class (Utils.convertToJavaClassName(getTable()), getPackage());
		
		clazz.setExtends (new generator.Class("CmdLineOrm", "sqlTools.orm"));
		clazz.addImport (new generator.Class("*", "java.sql"));
		clazz.addImport (new generator.Class("*", "java.math"));
		clazz.addImport (new generator.Class("*", "sqlTools.orm"));
		
		for (Iterator it = getDbFields(); it.hasNext(); ){
			NameType nt = (NameType)it.next();
			clazz.addField	(new Field(
						Utils.convertToJavaName(nt.name), 
						new generator.Class (Utils.getClassName(TypesUtil.getJavaType(nt.type))),
						set,
						get,
						protect ? java.lang.reflect.Modifier.PROTECTED : java.lang.reflect.Modifier.PUBLIC
						)
					);
		}	
		return clazz.getCode();
	}


	public Iterator getDbFields(){
		if (list == null) {
			list = new LinkedList();
			exe.metaData (new SQLFunction(){
				public void apply (DatabaseMetaData meta) throws SQLException {
					ResultSet rset = meta.getColumns(null, null, table, "%");
					while (rset.next()){
						list.add(new NameType(rset.getString("COLUMN_NAME"), rset.getInt("DATA_TYPE")));
					}
					if (rset!=null) rset.close();
				}	
			});


		}
		return list.iterator();
	}
	LinkedList list;

	class NameType {
		String name;
		int type;
		NameType (String name, int type) {
			this.name = name;
			this.type = type;
		}	
	}

	public static void main(String [] args) {
		DBCmdLine cmd = new DBCmdLine(args);
		if (!cmd.complete() || cmd.get("-table")==null || cmd.get("--help")!=null ) {
			System.err.println("[jre] sqlTools.orm.Generator");
			System.err.println("\t -table <table>");
			System.err.println("\t -package <package> (optional, the package the generated class should belong to.)");
			System.err.println("\t -protected <true/false> (optional, make fields protected.)");
			System.err.println("\t -get <true/false> (optional, generate getter methods.)");
			System.err.println("\t -set <true/false> (optional, generate setter methods.)");
			
			System.err.println(DBCmdLine.usage());
			System.exit(1);
		}
		SQLExecuter exe = new SQLExecuter (cmd);
		Generator gen = new Generator (cmd.get("-table"), exe);
		if (cmd.get("-package")!=null) gen.setPackage(cmd.get("-package"));
		gen.setProtected(Boolean.valueOf(cmd.get("-protected")).booleanValue());
		gen.setGet(Boolean.valueOf(cmd.get("-get")).booleanValue());
		gen.setSet(Boolean.valueOf(cmd.get("-set")).booleanValue());
		System.out.println(gen);
		exe.close();
		
		
	}
	
}
