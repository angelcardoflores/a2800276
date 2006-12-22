package sqlTools;

import function.*;

/**
	Representation of Index
*/

public class Index extends TableObject {
	
//	public Index();
	public Index (String name,  Column [] cols, boolean unique) throws Exception {
		setName(name);
		setColumns(cols);
		setTable(cols[0].getTable()); // setColumns checks that all cols are of same table.
		setUnique(unique);
		getTable().addIndex (this);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		The columns this Index comprises
	*/
	private Column[] columns;

	/** 
		whether this is a unique constraint
	*/
	private boolean unique;


	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>Columns</code>
		@see #Columns
	*/
	public Column[] getColumns () {
		return this.columns;
	}

	
	/** 
		getter method for <code>unique</code>
		@see #unique
	*/
	public boolean isUnique () {
		return this.unique;
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>Columns</code>
		@see #Columns
	*/
	public void setColumns (Column[] columns) throws Exception {
		checkColumns (columns);
		this.columns=columns;
	}
	
	/** 
		setter method for <code>unique</code>
		@see #unique
	*/
	public void setUnique (boolean unique) {
		this.unique=unique;
	}


	private void checkColumns (Column [] columns) throws Exception {
		if (columns.length == 0)
			throw new Exception ("Index must reference at least one column!");
		Table tab = columns[0].getTable();
		for (int i = 1; i!=columns.length; ++i) {
			if (columns[i].getTable()!=tab)
				throw new Exception ("All index columns must be part of same table.");
		};	
	}

	public void eachColumn (Function func) {
		try {
			for (int i=0; i!=columns.length; ++i) {
				func.apply (columns[i]);
			}	
		} catch (Throwable t) {
			t.printStackTrace();	
		}
	}

	public void toString (StringBuffer buf) {
		buf.append ("IDX: "+getName());
		if (isUnique()) buf.append (" unique");
		for (int i=0; i!=getColumns().length; ++i) {
			buf.append("\n");
			getColumns()[i].toString(buf);
		}
	}

	

}	
