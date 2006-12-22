package generator;

import java.lang.reflect.Modifier;

public class Field {

	private String name;
	private Class type;
	private boolean withGet;
	private boolean withSet;
	private int mod;
	
	public Field (String name, Class type) {
		this (name, type, false, false, java.lang.reflect.Modifier.PUBLIC);
			
	}
	
	/**
		@param modifier see java.lang.reflect.Modifier
	*/
	public Field (String name, Class type, boolean set, boolean get, int modifier) {
		this.name = name;
		this.type = type;
		this.withSet = set;
		this.withGet = get;
		this.mod = modifier;
	}

	public String getCode() {
		StringBuffer buf = new StringBuffer ();
		buf.append (fieldDef());
		
		buf.append (setDef());
		buf.append (getDef());	
		return buf.toString();
	}

	private String fieldDef() {
		StringBuffer buf = new StringBuffer();	
		buf.append ("\t");
		if (Modifier.isPublic(this.mod)) {
			buf.append ("public ");	
		} else if (Modifier.isProtected(this.mod)){
			buf.append ("protected ");	
		} else if (Modifier.isPrivate(this.mod)) {
			buf.append ("private ");	
		}
		buf.append (type.getFullName());
		buf.append (" ");
		buf.append (name);
		buf.append (";\n\n");
		return buf.toString();
	}
	
	private String setDef () {
		if (!withSet) return "";
		StringBuffer buf = new StringBuffer();
		buf.append ("\tpublic void set");
		buf.append (capitalise(this.name));
		buf.append ("("+type.getFullName()+" "+this.name+"){\n\t\t");
		buf.append ("this."+this.name+" = "+this.name+";\n\t}\n\n");
		return buf.toString();	
	}

	private String getDef () {
		if (!withGet) return "";
		StringBuffer buf = new StringBuffer();
		buf.append ("\tpublic "+type.getFullName());
		buf.append (" get"+capitalise(this.name)+"() {\n\t\t");
		buf.append (" return this."+this.name+";\n\t}\n\n");
		return buf.toString();	

	
	}
	private String capitalise (String str) {
		char [] c = str.toCharArray();
		if (c!=null && c.length != 0){
			c[0]=Character.toUpperCase(c[0]);	
		}
		return new String (c);
	}
	
}
