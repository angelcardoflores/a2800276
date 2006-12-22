package sqlTools;

/**
*/

public abstract class SchemaObject extends DBObject {
	private Schema schema;
	private Catalog catalog;

	public SchemaObject (String name, Schema schema, Catalog catalog) {
		super(name);
		setSchema (schema);
		setCatalog (catalog);
		
	}

	public Schema getSchema() {
		return this.schema;	
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Catalog getCatalog() {
		return this.catalog;	
	}

	public void setCatalog(Catalog schema) {
		this.catalog = schema;
	}

}	
