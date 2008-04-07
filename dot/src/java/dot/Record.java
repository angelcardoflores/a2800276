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
