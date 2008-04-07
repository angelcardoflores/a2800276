package dot;


/**
	Base class of all dot Elements.
*/
public abstract class BaseElement implements Element {

	private Element parent;


	public void setParent (Element el){
		this.parent = el;	
	}

	public Element getParent () {
		return parent;	
	}


}
