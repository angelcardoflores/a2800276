package dot;

import java.util.*;
public class RecordCell {

	/**
		This constructor is just a placeholder that
		contains subcells.
	*/
	public RecordCell () {
	}
	
	public RecordCell (String name) {
		this();
		setName(name);
	}

	public RecordCell (String name, String label) {
		this(name);	
		setLabel(label);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		The name (and therefore id) of this record
	*/
	private String name;

	/** 
		Text of this field in case the text should be diffrent from the id.
	*/
	private String label;

	/** 
		Cells that make up this record cell (if applicable)
	*/
	private LinkedList subCells;

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>name</code>
		@see #name
	*/
	public String getName () {
		return this.name;
	}

	/** 
		getter method for <code>label</code>
		@see #label
	*/
	public String getLabel () {
		return this.label;
	}

	/** 
		getter method for <code>subCells</code>
		@see #subCells
	*/
	public LinkedList getSubCells () {
		return this.subCells;
	}
	public RecordCell getSubCell (int i) {
		return (RecordCell)getSubCells().get(i);	
	}

	

	public boolean hasSubCells () {
		return getSubCells()!=null;	
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>name</code>
		@see #name
	*/
	public void setName (String name) {
		this.name=name;
	}

	/** 
		setter method for <code>label</code>
		@see #label
	*/
	public void setLabel (String label) {
		this.label=label;
	}

	/** 
		setter method for <code>subCells</code>
		@see #subCells
	*/
	public void addSubCell (RecordCell subCell) {
		if (getSubCells()==null)
			this.subCells=new LinkedList();
		getSubCells().add(subCell);
	}
	
	public String pack () {
		StringBuffer buf = new StringBuffer();
		if (hasSubCells()){
			buf.append ("{");			
			buf.append (getSubCell(0).pack());
			
			for (int i=1; i<getSubCells().size();++i){
				buf.append("|");
				buf.append(getSubCell(i).pack());
			}

			buf.append ("}");			
		} else {
			if (getLabel()!=null) {
				buf.append ("<"+getName()+"> ");
				buf.append (getLabel());
			} else {
				buf.append (" "+getName()+" ");	
			}
			
		}
		return buf.toString();
		
	}
	


}


