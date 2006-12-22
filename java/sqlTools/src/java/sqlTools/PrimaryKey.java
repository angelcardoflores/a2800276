package sqlTools;

public class PrimaryKey extends ReferencedKey {
	public PrimaryKey (String name, Column [] cols) throws Exception{
		super (name, cols);	
		getTable().setPrimaryKey(this);
	}
}
