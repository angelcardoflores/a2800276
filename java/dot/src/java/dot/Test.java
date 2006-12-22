package dot;

public class Test {
	
	public static void main (String [] args) {
		Graph g = new Graph();
		String one = "Try this out\\n"+	
				"line2";
		Node n1 = new Node ("one", one);
		n1.addAttribute (ShapeAttribute.EGG);
		Node n2 = new Node ("two", one);
		Edge e1 = new Edge (n1,n2);
		g.addNode(n1);
		g.addNode(n2);
		g.addEdge(e1);
		System.out.println (g.pack());
	}
}
