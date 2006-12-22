package sqlTools;

import java.util.*;

/**
	Representation of Procedure
*/

public class Procedure extends SchemaObject {
	public Procedure (String name, Schema schema, Catalog cat, String remarks, int type) {
		super(name, schema, cat);
		setType(type);
		setRemarks(remarks);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		type of procedure, see DatabaseMetaData.getProcedures
	*/
	private int type;

	/** 
		comments
	*/
	private String remarks;
	
	LinkedList columns = new LinkedList();

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>type</code>
		@see #type
	*/
	public int getType () {
		return this.type;
	}

	/** 
		getter method for <code>remarks</code>
		@see #remarks
	*/
	public String getRemarks () {
		return this.remarks;
	}

	
	public ProcColumn [] getColumns () {
		return (ProcColumn [])columns.toArray(new ProcColumn[0]);	
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>type</code>
		@see #type
	*/
	public void setType (int type) {
		this.type=type;
	}

	/** 
		setter method for <code>remarks</code>
		@see #remarks
	*/
	public void setRemarks (String remarks) {
		this.remarks=remarks;
	}
	
	public void addColumn (ProcColumn col) {
		columns.add (col);
	}

	public void toString (StringBuffer buf) {
		buf.append (getName());
		ProcColumn [] cols = getColumns();
		for (int i=0; i!=cols.length; ++i) {
			cols[i].toString(buf);	
		}
		buf.append ("\n");
	}
}	
