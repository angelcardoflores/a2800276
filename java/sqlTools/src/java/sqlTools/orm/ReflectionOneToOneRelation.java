package sqlTools.orm;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import function.sql.SQLExecuter;
import function.sql.SQLFunction;


/**
	caveats: presently only works for classes linked by a single field.
*/
public class ReflectionOneToOneRelation extends OneToOneRelation {

	/**
		TO gets loaded by FROM, FROM is the class that contains TO
	*/
	public ReflectionOneToOneRelation (Field field) {
		super (field);
		from = ReflectionHelperORM.getSampleInstance(getFrom());
		to = ReflectionHelperORM.getSampleInstance(getTo());
	}
	ORMField [] connectingFields;
	ORMObject from;
	ORMObject to;
	

	public ORMField [] getConnectingFields () {
		if (connectingFields != null) {
			return connectingFields;
		}
			
		SQLExecuter exe = from.getSQLExecuter();	
		final ORMField [] ret = new ORMField[1];
		exe.metaData (new SQLFunction () {
			public void apply (DatabaseMetaData meta) throws SQLException {
				ResultSet rset = meta.getImportedKeys (null, null, from.getTableName());
				while (rset.next()){
					String table = rset.getString("PKTABLE_NAME");
					if (!table.equals(to.getTableName())){
						continue;	
					}
					String pk_col = rset.getString("PKCOLUMN_NAME");
					ORMField f0 = to.getField(pk_col);
					if (f0 == null){
						throw new RuntimeException("[ReflectionOneToOneRelation.getConnectingFields] no field for: "+pk_col+" in class "+to.getClass());	
					}

					String fk_col = rset.getString("FKCOLUMN_NAME");
					if (!fk_col.equals(Utils.convertToDBName(getField().getName()))){
						continue;	
					}
					
					ORMField f1 = from.getField(pk_col);
					
					if (ret[0]!=null){
						System.err.println("[Warning] automaticly generated one-to-to relations currently only support linking through a single column.\n Manually implent the relation class if necessary.");
						throw new RuntimeException("[ReflectionOneToOneRelation.getConnectingFields] more than one DB field connecting: "+getTo()+" and:"+getFrom()+" already have: "+(ret[0].getNameInTable())+" now: "+f1.getNameInTable());	
					}
					ret[0]=f1;
				}
				if (ret[0]==null){
					throw new RuntimeException("[ReflectionOneToOneRelation.getConnectingFields] can't load relation between: "+getTo()+" and:"+getFrom());						
				}
				
				
			}				
		});
		connectingFields = ret;	
		return connectingFields;
	}
}
