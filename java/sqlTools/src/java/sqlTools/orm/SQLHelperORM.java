package sqlTools.orm;

public class SQLHelperORM {
	
	static String getSelectStatement (ORMObject orm) {
		StringBuffer buf = new StringBuffer();
		getSelectStart(orm, buf);
		appendWhereClause(orm, buf);
		return buf.toString();
	}

	static String getInsertStatement (ORMObject orm) {
		StringBuffer buf = new StringBuffer();
		buf.append ("INSERT INTO ");
		buf.append (orm.getTableName());
		buf.append (" (");
		appendCommaSeperatedFields(orm, buf);
		buf.append (") VALUES (");
		int num = orm.getORMFields().length;
		for (int i=num; i!=0; --i){
			buf.append (" ? ");
			if (i!=1) buf.append(",");
		}
		buf.append (")");
		return buf.toString();
	}

	static String getCountStatement (Class clazz, Object [] primKeys) {
		StringBuffer buf = new StringBuffer();
		buf.append ("SELECT COUNT(*) FROM ");
		ORMObject orm = ReflectionHelperORM.getSampleInstance(clazz);
		ReflectionHelperORM.setPrimaryKeyValues(orm, primKeys);
		buf.append (orm.getTableName());
		appendWhereClause(orm, buf);
		return buf.toString();
	}

	static String getUpdateStatement (ORMObject orm) {
		StringBuffer buf = new StringBuffer();
		buf.append ("UPDATE ");
		buf.append (orm.getTableName());
		buf.append (" SET ");
		ORMField [] fields = orm.getORMFields();
		for (int i = 0; i!=fields.length; ++i){
			buf.append (fields[i].getNameInTable());
			buf.append (" = ? ");
			if (i!=fields.length-1) buf.append(", ");
		}
		appendWhereClause(orm,buf);
		return buf.toString();

		
	}

	static String getDeleteStatement (ORMObject orm) {
		StringBuffer buf = new StringBuffer();
		buf.append ("DELETE FROM ");
		buf.append (orm.getTableName());
		appendWhereClause(orm,buf);
		return buf.toString();
		
	}


	/**
		append the "select field1, field2, ... fieldn from table-name" 
		for loading class ORMObject to the StringBuffer.
	*/
	static void getSelectStart (ORMObject orm, StringBuffer buf) {
		buf.append ("SELECT ");
		appendCommaSeperatedFields(orm, buf);
		buf.append(" FROM ");
		buf.append(orm.getTableName());

	}

	private static void appendCommaSeperatedFields (ORMObject orm, StringBuffer buf) {
		ORMField [] fields = orm.getORMFields();
		for (int i=0; i!=fields.length; ++i) {
			ORMField f = fields[i];
			buf.append (f.getNameInTable());		
			if (i!=fields.length-1) { buf.append(", "); }
		}

	}

	private static void appendWhereClause (ORMObject orm, StringBuffer buf) {
		buf.append(" WHERE ");
		ORMField [] fields = orm.getPrimaryKeys();
		for (int i=fields.length; i!=0; --i) {
			ORMField f = fields[i-1];
			

			buf.append (f.getNameInTable());
			buf.append (" = ");
			buf.append ("'");
			if (f.getValue(orm) == null){
				throw new RuntimeException ("[ORMObject.getSelectStatement] primary key field: "+f.getName()+" must have a value.");	
			}
			buf.append (f.getValue(orm));		
			buf.append ("'");
			if (i!=1) { buf.append(" AND "); }
		}
			
	}


}
