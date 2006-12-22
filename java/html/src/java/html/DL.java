package html;

public class DL extends Tag{
	class D extends Tag {
		public D (String name) {
			super (name);	
		}

		public D (String name, String content) {
			super (name, content);	
		}
		public int numIndent ()	{
			return super.numIndent();	
		}
		public boolean breaks () {
			return false;	
		}
	}

	class DD extends D {
		DD (String definition) {
			super ("dd", definition);	
		}	
	}

	class DT extends D {
		DT (String term) {
			super ("dt", term);	
		}	
		DT (Tag tag) {
			super ("dt");
			add(tag);
		}
	}

	public DL () {
		super ("dl");	
	}

	public DL (String term, String def) {
		this();
		addDefinition (term, def);
	}
	
	public DL (Tag term, String def) {
		this();
		addDefinition (term, def);
	}

	public void addDefinition (String term, String def) {
		add (new DT (term));
		add (new DD (def));	
	}

	public void addDefinition (Tag term, String def) {
		add( new DT (term) );
		add (new DD(def));
	}
}
