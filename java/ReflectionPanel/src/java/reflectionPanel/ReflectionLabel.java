package reflectionPanel;



import javax.swing.*;
import java.lang.reflect.*;
import java.awt.event.*;
import java.awt.*;

public class ReflectionLabel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -559137094742792002L;

	String name;
	
	Method getter;
	Method setter;
	
	JTextField field;
	ReflectionPanel parent;

	ReflectionLabel (String label, ReflectionPanel parent) {
		this.parent = parent;
		this.name = label;
		setField(new JTextField());
		setLayout (new GridLayout (1,0));
		add (new JLabel(label));
		add (field);
	}

	void setField (JTextField field) {
		this.field = field;
		field.addActionListener (
			new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					JTextField f = (JTextField)e.getSource();
					String [] str = new String [1];
					str[0] = f.getText();
					//System.out.println (parent.value);
					if (parent.value == null)
						return;
					try {
						setter.invoke(parent.value, str);	
					} catch (Throwable t) {
						t.printStackTrace();
						System.exit(1);
					}
					
				}		
			}
		
		); // setActionListener
	}
}
