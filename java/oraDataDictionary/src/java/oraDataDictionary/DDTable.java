package oraDataDictionary;
import java.util.*;

public class DDTable {
	
	DDNameComment nameComment; // for table
	LinkedList columns = new LinkedList(); // contains DDNameComments for columns;
	
	DDTable (String name, String comment) {
		this.nameComment = new DDNameComment (name, comment);
	}

	public String getName () {
		return nameComment.getName();
	}

	public String getComments () {
		return nameComment.getComments();	
	}

	void addColumn (String name, String comment) {
		columns.add (new DDNameComment (name, comment));
	}

	public Iterator iterator () {
		return columns.iterator();		
	}
	


	class DDNameComment {
		DDNameComment (String name, String comment) {
			content[0]=name;
			content[1]=comment;
		}
		
		public String getName () {
			return content[0];	
		}

		public String getComments () {
			return content[1];	
		}

		public String [] getArr () {
			return content;	
		}
		String [] content = new String [2];
	}
}
