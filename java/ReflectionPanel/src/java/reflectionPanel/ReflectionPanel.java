package reflectionPanel;


import javax.swing.*;
import java.lang.reflect.*;
import java.util.*;

/**
	Panel used to Display data of an arbitrary class. Uses
	Reflection to determine Methods in the class to Display. Any
	method whose name starts which "get" will be displayed, if the
	same attribute is also available in a method starting with
	"set", data entry will also be allowed.

	This implementation only support Methods with type "String"

*/

public class ReflectionPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3457286780778530014L;
	Class klass;
	Class STRING_CLASS = new String().getClass();
	HashMap labelsMap = new HashMap();

	private static final Object [] PARAM = new Object [0];
	
	
	Object value;
	
	/**
		Initialize the panel to correspond to the type klass.
		The actual value that is to be displayed should be
		passed using the setValue method.

		@see #setValue(Object)
	*/
	public ReflectionPanel (Class klass) {
		this.klass = klass;
		init();
	}
	
	/**
		Initializes the ReflectionPanel to display the object
		"value". "value" must be an instance of the class that
		the ReflectionPanel was initialized to in the
		constructor.
	*/
	public void setValue (Object value)  { 
		if (!klass.isInstance(value)) 
			throw new IllegalArgumentException("not an instance of: "+klass);

		String text = null;
		this.value = value;
		for (Iterator it = labelsMap.keySet().iterator(); it.hasNext();) {

			try {
				ReflectionLabel label = (ReflectionLabel)labelsMap.get(it.next());
				text = (String)label.getter.invoke(value, PARAM);
				label.field.setText(text);	

			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
				throw new IllegalArgumentException("iae?");
			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
				throw new IllegalArgumentException("ite?");	
			}
		}

	}

	private void init () {
		Method [] m = klass.getDeclaredMethods();
		sortGetters(m);
		sortSetters(m);
		constructGUI();
	}

	private void constructGUI () {
		setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
		ReflectionLabel label = null;
		for (Iterator it = labelsMap.keySet().iterator(); it.hasNext();) {
			label = (ReflectionLabel)labelsMap.get(it.next());
			label.field.setEditable (label.setter==null?false:true);
			add (label);	
		}
		
		
	}

//	private void addTextField (String name, boolean editable) {
//		JTextField field = new JTextField ();
//		field.setEditable (editable);
//		fieldPane.add (field);
//	}

//	private static boolean isIn (Object element, Object [] arr) {
//		for (int i=0; i!=arr.length; i++) {
//			if (element.equals(arr[i]))
//				return true;
//		} 
//		return false;
//	}

	private void sortSetters (Method [] m) {
		ReflectionLabel label = null;
		for (int i =0; i!= m.length; i++) {
			if (!Modifier.isPublic(m[i].getModifiers()))
				continue;
			if (!m[i].getName().startsWith("set"))
				continue;
			if (m[i].getParameterTypes().length!=1)
				continue;
			if (m[i].getParameterTypes()[0]!=STRING_CLASS)
				continue;
			if ((label=(ReflectionLabel)labelsMap.get(m[i].getName().substring("set".length())))==null)
				continue; // no getter
			label.setter = m[i];	
		}
		
	}

	private void sortGetters (Method [] m) {
		ReflectionLabel label = null;
		for (int i =0; i!= m.length; i++) {
			if (!Modifier.isPublic(m[i].getModifiers()))
				continue;
			if (!m[i].getName().startsWith("get"))
				continue;
			if (m[i].getParameterTypes().length!=0)
				continue;
			if (m[i].getReturnType()!=STRING_CLASS)
				continue;
			label = new ReflectionLabel (m[i].getName().substring("get".length()), this);
			label.getter = m[i];
			labelsMap.put(label.name, label);
			
		}
	}
	public static void main (String [] args) {
		JFrame frame = new JFrame ();
		
		Hallo hallo = new Hallo();
		Hallo1 h2 = new Hallo1();
		
			
		
		ReflectionPanel panel = new ReflectionPanel (Hallo.class);
		ReflectionPanel panel1 = new ReflectionPanel (Hallo1.class);
		
		panel.setValue (hallo);
		panel1.setValue (h2);

		JTabbedPane tPane = new JTabbedPane();
		tPane.add (panel);
		tPane.add (panel1);
		frame.getContentPane().add(tPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	

	
}

	class Hallo {
		public String getOne() {
			return "one";	
		}	
		public void setOne(String str) {
				
		}	
		public String getTwo() {
			return "two";	
		}
	}

	class Hallo1 {
		public String getOne1(){
			return "one1";	
		}	
		public String getTwo1() {
			return "two1";	
		}
	}

