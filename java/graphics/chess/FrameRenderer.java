package graphics;

import swing.*;
import java.awt.*;
import javax.swing.*;

public class FrameRenderer {
	Renderable r;
	String title;
	public FrameRenderer (String title,Renderable r) {
		this.title=title;
		this.r=r;
		go();
	}
	

	void go() {
		final Dimension dim = r.getSize();
		final Renderable r = this.r;
		
		MainFrame f = new MainFrame(title);
		
		f.add(new Canvas () {
			public void paint (Graphics g) {
				r.render((Graphics2D)g);		
			}	
			public Dimension getPreferredSize(){
				return dim;	
			}
		});
		
//		f.getContentPane().add(new Canvas () {
//			public void paint (Graphics g) {
//				r.render((Graphics2D)g);		
//			}	
//			public Dimension getPreferredSize(){
//				return dim;	
//			}
//		});
		f.show();
	}

	
}
