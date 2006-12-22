package html;

import java.awt.Color;
import html.color.Gradient;

public abstract class AbstractPixelTableModel implements PixelTableModel {
	public abstract int getHeight ();
	public abstract int getWidth ();

	public int getPixelPerYPoint() {
		return 10;	
	}
	public int getPixelPerXPoint() {
		return 1;	
	}

	public abstract String getColor (int x, int y);
	public String getText (int x, int y) {
		return "";	
	}

	public static void main (String [] args) {
		
		final Gradient g = new Gradient (Color.green, Color.red, 100);
	
		PixelTableModel pix = new AbstractPixelTableModel () {
			public int getHeight () {
				return 10;	
			}
			public int getWidth () {
				return 100;	
			}
			
			String currColor;
			public String getColor (int x, int y) {
				Gradient g2 = new Gradient (g.getColor(x), Color.blue, 10);
				currColor = g2.getHtmlColor(y);
				return g2.getHtmlColor(y);
			}

			public String getText (int x, int y) {
				return x + " : " + y;	
			}
		};	
		HtmlDocument doc = new HtmlDocument ("PixelTable Test", "pixTableTest.html");
		doc.add (new PixelTable(pix));
		try {
			doc.writeToDisc();	
		} catch (Throwable t) {
			t.printStackTrace();	
		}
	}

}
