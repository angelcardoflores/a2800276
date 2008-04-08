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
