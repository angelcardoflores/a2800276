
package sqlTools.orm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import function.sql.SQLExecuter;
import function.sql.SQLFunction;

/**
	<p>The class ORMObject provides the basic functionality of this
	object-relational mapping library. Using this class you would be
	able to map the following database table:</p>
	<code><pre>
	create table test {
		name varchar(10) primary key,
	        number number (1)
	}
	</pre></code>

	<p>with only the following code:</p>

	<code><pre>
	public class Test extends ORMObject {
		public String name;
		public int number;
		
		public String getTableName (){
			return "test";	
		}
		protected ORMField [] getORMFields (){
			return {
				new ORMField(this.getClass().getDeclaredField("name"), "name"),	
				new ORMField(this.getClass().getDeclaredField("number"), "number")	
			};		
		}
		protected abstract ORMField [] getPrimaryKeys() {
			return {
				new ORMField(this.getClass().getDeclaredField("name"),"name")
			};	
		}
		protected abstract OneToOneRelation [] getOneToOneRelations (){
			return new OneToOneRelation[0];	
		}
		protected SQLExecuter getSQLExecuter(){...}
		protected ORMField getField (String key) {...}

				
	}
	</pre></code>

	<p>
	Since this is nearly as tedious as actualy writing the ORM
	mapping by hand, <code>ORMObject</code> is usually not used as
	the base for your own object-relational classes. Instead @see
	ReflectionORMObject provides facilities which implement the
	abstract methods in <code>ORMObject</code> using
	<code>DatabaseMetaData</code> provided in the JDBC API.
	</p>
	<p>
	In the easiest case, the above table would be mapped as follows:
	</p>
	
	<code><pre>
	public class Test extends ReflectionORMObject {
		public String name;
		public int number;
	}
	</pre></code>

	<p>This enables you to load rows from the database using the
	following java code: <code>Test test = Test.load(Test.class,
	"name_to_load");</code> or you could do this: <code>Test
	test=new Test(); test.name="hallo";test.number=2;test.save();</code></p>

	<h2>Naming conventions</h2>
	<p>The abstract implementation in <code>ORMObject</code> does
	not take into account any type of naming conventions. You can
	map any type of database table to an object. You would, of
	course, not be able to enjoy the benefits of having the
	<code>orm</code> library generating all code for you. The naming
	conventions of <code>ReflectionORMObject</code> are discussed
	further in that classes Javadoc, but basically, it boils down
	to: 
	<ul>
		<li>database names are either all UPPER_CASE or
		lower_case.</li>
		<li>database names use underscores (_) to signify
		spaces.</li>
		<li>Java names are CamelCase.</li>
		<li>Java class names begin with a CaptialLetter.</li>
		<li>Java field and method names begin with a
		lowercaseLetter.</li>
		<li>database tables are mapped to java objects named
		identically apart from case conventions (E.g. table
		"all_")</li>
		
	</ul></p>
	
	<h2>Type mapping</h2>
	<p>The following table defines how Java-Types are matched to
	database types and vice versa.</p>
	<table border =1>
		<tr>
			<th>Database type (@see java.sql.Types)</th>
			<th>Java type</th>
		</tr>
		<tr>
			<td colspan=2 bgcolor=lightgrey> "Advanced" Types</td>
		</tr>
			<tr><td>ARRAY</td><td>java.sql.Array</td></tr>
			<tr><td>BLOB</td><td>java.sql.Blob</td></tr>
			<tr><td>CLOB</td><td>java.sql.Clob</td></tr>
			<tr><td>STRUCT</td><td>java.sql.Struct</td></tr>
			<tr><td>REF</td><td>java.sql.Ref</td></tr>
			<tr><td>JAVA_OBJECT</td><td>java.lang.Object</td></tr>

		<tr>	<td colspan=2 bgcolor=lightgrey>Date related</td></tr>
			<tr><td>DATE</td><td>java.sql.Date</td></tr>
			<tr><td>TIME</td><td>java.sql.Time</td></tr>
			<tr><td>TIMESTAMP</td><td>java.sql.Timestamp</td></tr>

				
		<tr>	<td colspan=2 bgcolor=lightgrey>Numeric types</td></tr>
			<tr><td>DECIMAL</td><td>java.math.BigDecimal</td></tr>
			<tr><td>NUMERIC</td><td>java.math.BigDecimal</td></tr>
			<tr><td colspan=2> numeric may also be mapped to
			<code>int</code> columns </td></tr>

		<tr>	<td colspan=2 bgcolor=lightgrey>Floating Point</td></tr>
			<tr><td>FLOAT</td><td>double</td></tr>
			<tr><td>DOUBLE</td><td>double; </td></tr>
			
			<tr><td>REAL</td><td>float</td></tr>
			<td colspan=2>FLOAT and DOUBLE are double ... but REAL is float</td>


		<tr>	<td colspan=2 bgcolor=lightgrey>  Integer	</td></tr>
			<tr><td>BIGINT (64 bit signed)</td><td>long</td></tr>
			<tr><td>INTEGER</td><td>int</td>
			<tr><td>SMALLINT ( -32768 -- 32767)</td><td>short</td>	
			<tr><td>TINYINT (0--255)</td><td>short</td>
					
			
		<tr>	<td colspan=2 bgcolor=lightgrey> Binary</td></tr>
			<tr><td>BIT</td><td>boolean</td></tr>	
			<tr><td>BINARY</td><td>byte[]</td></tr>	
			<tr><td>LONGVARBINARY</td><td>byte[]</td></tr>	
			<tr><td>VARBINARY</td><td>byte[]</td></tr>
			<tr><td colspan=2> note that LONGVARBINARY may be very large and hosuld be accessed by StreamsBinary</td></tr>
			
		<tr>	<td colspan=2 bgcolor=lightgrey> Character</td></tr>

			<tr><td>CHAR</td><td>String</td></tr>	
			<tr><td>VARCHAR</td><td>String</td></tr>	
			<tr><td>LONGVARCHAR</td><td>String</td></tr>	
			<tr><td colspan=2> note that LONGVARCHAR may be very large and hosuld be accessed as a Streams</td></tr>
		<tr>	<td colspan=2 bgcolor=lightgrey> Exotics	</td></tr>

			<tr><td>DISTINCT</td><td>n/a</td></tr>	
			<tr><td>NULL</td><td>n/a</td></tr>	
			<tr><td>OTHER</td><td>n/a</td></tr>	
	</table>	
		
	<h2>loading</h2>
	<h2>inserting/updateing</h2>
	<h2>deleting</h2>
	<h2>Defining relations between tables</h2>
	<h2>Non-Standard names, constructs in Schema</h2>

	


	
*/
public abstract class ORMObject implements Cloneable {
	
