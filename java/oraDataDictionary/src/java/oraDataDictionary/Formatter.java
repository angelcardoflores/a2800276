package oraDataDictionary;

import html.*;
import java.util.*;
import java.io.*;

public class Formatter {
	
	private String dir;
	private String ver;

	private final static String [] header = {
		"Column Name", "Description"	
	};
	private HtmlDocument navigation = new HtmlDocument("Data Dictionary, Navigation", "navi.html");
	private FormattedTable indexForContent = new FormattedTable();
	private HtmlDocument content = new HtmlDocument ("Overview ... ", "overview.html");

	public Formatter (String dir, String ver) {
		this.dir = dir;
		this.ver = ver;
		initFrameSet();
		initIndex();
		//initThemeIndex();
		
	}
	
	private String lastPrefix = null;
	private boolean firstTime = true; 
	public void formatTable (DDTable table) {
		
		// Add Entry to Navi Index.
		ATag tag = new ATag (table.getName());
		tag.setHref ("overview.html#"+table.getName());
		tag.setAnchor (table.getName());
		tag.setTarget ("content");
		navigation.add (TagMaker.getSmall(tag));
			
		String prefix = null;
		if (table.getName().indexOf('_')!=-1)
			prefix = table.getName().substring(0,table.getName().indexOf('_'));
		else
			prefix = table.getName();
			
		if (!prefix.equals(lastPrefix)) {
			lastPrefix = prefix;
			// new table prefix
			indexForContent.add (new ATag (prefix, "#"+table.getName()));
		
			if (!firstTime)	{
				FormattedTable tab = new FormattedTable ();
				tab.setLeftToRight();
				tab.setNumColumns(2);
				content.addNew (TagMaker.getSmall(new ATag("up", "#index")));
				content.addNew (tab);
			} else {
				firstTime = false;	
			}
		}
		
		// Add Entry to Main Index
		tag = new ATag (table.getName());
		tag.setHref (table.getName()+".html");
		tag.setAnchor (table.getName());
		this.content.add (new DL (tag, table.getComments()));

		//Create Table Page

		HtmlDocument tableDoc = new HtmlDocument("Data Dict. Doc for: "+table.getName(), table.getName()+".html");
		tableDoc.add (TagMaker.getH(2, table.getName()));
		tableDoc.add (TagMaker.getP(table.getComments()));	
		
		Table htmlTable = new Table (header);
		htmlTable.setBorder (1);
			
		DDTable.DDNameComment col = null;
		ATag crossRef = null;
		Object [] tableCol = new Object[2];
		for (Iterator it = table.iterator(); it.hasNext();){
			col = (DDTable.DDNameComment)it.next();
			// each col is indexable through
			// hred=tableName.html#colName
			crossRef = new ATag (col.getName());
			crossRef.setAnchor(col.getName());
			tableCol[0] = crossRef;
			tableCol[1] = col.getComments();
			htmlTable.add(tableCol);
		}
		tableDoc.add (htmlTable);	
		ATag home = new ATag ("return to Index", "overview.html#"+table.getName());
		tableDoc.add (home);
		try{
			tableDoc.writeToDisc();	
		} catch (IOException ioe) {
			
		}
		
			
	}

	void initFrameSet () {
		Frameset fs = new ClassicLeftRightNaviFrameset (navigation, content);
		HtmlDocument doc = new HtmlDocument ("Oracle Data Dictionary Doc: Ver. "+ver, "index.html", fs);
		
		try {
			doc.writeToDisc();	
		}catch (IOException ioe) {
			ioe.printStackTrace();	
		}
	}

	void initIndex () {
		FormattedTable tab = new FormattedTable ();
		tab.setLeftToRight();
		tab.setNumColumns(2);
		ATag tag = new ATag (" ");
		tag.setAnchor("index");
		content.add (tag);
		indexForContent.setNumColumns(4);
		content.add (indexForContent);
		content.addNesting (tab);

	}

	void finish () {
		try {
			navigation.writeToDisc();
		content.addNew (TagMaker.getSmall(new ATag("up", "#index")));
			content.writeToDisc();
		} catch (IOException ioe) {
			ioe.printStackTrace();	
		}
	}
}
