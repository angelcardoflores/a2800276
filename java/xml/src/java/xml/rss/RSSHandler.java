package xml.rss;

import org.xml.sax.helpers.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class RSSHandler extends DefaultHandler {

	Channel masterBase;
	RSSBase currentBase;

	int currentState = STATE_UNKNOWN;
	int prevState = STATE_UNKNOWN;
	
	final static int STATE_UNKNOWN = 0;
	final static int STATE_TITLE = 1;
	final static int STATE_LINK = 2;
	final static int STATE_DESCRIPTION = 3;
	final static int STATE_ITEM = 4;
	final static int STATE_CHANNEL = 5;

	

	public RSSHandler (String uri) {
		masterBase = new Channel();
		currentBase = masterBase;
		parse(uri);
	}

	void parse (String uri) {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(uri, this);
		} catch (Throwable t) {
			t.printStackTrace();	
		}
	
	}

	public Channel getChannel () {
		return this.masterBase;	
	}

	public void startElement (java.lang.String uri, java.lang.String localName, java.lang.String qName,
		Attributes attributes) throws SAXException {
		if (qName == null)
			return;

		if (qName.trim().equalsIgnoreCase("channel")){
			currentState = STATE_CHANNEL;	
			return;
		}
		
		if (qName.trim().equalsIgnoreCase ("item")) {
			currentState = STATE_ITEM;
			return;
		}
		
		if (currentState == STATE_CHANNEL || currentState == STATE_ITEM) {

			clearChars();

			if (qName.trim().equalsIgnoreCase ("title")) {
				prevState = currentState;
				currentState = STATE_TITLE;
				return;
			}
			if (qName.trim().equalsIgnoreCase ("description")) {
				prevState = currentState;
				currentState = STATE_DESCRIPTION;
				return;
			}
			if (qName.trim().equalsIgnoreCase ("link")) {
				prevState = currentState;
				currentState = STATE_LINK;
				return;
			}


		}


		prevState = currentState;
		currentState = STATE_UNKNOWN;
			
	}
	
	public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName)
		throws SAXException {
		System.out.println(qName);
			
			clearChars();
			if (qName.trim().equalsIgnoreCase("channel")){
				currentBase = new Item();
				return;
			}
			
			if (qName.trim().equalsIgnoreCase ("item")) {
				masterBase.addItem((Item)currentBase);
				currentBase = new Item();
				return;
			}
			
			if (qName.trim().equalsIgnoreCase ("title")) {
				currentState = prevState;
				return;
			}
			if (qName.trim().equalsIgnoreCase ("description")) {
				currentState = prevState;
				return;
			}
			if (qName.trim().equalsIgnoreCase ("link")) {
				currentState = prevState;
				return;
			}
			currentState = prevState;

		}


	public void endDocument () {
		if (currentBase!=masterBase)
			masterBase.addItem((Item)currentBase);
		clearChars();	
	}

	StringBuffer buffer = new StringBuffer();
	boolean lastCharWS;

	public void characters(char[] ch, int start, int length) {
//		char [] tmp = new char[length+2];
//		System.arraycopy (ch,start, tmp, 1, length);
//		tmp[0]='>';
//		tmp[tmp.length-1]='<';
//		System.out.println(currentState+":"+new String(tmp));

		for (int i=start; i!=start+length; ++i) {
			if (Character.isWhitespace(ch[i])) {
				if (lastCharWS)
					continue;
				lastCharWS = true;
				buffer.append(" ");
				continue;
			} else {
				lastCharWS = false;
				buffer.append(ch[i]);
			}
		}
	}

	public void clearChars () {
		String tmp = buffer.toString().trim();

	System.out.println("CC: "+currentState+" "+tmp);
		buffer=new StringBuffer();
		lastCharWS=false;
		switch (currentState) {
			case STATE_LINK: 
				currentBase.setLink(tmp);
				break;
			case STATE_DESCRIPTION:
				currentBase.setDescription(tmp);
				break;
			case STATE_TITLE:
				currentBase.setTitle(tmp);
				break;
			case STATE_UNKNOWN: 
			default: 
				break;
		}
	}
	
	private static void usage () {
		System.err.println("usage: [jre] xml.rss.RSSHandler uri");
		System.exit(0);	
	}

	public static void main (String [] args){
		if (args.length != 1)
			usage();
		RSSHandler handler = new RSSHandler(args[0]);
		System.out.println(handler.getChannel());
	}

	

	

}