	/**
		Implement this method to provide access to the database
		this Object is stored in. In practice this method would
		act more or less like a static method, but as Java
		doesn't support proper inheritance for static methods,
		it becomes an instance method. Further, one could
		imagine diffrent instances of ORMObject being loaded
		from diffrent databases or being beind migrated from on
		DB to another.

	*/
	protected abstract SQLExecuter getSQLExecuter();
	
	/**
		Returns the name of the table that represents this
		object in the database. Or, if you like, the name of the
		database table this object is a representation of.
		Typically, the tablename gets dynamically detected at
		runtime, but you could override this function in case
		you can't or won't follow the naming conventions or
		you'd like to do performance optimizations and want to
		save some initialization/reflection/db-exploration time.
	*/
	
	public abstract String getTableName ();

	/**
		Provides access to all fields of this object that are
		mapped to database columns. Typically, the mapped fields
		get dynamically detected at runtime, but you could
		override this function in case you can't or won't follow
		the naming conventions or you'd like to do performance
		optimizations and want to save some
		initialization/reflection/db-exploration time.

	*/
	protected abstract ORMField [] getORMFields ();

	/**
		This method is used to retrieve the specific named field
		mapping. This provided key can be either the name of the
		mapped Java field, or the name of the column in the
		database.
	*/
	protected abstract ORMField getField (String key);

	/**	
		Retrieve an array of field mappings containing the
		mapped fields that represent the primary key(s) of this
		object. Typically, the primary key gets dynamically
		detected at runtime, but this method may get implemented
		to provide public keys that can't be deduced from the
		databases data dictionary or in order to optimize in
		order to cut back initialization time.
	*/
	protected abstract ORMField [] getPrimaryKeys(); 

	/**
		Retrieve the all on-to-one relationships of this Object.
		Typically, these would be detected automatically at
		runtime, but this method can be overriden in order to
		provide information about relationships that can't be 
		deduced from the databases data dictionary or in order
		to provide optimatizations for initialization.
	*/
	protected abstract OneToOneRelation [] getOneToOneRelations ();
	
	
	/**	
		Provide a value for the field named (either in the
		database table or the Java object)
		<code>fieldName</code>. In case the named field does not
		exist, this method throws a RuntimeException.
	*/
	public void set (String fieldName, Object value){
		ORMField field = getField(fieldName);
		if (field == null) {
			throw new RuntimeException ("[ORMObject.set] no such field: "+fieldName+" in "+getTableName());	
		}
		field.setFieldValue(this, value);
	}
	
