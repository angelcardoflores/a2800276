package html;

public class Text extends Tag {
	
	private String t;
	public Text (String text) {
		super (text);	
		this.t=text;
	}
	
	public java.lang.String toString(){
		return t;	
	}
	public boolean isAllowed(html.Attribute a) {
		return false;	
	}
	public java.lang.String getName() {
//		return "#PCDATA";	
		return t;
	}
	public void add(html.Attribute a){}
	public void add(java.lang.String s) {}
	public void add(html.Tag t){}
	
}
