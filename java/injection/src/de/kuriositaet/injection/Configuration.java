package de.kuriositaet.injection;

public class Configuration {
	private Binding[] bindings;

	public Configuration(Binding... bindings){
		this.bindings = bindings;
	}

	public Binding[] getBindings() {
		return bindings;
	}

	public void setBindings(Binding[] bindings) {
		this.bindings = bindings;
	}
	
}
