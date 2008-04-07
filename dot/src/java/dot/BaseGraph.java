package dot;

import java.util.*;

public abstract class BaseGraph extends AttributedElement{
	
	LinkedList nodes;
	LinkedList edges;
	LinkedList subGraphs;
	String id;

	public String getID() {
		return this.id;	
	}

	public List getNodes () {
		return nodes;	
	}

	public List getEdges () {
		return edges;	
	}

	
	public List getSubGraphs () {
		return subGraphs;	
	}

	public void setID (String id) {
		this.id = id;	
	}

	public void addNode (Node node) {
		if (nodes == null) nodes = new LinkedList();
		node.setParent(this);
		nodes.add (node);
	}

	public void addEdge (Edge edge) {
		if (edges == null) edges = new LinkedList();	
		edge.setParent(this);
		edges.add (edge);
	}

	
	public String pack () {
		StringBuffer buf = new StringBuffer();
		buf.append (getGraphType());
		buf.append (" ");
		buf.append (getName());
		buf.append (" {\n");
		if (attributes!=null) pack (attributes, buf);
		if (nodes != null) pack (nodes, buf);
		if (edges != null) pack (edges, buf);
		if (subGraphs != null) pack (subGraphs, buf);
		buf.append ("}\n");
		return buf.toString();
	}

	protected abstract String getGraphType (); // should return "graph" or "digraph"

	public static void pack (List c, StringBuffer buf) {
		for (ElementIterator i = new ElementIterator(c.iterator()); i.hasNext();){
			buf.append ("\t");
			buf.append (i.nextElement().pack());	
		}		
	}

	
		
}
