package sqlTools;

public class ForeignKey extends Index {
	public ForeignKey (String name, Column [] cols, ReferencedKey ref, boolean unique) throws Exception {
		super (name, cols, unique);	
		setReferencedKey(ref);
		ref.addForeignKey(this);
		getTable().addForeign(this);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		key that this foreign key refers to
	*/
	private ReferencedKey referencedKey;

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>referencedKey</code>
		@see #referencedKey
	*/
	public ReferencedKey getReferencedKey () {
		return this.referencedKey;
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>referencedKey</code>
		@see #referencedKey
	*/
	public void setReferencedKey (ReferencedKey referencedKey) {
		this.referencedKey=referencedKey;
	}

	public void toString (StringBuffer buf) {
		super.toString(buf);
		String ref = getReferencedKey()==null?"?":getReferencedKey().getName()+" ("+getReferencedKey().getTable().getName()+")";
		buf.append ("\nFK --> "+ref);	
	}	

}
