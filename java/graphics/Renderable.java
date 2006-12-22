package graphics;
 
import java.awt.*;
/**
	Describes something that can be drawn onto
	a Graphics2D object;
*/
public interface Renderable {
		public Dimension getSize();
		public void setSize(Dimension d);
		public void render (Graphics2D gr);
}
