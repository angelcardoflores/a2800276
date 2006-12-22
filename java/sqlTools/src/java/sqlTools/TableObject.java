package sqlTools;

public abstract class TableObject extends DBObject {
	
	public TableObject(){
		// required for inheritance.		
	}	
	
	public TableObject (String name, Table table) {
		super(name);
		setTable(table);
	}
	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		The table this object is a part  of
	*/
	private Table table;

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>table</code>
		@see #table
	*/
	public Table getTable () {
		return this.table;
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>table</code>
		@see #table
	*/
	public void setTable (Table table) {
		this.table=table;
	}


}
