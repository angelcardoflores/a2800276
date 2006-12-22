package sqlTools.sample;

import sqlTools.orm.*;

public class MerchantZvt extends CmdLineOrm {

	public MerchantZvt () {
		super ();	
	}

	public int id;
	public int termSeq;
	public int country;
	public String kontonummer;
	public String autorTyp="1";

	
	protected ORMField [] getPrimaryKeys () {
		if (this.prim == null) {
			this.prim = new ORMField[1];
			this.prim [0] = getField("id");
		}
		return this.prim;
	}
	private ORMField [] prim;
}
