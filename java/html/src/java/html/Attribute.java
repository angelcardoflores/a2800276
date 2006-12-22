package html;

public class Attribute {
	String name;
	String value;
	public Attribute (String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Attribute (String name, int value) {
		this (name, Integer.toString(value));	
	}
	
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append(name);
		buf.append("=\"");
		buf.append(value);
		buf.append("\"");
		return buf.toString();
		
	}

}

