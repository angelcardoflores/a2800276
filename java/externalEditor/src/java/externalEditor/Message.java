package externalEditor;

/**
	Base class of the netbeans External Editor protocol.
*/


public abstract class Message {
		
	public static final String TXT = Arg.TXT;
	public static final String BOOL = Arg.BOOL;
	public static final String NUM = Arg.NUM;
	public static final String OPTNUM = Arg.OPTNUM;

	
	public static Message parseMessage (String message) {
		char [] msg = message.toCharArray();
	System.out.println(message);
	System.out.println(msg.length);
		int i=0;
	System.out.println("i="+i+" msg[i]="+msg[i]+"");
		if (msg[i]<'0'||msg[i]>'9') {
			throw new RuntimeException("ParseError! msg doesn't start with proxyID!");	
		}
		String proxyID="";
		while (true) { // get proxyID

			if (msg.length<=i || msg[i]<'0'||msg[i]>'9') break; // reached ":"
			proxyID+=msg[i++];
		}
		if (msg.length<=i || msg[i]!=':') {
			// Responses are of the form
			//	seqNo [optional args or error]
			// all other Message type have a ':' following the
			// initial digits.
			return parseReply (proxyID, message); // what we thought was the proxyID was seqNo!	
		}
		++i;
		Message retMessage = null;
		
		// parse msg name
		String name = "";
		while  (true) {
			if (!Character.isLetter(msg[i])) break;
			name += msg[i++];
		}

		
		
		if (msg[i]=='!')
			retMessage = new Command();
		else if (msg[i]=='/')
			retMessage = new Function();
		else if (msg[i]=='=')
			retMessage = new Event();
		else {
			throw new RuntimeException("ParseError! not a proper msg-type. seqno must be followed with one of !/= ("+message+")");
			
		}
		++i;
		
		retMessage.setProxyID(proxyID);
		retMessage.setVerb(name);
		

		String seqNo = "";

		if (msg[i]<'0'||msg[i]>'9') {
			throw new RuntimeException("ParseError! seqNo is invalid: ("+message+")");	
		}
		while (true) { // get proxyID
			if (i==msg.length || msg[i]<'0'||msg[i]>'9') break; // reached " "
			seqNo+=msg[i++];
		}
		retMessage.setSeqNo(seqNo);
		retMessage.setArgs(parseArgs(message));
		return retMessage;
		
	}

	
	public static Reply parseReply (String seqno, String mes){
		Reply rep = new Reply(seqno);
		rep.setArgs(parseArgs(mes));
		return rep;	
	}
	
	public static String[] parseArgs(String message){
		int argsStart = message.indexOf(' ');
		if (argsStart==-1)
			return null;

		String argsPart = message.substring(++argsStart, message.length());
		return new utils.StringTokenizer(argsPart).toArray();
		
	}
	
	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		proxyID is a decimal integer, starting at 1 (0 is reserved for "global" EVTs); 
		it identifies the particular buffer.	
	*/
	private String proxyID;

	/** 
		name of the message/command
	*/
	private String verb;

	/** 
		seqno is a positive decimal integer that monotonically 
		increases with each CMD or FUN; REPLY uses the seqno of the corresponding FUN.
	*/
	private String seqNo;

	/** 
		may be any of:
			"string"      (literal quotes; newlines, carriage returns, double quotes, and backslashes encoded as \n \r \" \\, resp.)
			T             ("true")
			F             ("false")      
			[+-]digits    (decimal integer)
	*/
	private Arg[] args;

	private Class messageType;

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>proxyID</code>
		@see #proxyID
	*/
	public String getProxyID () {
		return this.proxyID;
	}

	/** 
		getter method for <code>verb</code>
		@see #verb
	*/
	public String getVerb () {
		return this.verb;
	}

	/** 
		getter method for <code>seqNo</code>
		@see #seqNo
	*/
	public String getSeqNo () {
		return this.seqNo;
	}

	/** 
		getter method for <code>args</code>
		@see #args
	*/
	public Arg[] getArgs () {
		return this.args;
	}
	/** 
		getter method for <code>messageType</code>
		@see #messageType
	*/
	public Class getMessageType () {
		if (this.messageType == null) {
			this.messageType = this.getClass();	
		}
		return this.messageType;
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>proxyID</code>
		@see #proxyID
	*/
	public void setProxyID (String proxyID) {
		this.proxyID=proxyID;
	}

	/** 
		setter method for <code>verb</code>
		@see #verb
	*/
	public void setVerb (String verb) {
		if (!verbAllowed(verb)) {
			throw new RuntimeException("msg name not supported ("+verb+")");
		}
		this.verb=verb;
	}

	public abstract boolean verbAllowed (String verb);

	/** 
		setter method for <code>seqNo</code>
		@see #seqNo
	*/
	public void setSeqNo (String seqNo) {
		this.seqNo=seqNo;
	}

	/** 
		setter method for <code>args</code>
		@see #args
	*/
	public void setArgs (String[] argStrings) {
		if (argStrings==null)
			return;
		this.args = new Arg[argStrings.length];
		for (int i = 0; i!=args.length; ++i){
			this.args[i]=Arg.parseArg(argStrings[i]);
		}
		
	}

	public void setArgs (Arg[] args) {
		this.args=args;	
	}

	public void setArg (String arg) {
		String [] args = {arg};
		setArgs(args);	
	}


	public String toString () {
		StringBuffer buf = new StringBuffer();
		buf.append ("Type: "+getMessageType().getName()+"\n");
		buf.append ("ProxyID: "+getProxyID()+"\n");
		buf.append ("Verb: "+getVerb()+"\n");
		buf.append ("SeqNo: "+getSeqNo()+"\n");
		for (int i=0; (getArgs()!=null&&i!=getArgs().length); ++i) {
			buf.append ("\t"+i+") "+getArgs()[i]+"\n");	
		}
		return buf.toString();
		
	}
	public static void main (String [] args) {
		for (int i=0; i!=args.length; ++i)
			System.out.println(Message.parseMessage(args[i]));
	}

	private static final String [] MSG_VERBS = {
		"actionMenuItem",
		"actionSensitivity",
		"addAnno",
		"balloonEval",
		"balloonResult",
		"balloonText",
		"buttonRelease",
		"close",
		"create",
		"defineAnnoType",
		"editFile",
		"enableBalloonEval",
		"endAtomic",
		"fileClosed",
		"fileModified",
		"fileOpened",
		"geometry",
		"getCursor",
		"getDot",
		"getLength",
		"getMark",
		"getModified",
		"getText",
		"guard",
		"initDone",
		"insert",
		"insert",
		"invokeAction",
		"keyAtPos",
		"keyCommand",
		"killed",
		"moveAnnoToFront",
		"netbeansBuffer",
		"newDotAndMark",
		"putBufferNumber",
		"quit",
		"raise",
		"remove",
		"remove",
		"removeAnno",
		"revert",
		"save",
		"save",
		"saveAndExit",
		"setAsUser",
		"setBufferNumber",
		"setContentType",
		"setDot",
		"setExitDelay",
		"setFullName",
		"setLocAndSize",
		"setMark",
		"setModified",
		"setReadOnly",
		"setStyle",
		"setTitle",
		"setVisible",
		"showBalloon",
		"specialKeys",
		"startAtomic",
		"startCaretListen",
		"startDocumentListen",
		"startupDone",
		"stopCaretListen",
		"stopDocumentListen",
		"unguard",
		"unmodified",
		"version",
		"version"
	};
}
