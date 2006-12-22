package html;
import java.io.*;
public class HtmlDocument {
	
	String fileName;
	String docName;
	Tag base;
	Tag body;
	Tag addToThis;
	
	public HtmlDocument (String docName, String fileName) {
		this (docName, fileName, null);
	}
	public HtmlDocument (String docName, String fileName, Frameset frameset) {
		this.fileName = fileName;
		this.docName = docName;
		this.body = frameset;
		init();
	}
	public String getFileName () {
		return fileName;	
	}

	public String getDocName () {
		return docName;	
	}

	private void init () {
		base = new Tag ("html");
		base.add(new Head (this.docName));
		if (body == null)
			body = new Tag ("body");
		base.add(body);
		addToThis = body;
	}

	public void add (Tag tag) {
		addToThis.add (tag);	
	}

	public void add (String string) {
		addToThis.add (string);	
	}

	/**
		Create a nesting for the body. Usually a call to add will append
		items to the body tag. A call to nest will add the passed tag to 
		the body and all further content added by HtmlDocument.add will be 
		appended to the nested Tag, not the original body.
	*/

	public void addNesting (Tag tag) {
		addToThis.add (tag);
		addToThis = tag;
	}

	public void addNew (Tag tag) {
		body.add(tag);
		addToThis = tag;
	}
	
	public void writeToDisc () throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter (fileName);
			writer.write (base.toString());
		} finally {
			writer.close();
		}
	}

	
}
