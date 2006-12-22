package html.parse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.DocumentParser;
import javax.swing.text.html.parser.TagElement;

public class HtmlParser2 extends DocumentParser {
	
	public HtmlParser2 () {
		super(HtmlParser.DTDe.getDTD());	
	}

	protected void handleStartTag(TagElement tag) {
		System.out.println ("start:"+tag);	
	}
	protected void handleComment(char[] text) {
		System.out.println ("comment: "+text);	
	}
	protected void handleEmptyTag(TagElement tag){
		System.out.println ("empty: "+tag);	
	}
	protected void handleEndTag(TagElement tag){
		System.out.println("end: "+tag);	
	}
	protected void handleText(char[] data) {
		System.out.println ("text: "+ data);
	}
	protected void handleError(int ln,
                           String errorMsg) {
		System.out.println ("Error: "+errorMsg);		  		 
	}

	public void parse(String url) {
		try {
			URL ur = new URL(url);
			InputStream is = ur.openStream();
			InputStreamReader isr = new InputStreamReader(is);
			super.parse (isr, new HTMLEditorKit.ParserCallback(), true);
		} catch (Throwable t) {
			t.printStackTrace();	
		} 
	}

	public static void main (String [] args) {
		HtmlParser2 parser = new HtmlParser2();
		parser.parse(args[0]);
	}
           
	
}
