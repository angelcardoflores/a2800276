package sqlTools;

import java.util.*;
import function.*;

/**
	Key that may be referenced by Foreign keys.
*/
public class ReferencedKey extends Index {
	public ReferencedKey (String name, Column [] cols) throws Exception {
		super (name, cols, true);	
		getTable().addReferencedKey(this);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		keys this key is referenced by
	*/
	private LinkedList foreignKeys = new LinkedList();

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>foreignKeys</code>
		@see #foreignKeys
	*/
	public ForeignKey[] getForeignKeys () {
		return (ForeignKey [])this.foreignKeys.toArray(new ForeignKey[0]);
	}

	public String eachForeignKey (Function func) {
		try {
			for (Iterator it = foreignKeys.iterator(); it.hasNext();) {
				func.apply (it.next());	
			}
		} catch (Throwable t) {
			t.printStackTrace();
			return t.getMessage();
		}
		return null;
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>foreignKeys</code>
		@see #foreignKeys
	*/
	public void addForeignKey (ForeignKey fk) {
		this.foreignKeys.add(fk);
	}

	public void toString (final StringBuffer buf) {
		super.toString(buf);
		if (foreignKeys.size()>0) buf.append ("\nreferenced by:\n");
		eachForeignKey (new SafeFunction () {
			public void apply(Object obj){
				buf.append ("\n\t");
				buf.append(((ForeignKey)obj).getName());
				buf.append("(");
				buf.append(((ForeignKey)obj).getTable().getName());
				buf.append(")");
			}	
		});
		
	}


}
