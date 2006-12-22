package sqlTools;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import utils.CmdLine;
import function.sql.SQLExecuter;

public class DumpMetaDataGUI {
	
	static String driver, url, user, password;

	private static void usage () {
		String usage = 	"usage: [jre] sqlTools.DumpMetaDataGUI \n"+
				"\t -driver <driver>\n"+
				"\t -url <url>\n"+
				"\t -user <user>\n"+
				"\t -password <password>";
		System.err.println(usage);
		System.exit(1);
	}
	
	public static TreeNode getDBTreeRoot(Database db) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode("Database");
		addSchema (node, db);
		addCatalogs (node, db);
		return node;
	}

	public static void addSchema (DefaultMutableTreeNode node, Database db) {
		DefaultMutableTreeNode schema = null;
		Schema [] s = db.getSchema();
		System.out.println ("Adding "+s.length+" Schema ...");
		System.out.println ("Please be patient, this might take forever ...");
		for (int i=0; i!=s.length; ++i ) {
			schema = new DefaultMutableTreeNode (s[i].getName());
			node.add (schema);
			addTables (schema, db, s[i]);
			addProcs (schema, db, s[i]);
		}

	}

	public static void addCatalogs (DefaultMutableTreeNode node, Database db) {
		DefaultMutableTreeNode cat = null;
		Catalog [] s = db.getCatalogs();
		System.out.println ("Adding "+s.length+" Catalogs ...");
		System.out.println ("Please be patient, this might take forever ...");
		for (int i=0; i!=s.length; ++i ) {
			cat = new DefaultMutableTreeNode (s[i].getName());
			node.add (cat);
			addTables (cat, db, s[i]);
			//addProcs (cat, db, s[i]);
		}

	}
	static void addTables (DefaultMutableTreeNode node, Database db, Schema sch) {
		System.out.println ("Adding Tables for: "+sch.getName());
		DefaultMutableTreeNode table = null; 
		Table [] t = db.getTables (sch);
		// add one tree entry for each table type in Database.
		for (int i=0; i!=db.getTableTypes().length; ++i) {
			table = new DefaultMutableTreeNode (db.getTableTypes()[i]);
			node.add(table);
			addTableTypes (table, db.getTableTypes()[i], t);
		}
		return;
		/*
		for (int i=0; i!=t.length; ++i) {
			table = new DefaultMutableTreeNode (t[i].getName());
		//	System.out.println ("Adding: "+sch.getName()+"."+t[i].getName());
			node.add(table);
			table.add (new DefaultMutableTreeNode("Type:"+t[i].getType()));
			if (!"".equals(t[i].getRemarks()) && t[i].getRemarks()!=null);
				table.add (new DefaultMutableTreeNode("Remarks:"+t[i].getRemarks()));
		}*/
	}

	static void addTables (DefaultMutableTreeNode node, Database db, Catalog cat) {
		System.out.println ("Adding Tables for: "+cat.getName());
		DefaultMutableTreeNode table = null; 
		Table [] t = db.getTables (cat);
		System.out.println("numTables: "+t.length);
		
		// add one tree entry for each table type in Database.
		for (int i=0; i!=db.getTableTypes().length; ++i) {
			table = new DefaultMutableTreeNode (db.getTableTypes()[i]);
			node.add(table);
			addTableTypes (table, db.getTableTypes()[i], t);
		}
		return;
		/*
		for (int i=0; i!=t.length; ++i) {
			table = new DefaultMutableTreeNode (t[i].getName());
		//	System.out.println ("Adding: "+sch.getName()+"."+t[i].getName());
			node.add(table);
			table.add (new DefaultMutableTreeNode("Type:"+t[i].getType()));
			if (!"".equals(t[i].getRemarks()) && t[i].getRemarks()!=null);
				table.add (new DefaultMutableTreeNode("Remarks:"+t[i].getRemarks()));
		}*/
	}
	static void addTableTypes (DefaultMutableTreeNode node, String type, Table [] t) {
		DefaultMutableTreeNode tab = null;
		int count = 0;
		for (int i = 0; i!=t.length; ++i) {
			if (t[i].getType().equals(type)){
				tab = new DefaultMutableTreeNode(t[i].getName());
				node.add(tab);
				addColumns (tab, t[i]);
				++count;
			}
		}
		if (count==0)
			node.removeFromParent();
	}

	static void addColumns (DefaultMutableTreeNode node, Table t) {
		DefaultMutableTreeNode col = null;
		Column [] cols = t.getColumns();
		for (int i=0; i!=cols.length; ++i) {
			col = new DefaultMutableTreeNode (cols[i].getName());
			node.add(col);
			col.add(new DefaultMutableTreeNode(TypesUtil.getTypeName(cols[i].getType())));
			if (cols[i].getRemarks()!=null  && !"".equals(cols[i].getRemarks()))
				col.add(new DefaultMutableTreeNode(cols[i].getRemarks()));
		}
	}

	static void addProcs (DefaultMutableTreeNode node, Database db, Schema sch) {
		DefaultMutableTreeNode proc = new DefaultMutableTreeNode("Procedures");
		node.add (proc);
		node = proc;
		
		Procedure [] procs = db.getProcedures (sch);
		for (int i=0; i!=procs.length; ++i) {
			proc = new DefaultMutableTreeNode(procs[i].getName());
			node.add(proc);
			proc.add (new DefaultMutableTreeNode(new Integer(procs[i].getType())));
			proc.add (new DefaultMutableTreeNode(procs[i].getRemarks()));
		}
	}
	
	public static void main (String [] args) {
		CmdLine cmd = new CmdLine (args);
		driver = cmd.get("-driver");
		url = cmd.get("-url");
		user = cmd.get ("-user");
		password = cmd.get("-password");
		
		if (
			driver == null ||
			url == null ||
			user == null ||
			password == null 
		) {
			usage();	
		}
		
		System.out.println ("Initialzing... Please pray.");
		SQLExecuter exe = new SQLExecuter (driver, url, user, password);
		Database db = new Database (exe);
		System.out.println ("DB - Metadata read... Building GUI..");
		TreeNode node = getDBTreeRoot(db);

		JFrame frame = new JFrame(); 
		
		frame.setContentPane (new JScrollPane(new JTree (node)));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		

	}
}
