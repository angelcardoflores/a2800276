package sqlTools.orm;

import java.lang.reflect.*;

public abstract class OneToOneRelation {

	private static final Class ORM = ORMObject.class;
	private Class from;
	private Class to;
	private Field field;
	/**
		@field is the classes field that contains the mapped ORMObject.
	*/
	public OneToOneRelation (Field field) {
		this.from = field.getDeclaringClass();
		this.to = field.getType();
		this.field = field;

		if (!ORM.isAssignableFrom(from) || !ORM.isAssignableFrom(to)){
			throw new RuntimeException("[AbstractOneToOneRelation.<init>] 'from' or 'to' not ORMObjects ( from:"+from+" to:"+to+")");	
		}
	}

	public Class getFrom () {
		return from;	
	}
	public Class getTo () {
		return to;	
	}

	public Field getField() {
		return field;	
	}

	/**
		retrieves the fields in the FROM Object, with which the connected 
		object is loaded from the DB.
	*/
	public abstract ORMField [] getConnectingFields ();

	public String toString () {
		return "[OneToOneRelation] connecting: "+getFrom()+" to: "+getTo();
		
	}
	
}

//	Field f = // whatever
//	Class c = g.getType();
//	
//	ORMObject orm = load (c, this.getMappedKeys())
	
		
