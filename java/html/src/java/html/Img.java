package html;

public class Img extends Tag {
	
	public Img (String src, String altText) {
		super ("img");
		add (new Attribute ("src", src));
		add (new Attribute ("alt", altText));
	}
	
	public Img (String src) {
		this (src, "no alt text");	
	}

	public void setHeight (int i) {
		add (new Attribute ("height", Integer.toString(i)));
	}
	
	public void setWidth (int i) {
		add (new Attribute ("width", Integer.toString(i)));
	}
}
