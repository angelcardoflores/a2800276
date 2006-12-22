package sqlTools;

/**
	Representation of Column
*/

public class Column extends TableObject {
	public Column (String name, Table table) {
		super(name, table);
		table.addColumn(this);
		
	}

	public Column (String name, Table table, String type, String remark) {
		this (name, table, Integer.parseInt(type), remark);
	}

	public Column (String name, Table table, int type, String remark) {
		this (name, table);
		setType(type);
		setRemarks(remark);
	}
	
	/**
		@see DatabaseMetaData#getColumns
	*/
	public Column (String [] args) {
		// change this when we have some central instance to retrieve table data.
		this(args[3], new Table(args[2], new Schema(args[1]), new Catalog(args[0]), "unknown", ""));
		setType (args[5]);
		setRemarks (args[11]);
		
	}
	/************************************************************************
		Fields Definitions
	************************************************************************/


	/** 
		Data type of this column
	*/
	private int type;

	/** 
		Comments
	*/
	private String remarks;

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

	public void setType (String typeIntAsString) {
		this.type = Integer.parseInt(typeIntAsString);	
	}

	/** 
		setter method for <code>remarks</code>
		@see #remarks
	*/
	public void setRemarks (String remarks) {
		this.remarks=remarks;
	}

	public void toString (StringBuffer buf) {
		buf.append (getName());
		buf.append (" -- ");
		buf.append (TypesUtil.getTypeName(getType()));
	}

		
}	