	/**
		Retrieve the value of a named field from this Object.
		The provided fieldName can either refer to the name of
		the Java object or the name of the mapped database
		column. This method throws a RuntimeException in case a
		field with the provided name does not exist.
	*/
	public Object get (String fieldName) {

		ORMField field = getField(fieldName);	
		if (field == null) {
			throw new RuntimeException ("[ORMObject.get] no such field: "+fieldName+" in "+getTableName());	
		}
		return field.getValue(this);
		
	}

	/**
		Utility method to retrieve a named field as a String
		object. Throws a RuntimeException in case the named
		field does not exist.

		@see get
	*/
	public String getString (String fieldName) {
		return get(fieldName).toString();	
	}


	/**
		Writes this object to the database.
	*/
	public boolean save (){
		if (loadedFromDatabase()){
			if (update()) {
				return saveChildren();	
			} else {
				return false;	
			}
		} else {
			if (insert()) {
				return saveChildren();	
			} else {
				return false;	
			}
		}
	}
	
	/**
		Internal method to save the related Objects.
	*/
	private boolean saveChildren () {
		OneToOneRelation [] rels = getOneToOneRelations();
		for (int i=0; i!=rels.length; ++i) {
			ORMObject orm = ReflectionHelperORM.getFieldValue (this, rels[i].getField()); 
			if (!orm.save()){
				return false;	
			}
		}
		return true;
	}

	/**************************************************************
		prodably don't want this to be public.
	**************************************************************/
	
	public abstract boolean insert ();
	public abstract boolean update ();
	
	public abstract boolean delete (); //TODO abstract

	public static boolean DEBUG_SQL_STATEMENTS = true;	

	//////////////////////////////////////////////////		
	// override these for special behaviour
	//////////////////////////////////////////////////
	
	/**
		Generate the Select statement to load this Object from
		the database with. This statement is automatically
		generated, but may be overriden for non-standard
		behaviour. In case you're wondering, standard behaviour
		is to generate a statement like: <code>SELECT field1,
		field2 ... fieldn FROM tablename WHERE
		primary_key</code>.
	*/
	
	protected String getSelectStatement () {
		return getSQLInternal(SQLHelperORM.getSelectStatement(this));
	}

	/**
		Generate the Insert statement that this object gets
		injected into the database with.
	*/
	protected String getInsertStatement() {
		return getSQLInternal(SQLHelperORM.getInsertStatement(this));
	}
	
	/**
		Generate the Update statement this object gets saved
		with.
	*/
	protected String getUpdateStatement () {
		return getSQLInternal(SQLHelperORM.getUpdateStatement(this));	
	}
	
	/**
		Generate this objects delete statement.	
	*/
	protected String getDeleteStatement () {
		return getSQLInternal(SQLHelperORM.getDeleteStatement(this));	
	}
	
	/**
		Internal utilty method to enable SQL statement
		debugging.
	*/
	private static String getSQLInternal (String str) {
		if (DEBUG_SQL_STATEMENTS)
			System.err.println(str);
		return str;
	}
	
	/**
		Static method to load an <code>ORMObject</code> from the database.
		This class parameter needs to be included because of
		restrictions concerning overriding static methods in
		Java. Typically, you'll provide a method like the
		following in your implementations of
		<code>ORMObject</code> for ease
		of use:

		<code><pre>
		public static MyOrmObject load (String primKey) {
			Object [] arr = {primKey};
			return ORMObject.load(MyOrmObject.class, arr);
		}
		</pre></code>
	*/
	public static ORMObject load (Class clazz, Object [] primKeys) {
		
		ORMObject orm = cloneORM(ReflectionHelperORM.getSampleInstance(clazz));
		ReflectionHelperORM.setPrimaryKeyValues(orm, primKeys);		
		orm.loadFromDB();
		
		return orm;	
	}
	
	/**
		Same of load for objects that do not have composite
		primary keys. @see load
	*/
	public static ORMObject load (Class clazz, Object primKey){
		Object [] obj = {
			primKey	
		};
		return load(clazz, obj);
	}
	
