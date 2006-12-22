package xml.rss;

public class Item extends RSSBase {

	public Item () {
		super();	
	}
	public Item (String title, String link, String description) {
		super (title, link, description);	
	}

	public String toString () {
		return "Item\n"+super.toString();	
	}
}
