package sqlTools.collection;

import java.util.HashMap;
import java.util.Iterator;

import function.Function;

public class FuncHashMap extends HashMap {

		
	/**
	 * 
	 */
	private static final long serialVersionUID = -7542052065291755315L;

	public String eachValue (Function func) {
		try {
			for (Iterator it = values().iterator(); it.hasNext();){
				func.apply(it.next());				
			}	
		}catch (Throwable t) {
			t.printStackTrace();
			return t.getMessage();
		}	
		return null;
	}

	public Object [] toArray () {
		Class c = null;
		for (Iterator it = values().iterator(); it.hasNext();) {
			c = it.next().getClass();	
			break;
		}	
		if (c==null)
			return null;
			
		return values().toArray((Object[])java.lang.reflect.Array.newInstance(c,0));
	}
}
