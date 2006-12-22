package html;

public class ATag extends Tag {
	boolean targetSet;
	
	public ATag () {
		super ("a");	
	}

	public ATag (String content) {
		this ();
		add (content);
	}

	public ATag (Tag content) {
		this();
		add (content);
	}

	public ATag (String content, String url) {
		this (content);
		setHref (url);
	}

	public void setHref (String url) {
		add(new Attribute("href", url));	
	}

	public void setAnchor (String anchor) {
		add (new Attribute("name", anchor));	
	}

	public void setTarget (String target) {
		targetSet = true;
		add (new Attribute("target", target));	
	}

	public String toString () {
		if (!targetSet) {
			setTarget ("_self");
		}
		return super.toString();
	}
}
