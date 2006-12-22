package html;
import java.util.*;
import function.*;

public class Tag {
	String name;
	LinkedList attributes;
	LinkedList content;
	Tag parent;
	
	public Tag (String name) {
		this.name = name;
		parent = this;
	}

	public Tag (String name, String content) {
		this (name);
		add (content);
	}

	public Tag (String name, Tag content) {
		this(name);
		add (content);
	}

	public void add(Tag tag) {
		if (content == null)
			content = new LinkedList();
		tag.parent = this;
		content.add (tag);
	}

	public void add(String str) {
		
//		if (content == null)
//			content = new LinkedList();
//		if (str == null)
//			str = "&nbsp;";
		add (new Text(str));
			
	}

	public void add (Attribute attr) {
		if (!isAllowed(attr))
			return;
		if (attributes == null)
			attributes = new LinkedList();
		attributes.add (attr);
	}
	/**
		returns Parent or reference to itself if the tag has no parent.
	*/
	public Tag getParent() {
		return this.parent;	
	}

	public String getName() {
		return this.name;	
	}

	public void eachChild (Function func) {
		if (content==null)
			return;
		for (Iterator it = content.iterator(); it.hasNext();){
			try {
				func.apply(it.next());
			} catch (Throwable t){
				t.printStackTrace();	
			}
		}
	}

	/**
		is this an empty tag, i.e. is it written in the form
		<bla/>, or will it be written in the form <bla>... </bla>

		Override this in Child classes.
		
	*/
	public boolean isEmpty () {
		return false;	
	}

	/**
		Check whether the attribute is allowed for this type of
		tag. Override this in derived classes.
	*/

	public boolean isAllowed (Attribute attr) {
		return true;	
	}
	
	/**
		Whether the String representation of this Tag
		inserts linebreaks. Override in children if
		necessary.
	*/
	public boolean breaks () {
		return true;	
	}
	
	/**
		How many positions (tabs, x-spaces) to indent this tag;
		defaults to the same number of positions as the parent. Override 
		in derived classes.
	*/
	public int numIndent () {
		if (this==parent)
			return 0;
		return parent.numIndent();
	}
	private String indent = null;
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		
		if (indent == null) {
			indent = "";
			for (int i=0; i!=numIndent(); i++) {
				indent += '\t';
			}
			
		}

		buf.append (this.name);
		if (attributes != null) {
			for (Iterator it = attributes.iterator(); it.hasNext();) {
				buf.append (" ");
				buf.append (it.next());
			}	
		}
		if (isEmpty()) {
			makeEmptyTag(buf);
			buf.insert (0,indent);
			return buf.toString();
		}
		
		makeTag (buf);
		buf.insert (0,indent);
		
		if (content != null) {
			for (Iterator it = content.iterator(); it.hasNext();) {
				if (breaks()) {

					buf.append ("\n");
					buf.append (indent);
				}
				buf.append (it.next());
			}	
		}

		makeEndTag (buf);
		
		return buf.toString();
		
			
	}

	private static void makeEmptyTag (StringBuffer buf) {
		buf.append("/");
		makeTag (buf);
	}

	private void makeEndTag (StringBuffer buf) {
		if (breaks()) {
			buf.append('\n');
			buf.append (indent);
		}
		buf.append("</");
		buf.append(this.name);
		buf.append(">");
		
	}

	private static void makeTag (StringBuffer buf) {
		buf.insert (0,'<');
		buf.append ('>');
	}
	
	/**
		derived classes might new to recalculate the way
		they display content.
		@see FormattedTable
	*/
	
	protected void clear () {
		this.content = null;	
	}
	public static void main (String [] args) {
		Tag tag = new Tag ("html");
		tag.add (new Tag ("head"));
		Tag body = new Tag ("body");
		tag.add (body);
		body.add(new Attribute("test", 123));
		body.add ("This is a test");
		System.out.println (tag);
		
	}


}
