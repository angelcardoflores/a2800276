package html.color;


import html.*;

import java.awt.Color;


public class Gradient {
	
	private int redFrom;
	private int redStep;
	
	private int greenFrom;
	private int greenStep;

	private int blueFrom;
	private int blueStep;

	
	
	public Gradient (Color from, Color to, int steps) {
	
		redFrom = from.getRed();
		redStep = (to.getRed() - redFrom) / steps;

		greenFrom = from.getGreen();
		greenStep = (to.getGreen() - greenFrom) / steps;

		blueFrom = from.getBlue();
		blueStep = (to.getBlue() - blueFrom) / steps;
		
	}

	public Color getColor (int step) {
		return new Color (	redFrom+(redStep*step), 
					greenFrom+(greenStep*step), 
					blueFrom+(blueStep*step)
				);	
	}

	public String getHtmlColor (int step) {
		Color curr = getColor (step);
	
		StringBuffer buf = new StringBuffer();
		buf.append ("#");
		buf.append (toHexString(curr.getRed()));
		buf.append (toHexString(curr.getGreen()));
		buf.append (toHexString(curr.getBlue()));

		return buf.toString ();
		
	}

	private static String toHexString (int i) {
		String str = Integer.toHexString(i);
		if (str.length()==1)
			return "0"+str;
		if (str.length()>2)
			return "FF";
		return str;
	}

	static Tag makeTable (Color to, Color from) {
		Gradient g = new Gradient (to, from, 100);
		Tag table = new Tag ("table");
		table.add (new Attribute ("width", 200));
		table.add (new Attribute ("height", 200));
		table.add (new Attribute ("cellpadding", "0"));
		table.add (new Attribute ("cellspacing", "0"));


		Img img = new Img ("pixel.gif");
		img.setWidth(200);
		img.setHeight(1);

		for (int i = 0; i!=200; i++) {
			Tag tr = new Tag ("tr", new Tag ("td",img));
			
			tr.add (new Attribute ("bgcolor",g.getHtmlColor(i/2)));
			table.add(tr);
		}

		return table;

	}

	public static void main (String [] args) {
		HtmlDocument doc = new HtmlDocument ("Gradient Test", "gradienttest.html");
		doc.add (TagMaker.getH(3, "Red - Blue"));
		doc.add (makeTable (Color.red, Color.blue));
		doc.add (TagMaker.getH(3, "Gray - Yellow"));
		doc.add (makeTable (Color.lightGray, Color.yellow));
		doc.add (TagMaker.getH(3, "Orange - cyan"));
		doc.add (makeTable (Color.orange, Color.cyan));
		try {
			doc.writeToDisc();
			
		} catch (Throwable t) {
			t.printStackTrace();	
		}
		
		
		
	}
	
}
