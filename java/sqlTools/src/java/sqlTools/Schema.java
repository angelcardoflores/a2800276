package sqlTools;

/**
	Representation of Schema
*/

public class Schema extends ContainerObject {
	
	public Schema (String name) {
		super(name);
	}

	public boolean equals (Object obj) {
		if (!(obj instanceof Schema))
			return false;
		return this.getName().equals( ((Catalog)obj).getName() );
	}
	public void toString (StringBuffer buf) {
		buf.append ("Schema: ");
		super.toString (buf);
	}
	
}	
