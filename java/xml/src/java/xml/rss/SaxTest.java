package xml.rss;

import org.xml.sax.helpers.*;
import org.xml.sax.*;
import javax.xml.parsers.*;


public class SaxTest extends DefaultHandler {
	
	public void startElement(java.lang.String uri,
                         java.lang.String localName,
                         java.lang.String qName,
                         Attributes attributes)
	throws SAXException {
		System.out.println ("uri: "+uri);
		System.out.println ("localName: "+localName);
		System.out.println ("qName: "+qName);
		System.out.println ("attributes: "+attributes);
		for (int i=0; i!=attributes.getLength(); ++i){
				System.out.println(i+" "+attributes.getQName(i));
				System.out.println(i+" "+attributes.getValue(i));
		}
	}

	public void characters(char[] ch, int start, int length) {
		StringBuffer buf = new StringBuffer();
		for (int i=start; i!=start+length; ++i)
			buf.append (ch[i]);
		System.out.println("chrs: "+ buf.toString());	
			
	}


	public static void main (String [] args) {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(args[0], new SaxTest());
		} catch (Throwable t) {
			t.printStackTrace();	
		}
	}
}
