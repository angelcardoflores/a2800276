package dot;

/**
	All dot elements must implement this interface. Classes
	implementing this interface should extend the abstract class
	BaseElement.

	@see BaseElement
*/
public interface Element {
	/**
		Produces a String containing the description of this
		Element in the dot language.
	*/
	public String pack ();

	/**
		Sets el as the parent of this Element.
	*/
	public void setParent (Element el);

	/**
		Returns the Element that is the parent of this Element.
	*/
	public Element getParent ();
}
