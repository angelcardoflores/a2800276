package html;

public class Frame extends Tag {
	
	public Frame (String src, String name) {
		super ("frame");
		add (new Attribute("src", src));
		add (new Attribute("name", name));
	}

	public boolean isEmpty () {
		return true;	
	}

	public int numIndent() {
		return super.numIndent()+1;	
	}
}
