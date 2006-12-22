package externalEditor;

import java.util.Hashtable;

public class Command extends Message {
	
	public Command (String proxyID, String verb, String seqNo, String[] args) {
		this (proxyID, verb, seqNo);	
		setArgs (args);
		
		
	} 
	
	public Command (String proxyID, String verb, String seqNo, String arg) {
		this (proxyID, verb, seqNo);	
		setArg (arg);
		
	}
	
	public Command (String proxyID, String verb, String seqNo) {
		setProxyID (proxyID);
		setVerb (verb);
		setSeqNo (seqNo);
	}

	public Command () { }

	
	public boolean verbAllowed (String verb) {
		return ALLOWED.containsKey(verb);
	
	}

	private static final Hashtable ALLOWED;
	static {
		ALLOWED = new Hashtable();

		final String [] VERBS ={
			"actionMenuItem",
			"actionSensitivity",
			"addAnno",
			"balloonResult",
			"close",
			"create",
			"defineAnnoType",
			"editFile",
			"enableBalloonEval",
			"endAtomic",
			"guard",
			"initDone",
			"moveAnnoToFront",
			"netbeansBuffer",
			"putBufferNumber",
			"raise",
			"removeAnno",
			"save",
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
			"stopCaretListen",
			"stopDocumentListen",
			"unguard",
			"version"
		};

		String [][] ARGS ={
			{ TXT, TXT, BOOL, TXT },
			{ TXT, BOOL },
			{ NUM, NUM, NUM, NUM },
			{ TXT },
			{},
			{},
			{ NUM, TXT, TXT, TXT, OPTNUM, OPTNUM },
			// destroyPosition ? in netbeans doc, but not in vim
			{ TXT},
			{},
			{},
			{},
			{},
			{ NUM },
			{ BOOL },
			{ TXT },
			{},
			{ NUM },
			{},
			{ BOOL },
			{ TXT },
			{ TXT },
			{ NUM },
			{ NUM },
			{ TXT },
			{ NUM, NUM, NUM, NUM },
			{ NUM },
			{ BOOL },
			{ BOOL },
			{}, 
			{ TXT },
			{ BOOL },
			{ TXT },
			{ TXT },
			{},
			{},
			{},
			{},
			{},
			{},
			{}
		};



		for (int i=0; i!=VERBS.length; ++i){
			ALLOWED.put(VERBS[i], ARGS[i]);
		}	
	}
	
	

	
	
}
