package sqlTools;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.reflect.*;

public class TypesUtil {
	
	public static String getTypeName (String num) {
		try {
			return getTypeName (Integer.parseInt(num));	
		} catch (NumberFormatException nfe) {
				
		}
		return "unknown";
	}
	public static String getTypeName (int javaSqlType) {
		switch (javaSqlType) {

			case Types.ARRAY:	 return "ARRAY";
			case Types.BIGINT:	 return "BIGINT";
			case Types.BINARY:	 return "BINARY";
			case Types.BIT:		 return "BIT";
			case Types.BLOB:	 return "BLOB";
			case Types.CHAR:	 return "CHAR";
			case Types.CLOB:	 return "CLOB";
			case Types.DATE:	 return "DATE";
			case Types.DECIMAL:	 return "DECIMAL";
			case Types.DISTINCT:	 return "DISTINCT";
			case Types.DOUBLE:	 return "DOUBLE";
			case Types.FLOAT:	 return "FLOAT";
			case Types.INTEGER:	 return "INTEGER";
			case Types.JAVA_OBJECT:	 return "JAVA_OBJECT";
			case Types.LONGVARBINARY:return "LONGVARBINARY";
			case Types.LONGVARCHAR:	 return "LONGVARCHAR";
			case Types.NULL:	 return "NULL";
			case Types.NUMERIC:	 return "NUMERIC";
			case Types.OTHER:	 return "OTHER";
			case Types.REAL:	 return "REAL";
			case Types.REF:		 return "REF";
			case Types.SMALLINT:	 return "SMALLINT";
			case Types.STRUCT:	 return "STRUCT";
			case Types.TIME:	 return "TIME";
			case Types.TIMESTAMP:	 return "TIMESTAMP";
			case Types.TINYINT:	 return "TINYINT";
			case Types.VARBINARY:	 return "VARBINARY";
			case Types.VARCHAR:	 return "VARCHAR";
		}
		return "unknown";
	}

	public static Class getJavaType (int javaSqlType) {
		switch (javaSqlType) {
			////////////////////////////////////////	
			// "Advanced" Types                   //
			////////////////////////////////////////	
			case Types.ARRAY:	 return java.sql.Array.class;
			case Types.BLOB:	 return java.sql.Blob.class;
			case Types.CLOB:	 return java.sql.Clob.class;
			case Types.STRUCT:	 return java.sql.Struct.class;
			case Types.REF:		 return java.sql.Ref.class;
			case Types.JAVA_OBJECT:	 return Object.class;

			////////////////////////////////////////	
			// Date related                       //
			////////////////////////////////////////	
			case Types.DATE:	 return java.sql.Date.class;
			case Types.TIME:	 return java.sql.Time.class;
			case Types.TIMESTAMP:	 return java.sql.Timestamp.class;

				
			////////////////////////////////////////	
			// Numeric related                    //
			////////////////////////////////////////	
			case Types.DECIMAL:	 
			case Types.NUMERIC:
						return java.math.BigDecimal.class;

			////////////////////////////////////////	
			// Floating Point		      //
			////////////////////////////////////////	
			case Types.FLOAT:	 
			case Types.DOUBLE:	// FLOAT and DOUBLE are double ...	
						return DOUBLE_CLASS;
			
			case Types.REAL:	// but REAL is float (!?)
						return FLOAT_CLASS;


			////////////////////////////////////////	
			// Integer			      //
			////////////////////////////////////////	

			case Types.BIGINT:	// 64 bit signed
						return LONG_CLASS;

			case Types.INTEGER:	//-2147483648 -- 2147483647 
						return INT_CLASS;
						
			case Types.SMALLINT:	// -32768 -- 32767 
			case Types.TINYINT: 	// 0--255
						return SHORT_CLASS;
			
			
			////////////////////////////////////////	
			// Binary			      //
			////////////////////////////////////////	
			
			case Types.BIT:		 
						return BOOLEAN_CLASS;
			case Types.BINARY:	 
			case Types.LONGVARBINARY:
			case Types.VARBINARY:	
						// note that LONGVARBINARY may be very
						// large and hosuld be accessed by Streams
						return BYTE_ARRAY_CLASS;
						

			////////////////////////////////////////	
			// Character			      //
			////////////////////////////////////////	
			case Types.CHAR:	 		
			case Types.VARCHAR:	 
			case Types.LONGVARCHAR:	// note that LONGVARCHAR fields can be huge and
						// should be accessed as Streams.
						return String.class;
			
			////////////////////////////////////////	
			// Exotics			      //
			////////////////////////////////////////	
			case Types.DISTINCT:	 // we don't do DISTINCT, NULL or OTHER
			case Types.NULL: 
			case Types.OTHER:
			default:
						return null;
		}
	}

