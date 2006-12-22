package externalEditor;

import java.util.Hashtable;

public class Function extends Message {
	
	public Function (String proxyID, String verb, String seqNo, String[] args) {
		this (proxyID, verb, seqNo);
		setArgs (args);
	} 
	
	public Function (String proxyID, String verb, String seqNo, String arg) {
		this (proxyID, verb, seqNo);
		this.setArg(arg);
		
	}
	
	public Function (String proxyID, String verb, String seqNo) {
		setProxyID (proxyID);
		setVerb (verb);
		setSeqNo (seqNo);
	}
	
	public Function (){
		
	}
	public boolean verbAllowed (String verb) {
		return ALLOWED.containsKey(verb);	
	}

	private static Hashtable ALLOWED; 
	
	static {
		ALLOWED = new Hashtable	();

		String [] VERBS={
			"getDot",
			"getCursor",
			"getLength",
			"getMark",
			"getModified",
			"getText",
			"insert",
			"lookupPosition",
			"remove",
			"saveAndExit"
		};
		String [][] ARGS={
			{NUM},
			{},
			{NUM},
			{NUM},
			{},
			{TXT, NUM},
			{TXT,NUM},
			{},
			{TXT,NUM},
			{}
		};



		for (int i=0; i!= VERBS.length; ++i){
			Function.ALLOWED.put(VERBS[i],ARGS[i]);
		}
	}


}
