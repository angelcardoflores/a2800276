package de.kuriositaet.injection;

public class Injector {
	private Configuration[] configurations;

	public Injector(Configuration...configurations){
		this.configurations = configurations;
	}
	
	public <T> T createInstance(Class<T> clazz) {
		for (Configuration config : configurations){
			
		}
		return null;
	}
}
