package html;

import java.util.*;
/**
	Takes a number of Objects and formats them into
	a Table with the specified number of columns. The type
	of formatting can be influenced.

	i.e. the FormattedTable is given an array {
	1,2,3,4,5,6	
	}
	and told to format it left to right, 3 cols wide. The resulting table should look like
	this:

	1 2 3
	4 5 6

	top to bottom:
	
	1 3 5 
	2 4 6

	
*/
public class FormattedTable extends Table {
	
	LinkedList content = new LinkedList();
	int numColumns = 2;
	boolean leftToRight = true;

	public FormattedTable () {
		super();	
	}
	
	public FormattedTable (String [] header) {
		super (header);	
	}

	public void setNumColumns (int i) {
		this.numColumns = i;	
	}

	public void setLeftToRight () {
		this.leftToRight = true;	
	}

	public void setTopToBottom () {
		this.leftToRight = false;	
	}

	public void add (Tag tag) {
	System.out.println ("here");
		content.add (tag);	
	}

	public void add (String str) {
		content.add (str);	
	}

	public void add (Object [] obj) {
		for (int i = 0; i!= obj.length; i++) {
			content.add (obj[i]);	
		}	
		
	}

	public String toString () {
		if (leftToRight)
			return simpleLeftToRight();
		return topToBottom ();
	}

	private String simpleLeftToRight () {
		clear();
		Object [] arr = new Object [numColumns];
		for (int i=0; i<content.size(); i+=numColumns) {
			for (int j=0; j!=numColumns; j++) {
				if ((i+j)<content.size())
					arr[j]=content.get(i+j);	
				else 
					arr[j] = "";
			}
			super.add(arr);
		}
		
		return super.toString();
	}

	private String topToBottom () {
		clear();
		int size = content.size();
		
		/**
			offset b/t each col.
			
			0 3 6 9
			1 4 7
			2 5 8

			e.g. offset for 4-col table with 10 elems
			is 3.
		*/
		int offset = (size / numColumns) ;
		if ((size % numColumns)!=0)
			offset ++;
System.out.println ("size"+size);
System.out.println ("numColumns"+numColumns);
System.out.println ("offset"+offset);
		Object [] obj = new Object [numColumns];
		for (int row=0; row<offset; row++) {
			System.out.print (row+",");
			for (int col = 0; col<numColumns; col++) {
				int positionInContent = row+(col*offset);
				if (positionInContent<content.size())
					obj[col] = content.get(positionInContent);
				else
					obj[col] = "";
			}
			super.add(obj);
		}
		return super.toString();
	}

	public static void main (String [] args) {
		String [] test = {
		 	"eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "zehen"	
		};

		String [] test2 = {
		 	"eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "zehen", "elf"	
		};

		Tag hr = new Tag("HR") {
			public boolean isEmpty(){
				return true;	
			}	
		}; 
		FormattedTable tab = new FormattedTable ();
		tab.add (test);
		HtmlDocument doc = new HtmlDocument ("formattedTable test", "formattedTable.html");
		doc.add (TagMaker.getH(2, "10 elems, 2 cols, left2right"));
		doc.add(tab.toString());
		doc.add (hr);
		doc.add (TagMaker.getH(2, "10 elems, 2 cols, top2bottom"));
		tab.setTopToBottom();
		doc.add (tab.toString());
		doc.add (hr);
		doc.add (TagMaker.getH(2, "10 elems, 3 cols, top2bottom"));
		tab.setNumColumns(3);
		doc.add (tab.toString());

		doc.add (hr);
		doc.add (hr);
		
		tab = new FormattedTable();
		tab.add (test2);
		doc.add (TagMaker.getH(2, "11 elems, 2 cols, left2right"));
		doc.add(tab.toString());
		doc.add (hr);
		doc.add (TagMaker.getH(2, "11 elems, 2 cols, top2bottom"));
		tab.setTopToBottom();
		doc.add (tab.toString());
		doc.add (TagMaker.getH(2, "11 elems, 3 cols, top2bottom"));
		tab.setNumColumns(3);
		doc.add (tab.toString());
		
		try {doc.writeToDisc();} catch (Throwable t){}	
		

	
		
	
		

		
	}
	
		
}
