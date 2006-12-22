package externalEditor;

import java.util.Hashtable;

public class Event extends Message {
	
	public Event (String proxyID, String verb, String seqNo, String[] args) {
		this (proxyID, verb, seqNo);
		setArgs (args);
	} 
	
	public Event (String proxyID, String verb, String seqNo, String arg) {
		this (proxyID, verb, seqNo);
		this.setArg(arg);
		
	}
	
	public Event (String proxyID, String verb, String seqNo) {
		setProxyID (proxyID);
		setVerb (verb);
		setSeqNo (seqNo);
	}

	public Event () {
		
	}

	public boolean verbAllowed (String verb) {
		return Event.ALLOWED.containsKey(verb);
	}
	
	private static Hashtable ALLOWED; 
	static {
		ALLOWED = new Hashtable();	
		String [] VERBS = {
			"balloonEval",
			"balloonText",
			"buttonRelease",
			"fileClosed",
			"fileModified",
			"fileOpened",
			"geometry",
			"insert",
			"invokeAction",
			"keyCommand",
			"keyAtPos",
			"killed",
			"newDotAndMark",
			"quit",
			"remove",
			"revert",
			"save",
			"startupDone",
			"unmodified",
			"version"
		};

		String [][] ARGS = {
			{NUM, NUM, NUM},
			{TXT},
			{NUM,NUM,NUM},
			{TXT},
			{TXT,BOOL},
			{TXT,BOOL,BOOL},
			{NUM,NUM,NUM,NUM},
			{NUM,TXT},
			{TXT},
			{TXT},
			{TXT,NUM},
			{},
			{NUM,NUM},
			{},
			{NUM,NUM},
			{},
			{},
			{},
			{},
			{TXT}	
		};


		for (int i=0; i!= VERBS.length; ++i){
			ALLOWED.put(VERBS[i],ARGS[i]);
		}

	}

 
}
