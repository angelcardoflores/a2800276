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

public class Record extends Node {
	
	public static short VERTICAL = 0;
	public static short HORIZONTAL = 1;
	
	private LinkedList cells;
	private short orientation;
	
	public Record (String name) {
		this(name, HORIZONTAL);	
	}

	public Record (String name, short orientation) {
		super(name);
		addAttribute(ShapeAttribute.RECORD);
		setOrientation(orientation);	
	}

	public void addCell (RecordCell cell) {
		if (cells==null)
			cells=new LinkedList();
		cells.add(cell);
	}

	public RecordCell getCell (int i){
		return (RecordCell)cells.get(i);
	}

	public void setOrientation (short orientation){
		this.orientation = orientation;	
	}

	public short getOrientation (){
		return this.orientation;	
	}

	public String pack () {
		
		if (cells!=null) {
			StringBuffer buf = new StringBuffer();
			if (getOrientation()==VERTICAL)
				buf.append("{");

			buf.append(getCell(0).pack());
			for (int i=1; i!=this.cells.size(); ++i){
				buf.append("|");
				buf.append(getCell(i).pack());
			}
			
			
			if (getOrientation()==VERTICAL)
				buf.append("}");
				
			setLabel(buf.toString());
		}	
		return (super.pack());


	}
}
