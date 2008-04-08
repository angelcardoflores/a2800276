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

public class ShapeAttribute extends Attribute implements NodeAttribute {
	
 	 	 	
	public static final ShapeAttribute BOX = new ShapeAttribute ("box");
	public static final ShapeAttribute POLYGON = new ShapeAttribute ("polygon");
	public static final ShapeAttribute ELLIPSE = new ShapeAttribute ("ellipse");
	public static final ShapeAttribute CIRCLE = new ShapeAttribute ("circle");
	public static final ShapeAttribute POINT = new ShapeAttribute ("point");
	public static final ShapeAttribute EGG = new ShapeAttribute ("egg");
	public static final ShapeAttribute TRIANGLE = new ShapeAttribute ("triangle");
	public static final ShapeAttribute PLAINTEXT = new ShapeAttribute ("plaintext");
	public static final ShapeAttribute DIAMOND = new ShapeAttribute ("diamond");
	public static final ShapeAttribute TRAPEZIUM = new ShapeAttribute ("trapezium");
	public static final ShapeAttribute PARALLELOGRAM = new ShapeAttribute ("parallelogram");
	public static final ShapeAttribute HOUSE = new ShapeAttribute ("house");
	public static final ShapeAttribute PENTAGON = new ShapeAttribute ("pentagon");
	public static final ShapeAttribute HEXAGON = new ShapeAttribute ("hexagon");
	public static final ShapeAttribute SEPTAGON = new ShapeAttribute ("septagon");
	public static final ShapeAttribute OCTAGON = new ShapeAttribute ("octagon");
	public static final ShapeAttribute DOUBLECIRCLE = new ShapeAttribute ("doublecircle");
	public static final ShapeAttribute DOUBLEOCTAGON = new ShapeAttribute ("doubleoctagon");
	public static final ShapeAttribute TRIPLEOCTAGON = new ShapeAttribute ("tripleoctagon");
	public static final ShapeAttribute INVTRIANGLE = new ShapeAttribute ("invtriangle");
	public static final ShapeAttribute INVTRAPEZIUM = new ShapeAttribute ("invtrapezium");
	public static final ShapeAttribute INVHOUSE = new ShapeAttribute ("invhouse");
	public static final ShapeAttribute MDIAMOND = new ShapeAttribute ("Mdiamond");
	public static final ShapeAttribute MSQUARE = new ShapeAttribute ("Msquare");
	public static final ShapeAttribute MCIRCLE = new ShapeAttribute ("Mcircle");
	public static final ShapeAttribute RECT = new ShapeAttribute ("rect");
	public static final ShapeAttribute RECTANGLE = new ShapeAttribute ("rectangle");
	public static final ShapeAttribute RECORD = new ShapeAttribute ("record");


	
	private ShapeAttribute (String value) {
		super ("shape", value);	
	}
}
