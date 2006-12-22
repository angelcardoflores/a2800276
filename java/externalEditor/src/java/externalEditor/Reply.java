package externalEditor;

public class Reply extends Message {
	
	public Reply (String seqNo, String[] args) {
		this (seqNo);
		this.setArgs (args);
	} 
	
	public Reply (String seqNo, String arg) {
		this (seqNo);
		this.setArg(arg);
		
	}
	
	public Reply (String seqNo) {
		setSeqNo (seqNo);
	}
	

	public boolean verbAllowed (String verb) {
		return false; // Replies don't have verbs...	
	}
}