	private static final Class BYTE_ARRAY_CLASS = (new byte[0]).getClass();
	private static final Class BOOLEAN_CLASS = (new boolean[0]).getClass().getComponentType();
	private static final Class SHORT_CLASS = (new short[0]).getClass().getComponentType();
	private static final Class INT_CLASS = (new int[0]).getClass().getComponentType();
	private static final Class DOUBLE_CLASS = (new double[0]).getClass().getComponentType();
	private static final Class FLOAT_CLASS = (new float[0]).getClass().getComponentType();
	private static final Class LONG_CLASS = (new long[0]).getClass().getComponentType();

	
	/**
		Returns the SQL-Type that gets mapped to the the class.
		@returns Types.NULL in case the class can't be mapped to SQL.
		Note that java.lang.Object (while being a possible mapping for
		Types.JAVA_OBJECT) also returns NULL for obvious reasons.
		Other classes, such as <code>String</code>, which aren't 
		one-to-one mappings, will return what may seem to be an arbitrary
		mappings, described below.

		String -> VARCHAR
		byte[] -> BINARY
		short -> SMALLINT
		double -> DOUBLE
		BigDecimal -> NUMERIC
		
		
		

		
	*/
	public static int getSqlType (Class clazz) {

		if (clazz.equals (String.class)) 		{ return Types.VARCHAR; }
		if (clazz.equals(BYTE_ARRAY_CLASS)) 		{ return Types.BINARY;}
		if (clazz.equals(BOOLEAN_CLASS)) 		{ return Types.BIT; }
		if (clazz.equals(SHORT_CLASS)) 			{ return Types.SMALLINT; }
		if (clazz.equals(INT_CLASS)) 			{ return Types.INTEGER; }
		if (clazz.equals(LONG_CLASS))	 		{ return Types.BIGINT; }
		if (clazz.equals(FLOAT_CLASS)) 			{ return Types.REAL; }
		if (clazz.equals(DOUBLE_CLASS)) 		{ return Types.DOUBLE; }
		if (clazz.equals(java.math.BigDecimal.class)) 	{ return Types.NUMERIC; }
		if (clazz.equals(java.sql.Timestamp.class)) 	{ return Types.TIMESTAMP; }
		if (clazz.equals(java.sql.Time.class)) 		{ return Types.TIME; }
		if (clazz.equals(java.sql.Date.class)) 		{ return Types.DATE; }
		if (clazz.equals(java.sql.Array.class)) 	{ return Types.ARRAY; }
		if (clazz.equals(java.sql.Blob.class)) 		{ return Types.BLOB; }
		if (clazz.equals(java.sql.Clob.class)) 		{ return Types.CLOB; }
		if (clazz.equals(java.sql.Struct.class)) 	{ return Types.STRUCT; }
		if (clazz.equals(java.sql.Ref.class)) 		{ return Types.REF; }
		return Types.NULL;
	}
	
	/**	
		Method of <code>ResultSet</code>, used to retrieve the
		SQL Type with.
	*/
	public static Method getGetMethod (int javaSqlType) {
		switch (javaSqlType) {
			////////////////////////////////////////	
			// "Advanced" Types                   //
			////////////////////////////////////////	
			case Types.ARRAY:	 return GET_ARRAY;
			case Types.BLOB:	 return GET_BLOB;
			case Types.CLOB:	 return GET_CLOB;
			case Types.STRUCT:	 return GET_STRUCT;
			case Types.REF:		 return GET_REF;
			case Types.JAVA_OBJECT:	 return GET_OBJECT;

			////////////////////////////////////////	
			// Date related                       //
			////////////////////////////////////////	
			case Types.DATE:	 return GET_DATE;
			case Types.TIME:	 return GET_TIME;
			case Types.TIMESTAMP:	 return GET_TIMESTAMP;

				
			////////////////////////////////////////	
			// Numeric related                    //
			////////////////////////////////////////	
			case Types.DECIMAL:	 
			case Types.NUMERIC:
						return GET_BIG_DECIMAL;

			////////////////////////////////////////	
			// Floating Point		      //
			////////////////////////////////////////	
			case Types.FLOAT:	 
			case Types.DOUBLE:	
						return GET_DOUBLE;
			
			case Types.REAL:	 
						return GET_FLOAT;


			////////////////////////////////////////	
			// Integer			      //
			////////////////////////////////////////	

			case Types.BIGINT:	// 64 bit signed
						return GET_LONG;

			case Types.INTEGER:	//-2147483648 -- 2147483647 
						return GET_INT;
						
			case Types.SMALLINT:	// -32768 -- 32767 
			case Types.TINYINT: 	// 0--255
						return GET_SHORT;
			
			
			////////////////////////////////////////	
			// Binary			      //
			////////////////////////////////////////	
			
			case Types.BIT:		 
						return GET_BOOLEAN;
			case Types.BINARY:	 
			case Types.LONGVARBINARY:
			case Types.VARBINARY:	
						// note that LONGVARBINARY may be very
						// large and hosuld be accessed by Streams
						return GET_BYTES;
						

			////////////////////////////////////////	
			// Character			      //
			////////////////////////////////////////	
			case Types.CHAR:	 		
			case Types.VARCHAR:	 
			case Types.LONGVARCHAR:	// note that LONGVARCHAR fields can be huge and
						// should be accessed as Streams.
						return GET_STRING;
			
			////////////////////////////////////////	
			// Exotics			      //
			////////////////////////////////////////	
			case Types.DISTINCT:	 // we don't do DISTINCT, NULL or OTHER
			case Types.NULL: 
			case Types.OTHER:
			default:
						return null;
		}
	
	}


