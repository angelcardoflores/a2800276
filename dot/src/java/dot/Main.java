
package dot;

/**
	Test class for dot package.
*/
public class Main {
	
	public static void main (String [] args) {
		Graph g = new Graph();
		Node a = new Node ("nodea", "This is one hell of a Thing");
		a.addAttribute (ShapeAttribute.DOUBLEOCTAGON);
		Node b = new Node ("nodeb");
		Node c = new Node ("noded");
		Edge e1 = new Edge (a, b);
		Edge e2 = new Edge (a, c);
		e2.addAttribute (StyleAttribute.DOTTED);
		Edge e3 = new Edge (c, c);
		g.addNode (a);
		g.addNode (b);
		g.addNode (c);
		g.addEdge (e1);
		g.addEdge (e2);
		g.addEdge (e3);

		System.out.println (g.pack());
	}
}
