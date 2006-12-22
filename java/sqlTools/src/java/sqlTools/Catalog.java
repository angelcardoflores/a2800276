package sqlTools;

/**
	Representation of Catalog
*/

public class Catalog extends ContainerObject {
	
	public Catalog (String name) {
		super(name);
	}

	public boolean equals (Object obj) {
		if (!(obj instanceof Catalog))
			return false;
		return this.getName().equals( ((Catalog)obj).getName() );
	}
	
	public void toString (StringBuffer buf) {
		buf.append ("Catalog: ");
		super.toString (buf);
	}
}	
