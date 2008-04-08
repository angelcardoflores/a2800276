// Copyright (c) 2008 Tim Becker (tim.becker@gmx.net)
// 
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without
// restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following
// conditions:
// 
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

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
