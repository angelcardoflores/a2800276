package sqlTools.functions;

import function.*;
import sqlTools.*;
import java.util.*;

public class ToSchemaArray implements SafeFunction {
	LinkedList list = new LinkedList();

	public void apply (Object obj) {
		list.add (new Schema(((String[])obj)[0]));	
	}
	public Schema [] toArray () {
		return (Schema[])list.toArray(new Schema[0]);	
	}
}