	public static Method getSetMethod (int javaSqlType) {
		switch (javaSqlType) {
			////////////////////////////////////////	
			// "Advanced" Types                   //
			////////////////////////////////////////	
			case Types.ARRAY:	 return SET_ARRAY;
			case Types.BLOB:	 return SET_BLOB;
			case Types.CLOB:	 return SET_CLOB;
			case Types.STRUCT:	 return SET_STRUCT;
			case Types.REF:		 return SET_REF;
			case Types.JAVA_OBJECT:	 return SET_OBJECT;

			////////////////////////////////////////	
			// Date related                       //
			////////////////////////////////////////	
			case Types.DATE:	 return SET_DATE;
			case Types.TIME:	 return SET_TIME;
			case Types.TIMESTAMP:	 return SET_TIMESTAMP;

				
			////////////////////////////////////////	
			// Numeric related                    //
			////////////////////////////////////////	
			case Types.DECIMAL:	 
			case Types.NUMERIC:
						return SET_BIG_DECIMAL;

			////////////////////////////////////////	
			// Floating Point		      //
			////////////////////////////////////////	
			case Types.FLOAT:	 
			case Types.DOUBLE:	
						return SET_DOUBLE;
			
			case Types.REAL:	 
						return SET_FLOAT;


			////////////////////////////////////////	
			// Integer			      //
			////////////////////////////////////////	

			case Types.BIGINT:	// 64 bit signed
						return SET_LONG;

			case Types.INTEGER:	//-2147483648 -- 2147483647 
						return SET_INT;
						
			case Types.SMALLINT:	// -32768 -- 32767 
			case Types.TINYINT: 	// 0--255
						return SET_SHORT;
			
			
			////////////////////////////////////////	
			// Binary			      //
			////////////////////////////////////////	
			
			case Types.BIT:		 
						return SET_BOOLEAN;
			case Types.BINARY:	 
			case Types.LONGVARBINARY:
			case Types.VARBINARY:	
						// note that LONGVARBINARY may be very
						// large and hosuld be accessed by Streams
						return SET_BYTES;
						

			////////////////////////////////////////	
			// Character			      //
			////////////////////////////////////////	
			case Types.CHAR:	 		
			case Types.VARCHAR:	 
			case Types.LONGVARCHAR:	// note that LONGVARCHAR fields can be huge and
						// should be accessed as Streams.
						return SET_STRING;
			
			////////////////////////////////////////	
			// Exotics			      //
			////////////////////////////////////////	
			case Types.DISTINCT:	 // we don't do DISTINCT, NULL or OTHER
			case Types.NULL: 
			case Types.OTHER:
			default:
						return null;
		}
	
	}
	private static Method GET_ARRAY;
	private static Method GET_BLOB;
	private static Method GET_CLOB;
	private static Method GET_STRUCT;
	private static Method GET_REF;
	private static Method GET_OBJECT;
	private static Method GET_DATE;
	private static Method GET_TIME;
	private static Method GET_TIMESTAMP;
	private static Method GET_BIG_DECIMAL;
	private static Method GET_DOUBLE;
	private static Method GET_FLOAT;
	private static Method GET_LONG;
	private static Method GET_INT;
	private static Method GET_SHORT;
	private static Method GET_BOOLEAN;
	private static Method GET_BYTES;
	private static Method GET_STRING;



