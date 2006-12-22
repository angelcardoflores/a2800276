package sqlTools.sample;
import sqlTools.orm.*;
import utils.*;
public class ZvtLog extends CmdLineOrm {
	public int logId;
	public java.lang.String	contractNo;
	public java.lang.String	terminalId;
	public java.lang.String	type;
	public java.lang.String	pan;
	public java.lang.String	expDate;
	public java.lang.String	dateTime;
	public java.lang.String	amount;
	public java.lang.String	answercode;
	public java.lang.String	traceNo;
	public java.lang.String	authId;
	public java.lang.String	txTag;
	public java.lang.String	capturetoken;
	public java.lang.String	merchantId;
	public java.lang.String	transId;
	public java.lang.String	currency;
	public java.lang.String	dialogData;
	public java.lang.String	retrievalRef;
	public java.lang.String	ddProcCode;
	public java.lang.String	ddPosEntryMode;
	public java.lang.String	ddPosCondCode;
	public java.lang.String	ddCaptureRef;
	public java.lang.String	ddAsTraceNum;
	public java.lang.String	ddAid59;
	public java.lang.String	modAuthId;
	public java.lang.String	brandId;
	public java.sql.Timestamp	txDate;
	public java.lang.String	cvv;
	public java.lang.String xid_3d;
        public java.lang.String cavv_3d;
	public java.lang.String	eci;
	public java.lang.String	dialogDataOut;
	public java.lang.String	callerId;
	public java.lang.String	txMode;

	public static void main(String [] args) {
		DBCmdLine cmd = new DBCmdLine(args);
		if (!cmd.complete() || cmd.get("-l")==null) {
			System.err.println("[jre] sqlTools.sample.ZvtLog");	
			System.err.println("\t -l <logid>");
			System.err.println(DBCmdLine.usage());
			System.exit(1);
		}
		ZvtLog.setCmdLine(cmd);
		Object [] logs = ZvtLog.loadWithWhere(ZvtLog.class, "LOG_ID > "+cmd.get("-l"));
		for (int i=0; i!=logs.length; ++i) {
			System.out.println(logs[i]);	
		}
	}
} // end class
