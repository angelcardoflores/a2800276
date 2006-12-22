package graphics.chess;

import graphics.*;
import java.awt.*;
import javax.swing.*;
public class Board implements Renderable{
	
	Dimension size;
	
	public Board (Dimension size) {
		this.size = size;	
	}
	
	public Dimension getSize() {
		return size;	
	}

	public void setSize(Dimension size) {
		this.size=size;	
	}
	public int getHeight () {
		return (int)size.getHeight();	
	}
	public int getWidth () {
		return (int)size.getWidth();	
	}
	
	public int getSquareHeight(){
		return (int)((size.getHeight()-getMargin()*2)/8);
	}
	public int getSquareWidth(){
		return (int)((size.getWidth()-getMargin()*2)/8);
	}

	public int getMargin(){
		return 2;	
	}
	
	public void render (Graphics2D gr) {
		boolean white = true;
		gr.setColor(Color.gray);
		gr.fillRect(0,0,getWidth(), getHeight());
		for (int row=0; row!=8; ++row){
			for (int col=0; col!=8; ++col){
				//startswith white
				Color c = white?Color.white:Color.black;
				white = !white;
				gr.setColor(c);
				gr.fillRect (
					col*getSquareWidth()+getMargin(),
					row*getSquareHeight()+getMargin(),
					getSquareWidth(), 
					getSquareHeight()
				);
			}	
			white = !white;
		}
	}

	public static void main(String [] args) {/*
		final Dimension dim = new Dimension (500,500);
		final Board b = new Board(dim);
		
		JFrame f = new JFrame("BoardTest");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new Canvas () {
			public void paint (Graphics g) {
				b.render((Graphics2D)g);		
			}	
			public Dimension getPreferredSize(){
				return dim;	
			}
		});
		f.pack();
		f.show();
		*/
		FrameRenderer fr = new FrameRenderer ("Board", new Board(new Dimension(150,150)));
	}
}
