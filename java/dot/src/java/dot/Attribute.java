package dot;

public class Attribute extends BaseElement {
	
	private String name;
	private String value;
	
	public Attribute (String name, String value) {
		this (name, value, false);
	}

	public Attribute (String name, String value, boolean quoteValue) {
	
		this.name = name;
		this.value = value;
		if (quoteValue)
			this.value = '"'+value+'"';
	}

	public String pack () {
		StringBuffer buf = new StringBuffer();
		buf.append (name);
		buf.append (" = ");
		buf.append (value);
		return buf.toString();
	}
	
}
