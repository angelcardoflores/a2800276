package xml.rss;

import java.util.*;
import function.*;

public class Channel extends RSSBase {
	
	LinkedList items = new LinkedList();
	public Channel (){
		super();	
	}
	public Channel (String title, String link,  String description) {
		super (title, link, description);	
	}

	public void addItem (Item item) {
		items.add(item);			
	}

	public void eachItem (Function f){
		for (Iterator it = items.iterator(); it.hasNext();){
			try {
				f.apply (it.next());
			} catch (Throwable t) {
				t.printStackTrace();	
			}
		}	
	}

	public String toString () {
		final StringBuffer buf = new StringBuffer();
		buf.append("Channel\n"+super.toString());
		eachItem (new ItemFunction(){
			public void apply (Item it) {
				buf.append("\n");
				buf.append(it);
			}	
		});
		return buf.toString();
	}
}
