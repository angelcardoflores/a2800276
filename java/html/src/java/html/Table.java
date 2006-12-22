package html;

public class Table extends Tag {
	
	public Table () {
		super ("table");	
	}

	public Table (String [] header) {
		this();
		Tag heading = new Tag ("tr");
		for (int i=0; i!=header.length; i++) {
			heading.add(new Tag("th", header[i]));
		}
		add (heading);
	}

	public void setBorder (int i) {
		add (new Attribute ("border", i));	
	}

	public void add(Object [][] content) {
		for (int i=0; i!= content.length; i++) {
			add(content[i]);
		}
		
			
	}

	public void add(Object [] content) {
		Tag row = new Tag ("tr");
		Tag data = null;
		for (int j=0; j!=content.length; j++) {
			data = new Tag ("td") {
				public boolean breaks (){
					return false;	
				}	
				public int numIndent () {
					return super.numIndent()+1;	
				}
			};
			if (content[j] instanceof Tag)
				data.add ((Tag)content[j]);
			else if (content [j] == null)
				data.add ("");
			else
				data.add (content[j].toString());
			row.add(data);	
		}
		super.add(row);	

	}

	public void setWidth (int width) {
		add (new Attribute ("width", width));
	}

	public void setHeight (int height) {
		add (new Attribute ("height", height));	
	}

	public void setCellpadding (int padding) {
		add (new Attribute ("cellpadding",padding));		
	}

	public void setCellspacing (int spacing) {
		add (new Attribute ("cellspacing", spacing));	
	}
	
	public static void main (String [] args) {
		Tag tag = new Tag ("html");
		tag.add (new Tag ("head"));
		Tag body = new Tag ("body");
		tag.add (body);
		body.add(new Attribute("test", 123));
		body.add ("This is a test");
		String [][] arr = {
			{"eins", "zwei", "drei"},	
			{"eins", "zwei", "drei"},	
			{"eins", "zwei", "drei"}	
		};
		Table table = new Table();
		table.add(arr);
		body.add (table);
		System.out.println (tag);
		
	}
}