	private static Method SET_ARRAY;
	private static Method SET_BLOB;
	private static Method SET_CLOB;
	private static Method SET_STRUCT;
	private static Method SET_REF;
	private static Method SET_OBJECT;
	private static Method SET_DATE;
	private static Method SET_TIME;
	private static Method SET_TIMESTAMP;
	private static Method SET_BIG_DECIMAL;
	private static Method SET_DOUBLE;
	private static Method SET_FLOAT;
	private static Method SET_LONG;
	private static Method SET_INT;
	private static Method SET_SHORT;
	private static Method SET_BOOLEAN;
	private static Method SET_BYTES;
	private static Method SET_STRING;


	static {
		init();
	}						
	private static void init () {
		Class resultSetClass = ResultSet.class;
		Method [] methods = resultSetClass.getMethods();
		String methName = null;
		
		for (int i=0; i!=methods.length; ++i) {
			if (methods[i].getParameterTypes().length != 1){
				continue;	
			}
			methName = methods[i].getName();
			if ("getArray".equals(methName)) 	{ GET_ARRAY = methods[i]; }
			else if ("getBlob".equals(methName))	{ GET_BLOB = methods[i]; }
			else if ("getClob".equals(methods))	{ GET_CLOB = methods[i]; }
			else if ("getStruct".equals(methods))	{ GET_STRUCT = methods[i]; }
			else if ("getRef".equals(methName))	{ GET_REF = methods[i]; }
			else if ("getObject".equals(methName))	{ GET_OBJECT = methods[i]; }
			else if ("getDate".equals(methName))	{ GET_DATE = methods[i]; }
			else if ("getTime".equals(methName))	{ GET_TIME = methods[i]; }
			else if ("getTimestamp".equals(methName))	{ GET_TIMESTAMP = methods[i]; }
			else if ("getBigDecimal".equals(methName))	{ GET_BIG_DECIMAL = methods[i]; }
			else if ("getDouble".equals(methName))	{ GET_DOUBLE = methods[i]; }
			else if ("getReal".equals(methName))	{ GET_FLOAT = methods[i]; }
			else if ("getLong".equals(methName))	{ GET_LONG = methods[i]; }
			else if ("getInt".equals(methName))	{ GET_INT = methods[i]; }
			else if ("getShort".equals(methName))	{ GET_SHORT = methods[i]; }
			else if ("getBoolean".equals(methName))	{ GET_BOOLEAN = methods[i]; }
			else if ("getBytes".equals(methName))	{ GET_BYTES = methods[i]; }
			else if ("getString".equals(methName))	{ GET_STRING = methods[i]; }
		}
		
		Class pstmtClass = PreparedStatement.class;
		methods = pstmtClass.getMethods();
		for (int i=0; i!=methods.length; ++i) {
			if (methods[i].getParameterTypes().length != 2){ continue; }
			
			methName = methods[i].getName();
			if ("setArray".equals(methName)) 	{ SET_ARRAY = methods[i]; }
			else if ("setBlob".equals(methName))	{ SET_BLOB = methods[i]; }
			else if ("setClob".equals(methods))	{ SET_CLOB = methods[i]; }
			else if ("setStruct".equals(methods))	{ SET_STRUCT = methods[i]; }
			else if ("setRef".equals(methName))	{ SET_REF = methods[i]; }
			else if ("setObject".equals(methName))	{ SET_OBJECT = methods[i]; }
			else if ("setDate".equals(methName))	{ SET_DATE = methods[i]; }
			else if ("setTime".equals(methName))	{ SET_TIME = methods[i]; }
			else if ("setTimestamp".equals(methName))	{ SET_TIMESTAMP = methods[i]; }
			else if ("setBigDecimal".equals(methName))	{ SET_BIG_DECIMAL = methods[i]; }
			else if ("setDouble".equals(methName))	{ SET_DOUBLE = methods[i]; }
			else if ("setReal".equals(methName))	{ SET_FLOAT = methods[i]; }
			else if ("setLong".equals(methName))	{ SET_LONG = methods[i]; }
			else if ("setInt".equals(methName))	{ SET_INT = methods[i]; }
			else if ("setShort".equals(methName))	{ SET_SHORT = methods[i]; }
			else if ("setBoolean".equals(methName))	{ SET_BOOLEAN = methods[i]; }
			else if ("setBytes".equals(methName))	{ SET_BYTES = methods[i]; }
			else if ("setString".equals(methName))	{ SET_STRING = methods[i]; }

		}
		
	} // init


} // class
