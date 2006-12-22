package timeline;

import java.awt.*;
import javax.swing.*;
import swing.*;

public class Timeline {
	
		
	public Timeline (int from, int to, int steps) {
		setTo(to);
		setFrom(from);
		setSteps(steps);
	}

	/************************************************************************
		Fields Definitions
	************************************************************************/

	/** 
		first year
	*/
	private int to;

	/** 
		last year
	*/
	private int from;

	/** 
		number of steps
	*/
	private int steps;

	/************************************************************************
		Getter Methods
	************************************************************************/

	/** 
		getter method for <code>to</code>
		@see #to
	*/
	public int getTo () {
		return this.to;
	}

	/** 
		getter method for <code>from</code>
		@see #from
	*/
	public int getFrom () {
		return this.from;
	}

	/** 
		getter method for <code>steps</code>
		@see #steps
	*/
	public int getSteps () {
		return this.steps;
	}

	public int getTicks () {
		return (getFrom()-getTo())/steps;
	}

	/************************************************************************
		Setter Methods
	************************************************************************/

	/** 
		setter method for <code>to</code>
		@see #to
	*/
	public void setTo (int to) {
		this.to=to;
	}

	/** 
		setter method for <code>from</code>
		@see #from
	*/
	public void setFrom (int from) {
		this.from=from;
	}

	/** 
		setter method for <code>steps</code>
		@see #steps
	*/
	public void setSteps (int steps) {
		this.steps=steps;
	}

	public void render (Graphics2D gr){
		int x = 10;
		int y0 = 10;
		int y1 = 1000;
		
		int unitsPerYear = (y1-y0)/(getTo()-getFrom());

		gr.drawLine (x,y0,x,y1);
		gr.drawString (Integer.toString(getFrom()),x,y0); 
		gr.drawString (Integer.toString(getTo()),x,y1); 
	}

	public static void main(String [] args) {
		
		final Timeline time = new Timeline (2000, 1900, 50);
		
		JFrame frame = new JFrame("Test");
		JScrollPane pane = new JScrollPane ( new JPanel () {
			public void paint(Graphics g) {
				time.render((Graphics2D)g);		
			}		
		});
		frame.getContentPane().add(pane);
		frame.pack();
		frame.setSize(new Dimension(550,100));
	        frame.show();
	}

	


}
