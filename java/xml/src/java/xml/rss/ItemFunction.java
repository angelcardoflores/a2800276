package xml.rss;

import function.*;

public abstract class ItemFunction implements Function {
	
	public void apply (Object obj) {
		apply ((Item)obj);	
	}

	public abstract void apply (Item item);
}
