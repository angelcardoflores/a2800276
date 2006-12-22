package sqlTools.dot;

import dot.*;
import function.*;
import sqlTools.*;
import java.util.*;

public class DotDB {
	
	
	public static Graph getGraph (ContainerObject container) {
		final Graph g = new Graph ();
		g.setName(container.getName());
		final LinkedList fks = new LinkedList();
		final LinkedList rks = new LinkedList();
	
		container.eachTable (new Function (){
			public void apply (Object obj){
				Table t = ((Table)obj);	
				g.addNode (getTableNode(t));
				Index [] idx = t.getReferencedKeys();
				transfer (idx, rks);
				idx = t.getForeignKeys();
				transfer (idx, fks);
			}	
		});

		handleKeys(g, fks, rks);
		// each container
			//each table
			//each reference
		return g;
	}

	public static void handleKeys (Graph g, LinkedList fks, LinkedList rks) {
		Set fk = new HashSet();
		fk.addAll(fks);
		Set rk = new HashSet();
		rk.addAll(rks);
		
		for (Iterator it = rk.iterator(); it.hasNext();) {
			handleIndex (g, (ReferencedKey)it.next());
		}
		for (Iterator it = fk.iterator(); it.hasNext();) {
			ForeignKey idx = (ForeignKey)it.next();
			handleIndex (g, idx);
			handleFK (g, idx);
		}
		
			
	}

	private static void handleIndex (final Graph g, final Index idx) {
		g.addNode(new Node(idx.getName(), idx.getName()));
		idx.eachColumn (new Function (){
			public void apply (Object obj) {
				Column c = (Column)obj;
				g.addEdge (new Edge (idx.getName(), getExternalColumnId(c)));	
			}	
		});
	}

	static void handleFK (Graph g, ForeignKey fk) {
		g.addEdge (new Edge(fk.getName(), fk.getReferencedKey().getName()));	
	}

	private static void transfer (Index [] idx, LinkedList list) {
		if (idx==null)
			return;
		for (int i=0; i!=idx.length; ++i) {
			list.add (idx[i]);	
		}	
	}

	public static Graph getGraph (Table t) {
		Graph g = new Graph ();
		g.setName(t.getName());
		g.addNode(getTableNode(t));
		return g;
	}

	public static Record getTableNode (Table t) {
		final Record node = new Record(getTableId(t), Record.VERTICAL);
		node.addCell (getTableLabelCell(t));
		t.eachColumn(new Function () {
			public void apply (Object obj){
				node.addCell (getColumnLabelCell((Column)obj));
			}
		});
		return node;
	}
	
	static String getExternalColumnId (Column c) {
		return getTableId (c.getTable())+":"+getColumnId(c);	
	}
	
	static String getTableId (Table t) {
		return "T"+t.getName();		
	}
	
	public static RecordCell getTableLabelCell (Table t) {
		return new RecordCell(t.getName()+"("+t.getType()+")");
	}

	public static RecordCell getColumnLabelCell (Column col) {
		return new RecordCell(getColumnId(col), col.getName()+", "+TypesUtil.getTypeName(col.getType()));
	}

	public static String getColumnId (Column col) {
		return col.getTable().getName()+"_"+col.getName();	
	}


}
