package sqlTools;
import sqlTools.collection.FuncHashMap;
import function.Function;
import function.SafeFunction;
/**
	Representation of Table
*/

public class Table extends SchemaObject {

	
	public Table (String name, Schema schema, Catalog cat, String type, String remarks) {
		super(name, schema, cat);
		setType(type);
		setRemarks(remarks);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		The type of table (e.g. TABLE, VIEW, ALIAS), see DatabaseMetaData.getTables
	*/
	private String type;

	/** 
		explanatory comments in DB
	*/
	private String remarks;

	FuncHashMap columns = new FuncHashMap();
	FuncHashMap indices = new FuncHashMap();
	FuncHashMap referenced = new FuncHashMap();
	FuncHashMap foreign = new FuncHashMap();

	PrimaryKey pk;

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>type</code>
		@see #type
	*/
	public String getType () {
		return this.type;
	}

	/** 
		getter method for <code>remarks</code>
		@see #remarks
	*/
	public String getRemarks () {
		return this.remarks;
	}

	public Column [] getColumns () {
		return (Column [])columns.values().toArray(new Column[0]);	
	}

	public void eachColumn (Function fun) {
		columns.eachValue(fun);
	}

	public Column getColumn (String name) {
		return (Column)columns.get(name);	
	}

	public Index [] getIndices () {
		return (Index[])indices.toArray();
	}

	public ReferencedKey [] getReferencedKeys () {
		return (ReferencedKey[])referenced.toArray();	
	}

	public ReferencedKey getReferencedKey (String name) {
//	System.err.println("grk - "+name+""+referenced.get(name));
		return (ReferencedKey)referenced.get(name);	
	}
	public ForeignKey [] getForeignKeys () {
		return (ForeignKey[])foreign.toArray();	
	}

	public PrimaryKey getPrimaryKey () {
		return this.pk;	
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>type</code>
		@see #type
	*/
	public void setType (String type) {
		this.type=type;
	}

	/** 
		setter method for <code>remarks</code>
		@see #remarks
	*/
	public void setRemarks (String remarks) {
		this.remarks=remarks;
	}

	public void addColumn (Column col) {
		columns.put (col.getName(),col);
	}

	public void addIndex (Index idx) {
//	System.err.println("!!!!Adding: "+idx);
//		try {
//			throw new Exception();
//		}catch (Exception e) {
//			e.printStackTrace();	
//		}
		indices.put (idx.getName(),idx);	
	}


	public void addReferencedKey (ReferencedKey rk) {
//	System.err.println(getName()+"adding"+rk.getName());
		referenced.put(rk.getName(), rk);	
	}
	
	public void addForeign (ForeignKey fk) {
		foreign.put(fk.getName(), fk);	
	}

	public void setPrimaryKey (PrimaryKey pk) {
		this.pk=pk;	
	}

	public void toString (final StringBuffer buf) {
		buf.append ("\nTable("+getType()+") : "+getName());
		Column [] cols = getColumns();
		for (int i=0; i!=cols.length; ++i) {
			buf.append("\n");
			cols[i].toString(buf);	
		}
		
		if (getPrimaryKey()!=null){
			buf.append ("\n");
			buf.append ("Primary Key: ");
			getPrimaryKey().toString(buf);
		}
			
		toStringHash ("Indices", indices, buf);
		toStringHash ("ReferencedKeys", referenced, buf);
		toStringHash ("ForeignKeys", foreign, buf);
		
		
		buf.append ("\n\n");
		
	}

	private void toStringHash (String str, FuncHashMap map, final StringBuffer buf) {
		if (map.size()>0) buf.append ("\n\n"+str+" ("+map.size()+"): ");

		map.eachValue (new SafeFunction () {
			public void apply (Object obj) {
				buf.append ("\n");
				((DBObject)obj).toString(buf);
			}
		});

	}


}	
