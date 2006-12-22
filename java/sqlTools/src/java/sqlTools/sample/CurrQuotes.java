package sqlTools.sample;

import sqlTools.orm.*;
import java.sql.*;
import java.math.*;

import utils.*;

public class CurrQuotes extends CmdLineOrm {

	protected long id;
	protected long currId;
	protected BigDecimal rateEuro;
	protected BigDecimal rateDollar;
	protected BigDecimal rateGbp;
	protected Timestamp quoteDate;

	public long getId() {
		return this.id;
	}

	public long getCurrId() {
		return this.currId;
	}

	public BigDecimal getRateEuro() {
		return this.rateEuro;
	}

	public BigDecimal getRateDollar() {
		return this.rateDollar;
	}

	public BigDecimal getRateGbp() {
		return this.rateGbp;
	}

	public Timestamp getQuoteDate() {
		return this.quoteDate;
	}

	public void setId(long value) {
		this.id = value;
	}

	public void setCurrId(long value) {
		this.currId = value;
	}

	public void setRateEuro(BigDecimal value) {
		this.rateEuro = value;
	}

	public void setRateDollar(BigDecimal value) {
		this.rateDollar = value;
	}

	public void setRateGbp(BigDecimal value) {
		this.rateGbp = value;
	}

	public void setQuoteDate(Timestamp value) {
		this.quoteDate = value;
	}

	public static void main (String [] args) {
		CmdLineOrm.setCmdLine(new DBCmdLine(args));
//		CurrQuotes [] quotes = (CurrQuotes [])CurrQuotes.loadWithWhere(CurrQuotes.class, "");	
		ORMObject [] quotes = CurrQuotes.loadWithWhere(CurrQuotes.class, "");	
		for (int i=0; i!=quotes.length; ++i) {
			System.out.println(quotes[i]);	
		}
		((CurrQuotes)quotes[0]).getSQLExecuter().close();
	
	}

} // end class
