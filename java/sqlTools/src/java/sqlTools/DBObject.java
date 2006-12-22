package sqlTools;

/**
	Base class for DB Representation.
*/

public abstract class DBObject  {
	private String name;
	public DBObject () {
		// req for inheritance	
	}
	public DBObject (String name) {
		
		this.name = name==null?"":name;
	}

	public String getName () {
		return name;	
	}

	public void setName (String name){
		this.name = name;	
	}

	public String toString () {
		StringBuffer buf = new StringBuffer();
		toString(buf);
		return buf.toString();
	}

	public abstract void toString (StringBuffer buf);
}	
