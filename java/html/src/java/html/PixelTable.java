package html;

public class PixelTable extends Table {
	
	PixelTableModel model;
	
	public PixelTable (PixelTableModel model) {
		setCellspacing (0);
		setCellpadding (0);
		setHeight (model.getHeight()*model.getPixelPerYPoint());
		setWidth (model.getWidth()*model.getPixelPerXPoint());
		
		this.model = model;
		
		render();
	}

	private void render () {
		for (int y=0; y!=model.getHeight(); y++) {
			Tag tr = new Tag ("tr");
			tr.add (new Attribute("width",model.getWidth()*model.getPixelPerXPoint()));
			tr.add (new Attribute("height", model.getPixelPerYPoint()));
			
			for (int x = 0; x!=model.getWidth(); x++) {
				Tag td = new Tag ("td");
				td.add (new Attribute ("bgcolor", model.getColor(x,y)));
			td.add (new Attribute("width",model.getPixelPerXPoint()));
			td.add (new Attribute("height", model.getPixelPerYPoint()));
				Img img = new Img ("pixel.gif", model.getText(x,y));
				img.setHeight (model.getPixelPerYPoint());
				img.setWidth (model.getPixelPerXPoint());
				td.add (img);
				tr.add(td);
			}
			add(tr);
		}	
	}
	
}
