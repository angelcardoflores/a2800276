package dot;
import java.util.*;
public abstract class AttributedElement extends BaseElement {

	LinkedList attributes;
	String name;
	
	public void setName (String name){
		this.name = name;	
	}	
	public String getName () {
		return name;	
	}

	public void setLabel (String label) {
		addAttribute (new Attribute("label", label, true));	
	}


	public void addAttribute (Attribute attr) {
		if (attributes == null) attributes = new LinkedList();
		attr.setParent(this);
		attributes.add(attr);
	}
	
	public List getAttributes () {
		return attributes;	
	}

}