	/**
		Static method provided to load multiple Objects
		simulaneously. Typically, you would use this method in
		order to provide utility methods in your implementations
		of <code>ORMObject</code>:
		<code><pre>
		public static MyOrmObject [] loadByName (String name) {
			ORMObject [] arr = ORMObject.loadWithWhere(MyOrmObject.class, "name="+name);
			MyOrmObject [] ret = new MyOrmObject[arr.length];
			for (int i = 0;i!=ret.length; ++i){
				ret[i]=(MyOrmObject)arr[i]	
			}
			return ret;
		}
		</pre></code>
		@param whereClause should not contain the "WHERE"	
		@see load
	*/
	public static ORMObject [] loadWithWhere (Class clazz, String whereClause) {
		StringBuffer buf = new StringBuffer();

		final ORMObject orm = ReflectionHelperORM.getSampleInstance(clazz);
		SQLHelperORM.getSelectStart(orm, buf);
		if (whereClause != null && !whereClause.trim().equals("")){
			buf.append(" WHERE ");
			buf.append(whereClause);
		}
		final LinkedList list = new LinkedList();
		
		if (DEBUG_SQL_STATEMENTS){
			System.err.println (buf.toString());
		}
		
		final ORMField [] fields = orm.getORMFields();

		orm.getSQLExecuter().execute(buf.toString(), new SQLFunction() {
			public void apply (ResultSet rset) throws SQLException {
				while (rset.next()) {
					ORMObject obj = cloneORM(orm); 
					for (int i=0; i!=fields.length; ++i){
						fields[i].get(rset,obj);
					}
					obj.loadRelations();
					obj.loadedFromDatabase=true;	
					list.add(obj);
				}	
			}	
		});
		return (ORMObject[])list.toArray(ORMOBJECT_ARR_CAST); 
	}
	
	/**
		Utilty to determine whether an object equal to the
		provided one already exists in the database. Note that
		equality is determined solely by checking for the
		existance of a row with this objects primary key values.
	*/
	public static boolean existsInDB (ORMObject orm) {
		return existsInDB (orm.getClass(), ReflectionHelperORM.getPrimaryKeyValues(orm));		
	}


	/**
		Utilty to determine whether an object equal to the
		provided one already exists in the database. Note that
		equality is determined solely by checking for the
		existance of a row with this objects primary key values.
	*/
	public static boolean existsInDB (Class clazz, Object [] primKeys) {
		ORMObject orm = ReflectionHelperORM.getSampleInstance(clazz);
		return orm.getSQLExecuter().getInt(SQLHelperORM.getCountStatement(clazz,primKeys)) == 1;		

	}
	
	/**
		This initializes the <code>ORMObject</code> from the database. Prerequisite is that
		all primary key fields in this object are set to values and that
		such a record is available in the database.
	*/
	protected void loadFromDB () {
		final ORMObject obj = this;
		getSQLExecuter().execute(getSelectStatement(), new SQLFunction () {
			public void apply(ResultSet rset) throws SQLException {
				if (!rset.next()){
					System.err.println("[WARNING] not available in DB: "+this.toString());
					return;
					// TODO: figure out what to do here. probably make it private
					// and only make ORMObjects loadable from Factory method.
				}
				ORMField [] f = getORMFields();
				for (int i=0; i!=f.length; ++i) {
					f[i].get(rset, obj);		
				}
				loadRelations();
				obj.loadedFromDatabase = true;
			}	
		});	
	}

	protected void loadRelations () {
		loadOneToOneRelations();	
	}

	protected void loadOneToOneRelations () {
		OneToOneRelation [] rel = getOneToOneRelations();
		for (int i=0; i!=rel.length; ++i) {
			Field f = rel[i].getField(); // field to place loaded value in.
			Object [] params = ReflectionHelperORM.getFieldValues(this, rel[i].getConnectingFields());// params to load with
			ORMObject orm = ORMObject.load(rel[i].getTo(), params); // load value
			ReflectionHelperORM.setField (f, this, orm); // set field in this object. 
		}
	}
	
	/**
		Provides information about whether this object was
		loaded from, or has been saved to, the database, or
		whether it exists purely in memory.
	*/
	public boolean loadedFromDatabase () {
		return loadedFromDatabase;	
	}

	public void setLoadedFromDatabase (boolean b) {
		this.loadedFromDatabase = b;	
	}

	private boolean loadedFromDatabase;

	private static final Object[] ORMOBJECT_ARR_CAST = new ORMObject[0];
	

	// For some reason I don't yet fully understand, I can't refactor this method into, e.g. ReflectionHelperORM
	// in order to reduce clutter. Compiler complains about o.clone() being protected, even if I make
	// ReflectionHelperORM derived from ORMObject...
	static ORMObject cloneORM (ORMObject o) {
		try {
			return (ORMObject)o.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace();
			throw new RuntimeException("[ORMObject.cloneORM] (CloneNotSupportedException) can't create new instance of:"+o.getClass());
		}	
	}

	public String toString () {
		StringBuffer buf = new StringBuffer();
		buf.append("Classname: ");
		buf.append(this.getClass().getName());
		buf.append("\nDB-Tablename: ");
		buf.append(getTableName());
		buf.append("\n");
		ORMField [] f = getORMFields();
		for (int i=0; i!=f.length; ++i) {
			buf.append("\t");
			buf.append(f[i].getName());
			buf.append(" : ");
			buf.append(f[i].getNameInTable());
			buf.append(" : ");
			buf.append(f[i].getValue(this));
		buf.append("\n");
		}
		return buf.toString();
	}

	
	

	
	

}
