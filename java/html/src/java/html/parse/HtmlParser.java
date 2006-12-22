package html.parse;

import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.*;

import html.*;
import function.*;
import utils.*;


public class HtmlParser extends HTMLEditorKit.ParserCallback {

	int indention;
	Tag startTag = null;
	Tag currTag = null;

	
	public void handleText(char[] data, int pos) {
		currTag.add(new String(data));	
	}
	

        public void handleComment(char[] data, int pos) {
	}

	public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		Tag tag = new Tag(t.toString());
		handleAttributes (tag, a);
		if (currTag!=null) {
			currTag.add(tag);
		} else {
			startTag = tag;	
		}
		currTag = tag;
	}

	public void handleEndTag(HTML.Tag t, int pos) {
		if (currTag != null && currTag.getParent()!=null)
			currTag = currTag.getParent();
	}

	public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		Tag tag = new Tag (t.toString());
		handleAttributes (tag, a);
		if (currTag!=null)
			currTag.add (tag);

		
	}

	public void handleError(String errorMsg, int pos){
	}

	public void handleAttributes (Tag t, MutableAttributeSet as) {
		for (Enumeration e = as.getAttributeNames(); e.hasMoreElements(); ) {
			Object name = e.nextElement();
			t.add (new Attribute (name.toString(), as.getAttribute(name).toString()));	
		}	
	}
	void indent () {
		for (int i=0; i!=indention; ++i)
			System.out.print("\t");
	}

	public static void main (String [] args) {
		Stopwatch.start ("");
		HtmlParser parser = new HtmlParser();
		try {
		Stopwatch.printTimeReset ("", "> ParserDelegator");
			ParserDelegator del = new ParserDelegator();
		Stopwatch.printTimeReset ("", "> url");
			URL url = new URL(args[0]);
		Stopwatch.printTimeReset ("", "> openStrem");
			InputStream is = url.openStream();
		Stopwatch.printTimeReset ("", "< parse");
			del.parse(new InputStreamReader(is), parser, true);
		Stopwatch.printTimeReset ("", "< parse");
		}catch (Throwable t){
			t.printStackTrace();	
		}
		//System.out.println (parser.startTag);
		
		Stopwatch.printTimeReset ("", "> traversal");
		TreeTraversal.traverse (parser.startTag, "eachChild", new Function (){
			String lastPath = null;
			public void apply (Object obj) {
				if (obj instanceof String){
					System.out.print(lastPath+":");
					System.out.println (obj);
					return;
				}
				Tag t = (Tag)obj;
				lastPath = Utils.tagPath(t);
				System.out.println(lastPath);
			}	
		});
		Stopwatch.printTimeReset ("", "< traversal");
		
		
				
		
		
		
	}

	static class DTDe extends DTD {
		DTDe (String str) {
			super(str);	
		}
		
		static DTD getDTD() {
			return new DTDe("html");	
		}	
	}

}
