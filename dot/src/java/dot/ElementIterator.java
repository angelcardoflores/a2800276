package dot;
import java.util.*;

class ElementIterator implements Iterator {
	Iterator it;
	
	ElementIterator (Iterator it)	{
		this.it = it;	
	}
	public boolean hasNext () {
		return it.hasNext();	
	}
	public Object next () {
		return it.next();	
	}
	public Element nextElement () {
		return (Element)(it.next());	
	}
	public void remove() {
		it.remove();	
	}
}	

