package function;

import java.lang.reflect.*;

public class TreeTraversal {
	
	static final Class [] parameterTypes = {
		Function.class	
	};

	static class TraversalFunction implements Function {
		Function work;
		Method traversal;
		public TraversalFunction (Function work, Method traversal) {
			this.work = work;
			this.traversal = traversal;
		}
		
		public void apply (Object obj) {
			try {
				work.apply (obj);
				Object [] parameter = {
					this
				};
				if (traversal.getDeclaringClass()==obj.getClass())
					traversal.invoke(obj,parameter);
			} catch (Throwable t) {
			
				t.printStackTrace();	
			}
			
		}	
		
	}

	public static String traverse (Object startNode, String childMethodName, Function func) {
		Method traversalMethod = null;
		try {
			traversalMethod = startNode.getClass().getMethod (childMethodName, parameterTypes);	
			// the method the startNode provides to access all it's children.
		} catch (Throwable t) {
			t.printStackTrace();		
			return t.getMessage();
		}
		
		TraversalFunction traverse = new TraversalFunction (func, traversalMethod);
		traverse.apply (startNode);
		return null;

	}
}
