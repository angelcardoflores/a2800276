package html;

public interface PixelTableModel  {
	
	public int getHeight ();
	public int getWidth ();

	public int getPixelPerYPoint();
	public int getPixelPerXPoint();

	public String getColor (int x, int y);
	public String getText (int x, int y);
}
