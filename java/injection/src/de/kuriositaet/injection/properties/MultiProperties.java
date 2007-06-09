package de.kuriositaet.injection.properties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MultiProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3852437924582648907L;
	private Map<String, MultiProperties> subProperties;
	



	public MultiProperties() {
		super();
		this.subProperties = new HashMap<String, MultiProperties>();
	}

	
	
	public MultiProperties getSubProperties (String key) {
		if (this.subProperties.containsKey(key)) {
			return this.subProperties.get(key);
		}
		
		String lookingFor = key+".";
		String newKey = null;
		MultiProperties p = new MultiProperties();
		for (Object k_tmp : this.keySet()){
			String k = (String)k_tmp;
			if (k.startsWith(lookingFor)) {
				newKey = k.substring(lookingFor.length());
				p.setProperty(newKey, this.getProperty(k));
			}
		}
		this.subProperties.put(key, p);
		return p;
	}
	
	public Set<String> getSubPropertyKeys () {
		String key = null;
		Set<String> ret = new HashSet<String>();
		for (Object k_tmp: this.keySet()){
			key = (String)k_tmp;
			int dotPos = key.indexOf('.');
			if (dotPos != -1) {
				ret.add(key.substring(0, dotPos));
			}
		}
		return ret;
	}

}
