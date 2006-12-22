package html;

public class TagMaker {
	

	public static Tag getH (int level, String text) {
		if (level>6 || level < 1)
			level = 1;
			
		return new Tag ("h"+level, text) {
			public boolean breaks () {
				return false;	
			}	
		};
	}

	public static Tag getP (String align, String text) {
		Tag p = new Tag ("p", text) {
			public boolean breaks () {
				return false;	
			} 	
		};	
		p.add (new Attribute ("align", align));
		return p;
	}

	public static Tag getP (String text) {
		return getP ("left", text);	
	}

	public static Tag getSmall (Tag tag) {
		return new Tag ("small", tag) {
			public boolean breaks () {
				return false;	
			}	
		};	
	}
}
