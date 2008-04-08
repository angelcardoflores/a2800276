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

public class StyleAttribute extends Attribute implements NodeAttribute, EdgeAttribute, GraphAttribute {
	public static final StyleAttribute SOLID = new StyleAttribute ("solid");
	public static final StyleAttribute DASHED = new StyleAttribute ("dashed");
	public static final StyleAttribute DOTTED = new StyleAttribute ("dotted");
	public static final StyleAttribute BOLD = new StyleAttribute ("bold");
	public static final StyleAttribute INVIS = new StyleAttribute ("invis");

	public static final StyleAttribute FILLED = new StyleAttribute ("filled");
	public static final StyleAttribute DIAGONALS = new StyleAttribute ("diagonals");
	public static final StyleAttribute ROUNDED = new StyleAttribute ("rounded");

	private StyleAttribute (String value) {
		super ("style", value);
	}

	/**
		use for user defined styles
	*/
	public static StyleAttribute getStyleAttribute (String value) {
		return new StyleAttribute ('"'+value+'"');	
	}
}
