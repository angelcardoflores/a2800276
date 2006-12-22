package html;

public class Frameset extends Tag {

	Tag nfTag = new Tag ("noframes") {
		public int numIndent () {
			return super.numIndent()+1;	
		}	
	};
	String proportions = "";
	boolean vertical = true;

	public Frameset () {
		super ("frameset");	
	}

	public void addNoFrames (Tag tag) {
		nfTag.add (tag);
	}

	public void addNoFrames (String text) {
		nfTag.add (text);
	}

	/**
		add the rows/cols = "225,57,*" proportions, without the quotes.
	*/
	public void setProportions (String prop) {
		this.proportions = prop;	
	}

	public void setVertical () {
		this.vertical = true;		
	}

	public void setHorizontal () {
		this.vertical = false;	
	}

	protected String  getProportions () {
		return this.proportions;	
	}

	

	public String toString () {
		String attName = vertical?"cols":"rows"; 
		add (new Attribute(attName, proportions));
		add (nfTag);
		return super.toString();
	}
}
