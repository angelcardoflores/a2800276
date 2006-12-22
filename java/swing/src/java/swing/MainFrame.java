package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JScrollPane pane;

	public MainFrame () {
		this("Give the frame a title, slob!");			
	}
	public MainFrame (String title){
		super (title);	
		this.addWindowListener(new WindowAdapter () {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}	
		});
	}

	public Component add (Component comp) {
		return getContentPane().add(comp); 

	}

	public Component superAdd (Component comp) {
		return super.add(comp);	
	}

	public void show () {
		pack();
		super.show();
	}

	public static void main (String [] args) {
		MainFrame f = new MainFrame ("MyTitle");
		f.add (new JButton());
		f.show();
	}

}
