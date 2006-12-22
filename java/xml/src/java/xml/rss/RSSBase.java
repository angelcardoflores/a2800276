package xml.rss;

public abstract class RSSBase {
	
	public RSSBase(){
		
	}
	public RSSBase (String title, String link, String description) {
		setTitle(title);
		setLink(link);
		setDescription(description);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		title of this element
	*/
	private String title;

	/** 
		link of element
	*/
	private String link;

	/** 
		description of element
	*/
	private String description;

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>title</code>
		@see #title
	*/
	public String getTitle () {
		return this.title;
	}

	/** 
		getter method for <code>link</code>
		@see #link
	*/
	public String getLink () {
		return this.link;
	}

	/** 
		getter method for <code>description</code>
		@see #description
	*/
	public String getDescription () {
		return this.description;
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>title</code>
		@see #title
	*/
	public void setTitle (String title) {
		this.title=title;
	}

	/** 
		setter method for <code>link</code>
		@see #link
	*/
	public void setLink (String link) {
		this.link=link;
	}

	/** 
		setter method for <code>description</code>
		@see #description
	*/
	public void setDescription (String description) {
		this.description=description;
	}


	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append ("title: '"+getTitle()+"'\n");
		buf.append ("description: '"+getDescription()+"'\n");
		buf.append ("link: '"+getLink()+"'\n");
		return buf.toString();
	}


}
