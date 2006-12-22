package generator;

import java.util.*;


public class Class {

	private String name;
	private Package pack;
	private Class extend;
	private LinkedList fields = new LinkedList();
	private LinkedList imports = new LinkedList();
	
	public Class (java.lang.Class clss) {
		this (getClassName(clss), new Package(getPackageName(clss)));	
	}
	public Class (String name) {
		this(name, new Package());	
	}
	public Class (String name, Package pack) {
		this.pack=pack;
		this.name=name.trim();
	}

	public Class (String name, String packName) {
		this (name, new Package(packName));	
	}

	public void setPackage (Package pack) {
		this.pack = pack;	
	}

	public void setExtends (Class clss) {
		this.extend = clss;		
	}

	public void addField (Field f) {
		fields.add(f);	
	}

	public void addImport (Class c) {
		imports.add(c);	
	}


	public String getCode () {
		StringBuffer buf = new StringBuffer();
		buf.append(pack.getCode());
		buf.append(getImportsCode());
		buf.append ("\n");
		buf.append("public class ");
		buf.append(this.name);
		if (this.extend != null) {
			buf.append(" extends ");
			buf.append(extend.getFullName());
		}
		buf.append(" {\n\n");
		buf.append( getFieldCode() );
		buf.append("}");
		return buf.toString();		
	}

	public String getName() {
		return this.name;	
	}
	public String getFullName() {
		String n ="";
		if (this.pack.getName()!=null && !this.pack.getName().trim().equals(""))
			n+=this.pack.getName()+".";
		return n+getName();
	}

	private String getFieldCode () {
		StringBuffer buf = new StringBuffer();
		for (Iterator it = fields.iterator(); it.hasNext();  ){
			buf.append (((Field)it.next()).getCode());	
		}

		return buf.toString();
	}

	private String getImportsCode () {
		StringBuffer buf = new StringBuffer();
		for (Iterator it = imports.iterator(); it.hasNext();  ){
			buf.append ("import ");
			buf.append (((Class)it.next()).getFullName());
			buf.append (";\n");
		}

		return buf.toString();

	}
	
	static String getClassName (java.lang.Class clazz) {
		String name = clazz.getName();
		return name.substring(name.lastIndexOf('.')+1, name.length());
	}
	static String getPackageName (java.lang.Class clazz) {
		String name = clazz.getName();
		return name.substring(0, name.lastIndexOf('.'));
	}

	public static void main (String [] args) {
		Class c = new Class (Class.class);
		c.addField(new Field("fuckField", c, true, true, java.lang.reflect.Modifier.PROTECTED));
		System.out.println(c.getCode());	
	}

}

