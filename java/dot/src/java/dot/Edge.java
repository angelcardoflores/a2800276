package dot;

import java.util.*;

/**
	This class represents an edge element in dot. An Edge connects
	two nodes.

	@see Node
*/
public class Edge extends AttributedElement {

	LinkedList nodes;

	/**
		Construct an Edge connecting two Nodes.
	*/
	public Edge (Node n1, Node n2) {
		nodes = new LinkedList ();
		nodes.add(n1);
		nodes.add(n2);
	}

	/**
		Shortcut to construct an Edge connecting two nodes that
		haven't been constructed yet.
	*/
	public Edge (String str1, String str2) {
		this (new Node(str1), new Node(str2));
	}

	/**
		Adds further nodes. The nodes will be connected to the last
		node connected by this edge.
	*/
	public void addNode(Node n) {
		nodes.add(n);
	}

	public String pack () {
		StringBuffer buf = new StringBuffer();
		String edgeOp = (getParent() instanceof Graph) ? " -- " : " -> ";

		for (ElementIterator i = new ElementIterator (nodes.iterator()); i.hasNext();){
			Node n = (Node)i.nextElement();

			buf.append("\""+n.getName()+"\"");
			if (i.hasNext()){
				buf.append (edgeOp);
			}
			else {
				if (getAttributes()!=null) {
					buf.append (" [");
					for (ElementIterator j = new ElementIterator(getAttributes().iterator());j.hasNext();){
						buf.append (j.nextElement().pack());
						if (j.hasNext())
							buf.append (',');
					}
					buf.append("]");
				} // if getAttributes

			} // else
		} // for
		buf.append(";\n");
		return buf.toString();
	}


}
