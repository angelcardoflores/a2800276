package de.kuriositaet.injection;

public class BindingException extends RuntimeException {

	

	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}


	public BindingException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1664367772991438493L;

}
