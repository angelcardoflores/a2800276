package generator;

public class Package {
	private String name;
	

	public Package () {
		this(null);	
	}
	public Package (String name) {
		if (name != null) {
			this.name = name.trim();	
		} else {
			this.name = "";	
		}
	}

	public String getName() {
		return this.name;	
	}

	public String getCode () {
		if (this.name != null){
			return "package "+this.name+";\n\n";	
		}
		return "";
	}
}
