package sqlTools;

/**
	Representation of ProcColumn
*/

public class ProcColumn extends DBObject {
	public ProcColumn (String name) {
		super(name);
	}

	public void toString (StringBuffer buf) {
		buf.append(getName());
	}
}	
