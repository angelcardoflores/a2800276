package dot;

public class StyleAttribute extends Attribute implements NodeAttribute, EdgeAttribute, GraphAttribute {
	public static final StyleAttribute SOLID = new StyleAttribute ("solid");
	public static final StyleAttribute DASHED = new StyleAttribute ("dashed");
	public static final StyleAttribute DOTTED = new StyleAttribute ("dotted");
	public static final StyleAttribute BOLD = new StyleAttribute ("bold");
	public static final StyleAttribute INVIS = new StyleAttribute ("invis");

	public static final StyleAttribute FILLED = new StyleAttribute ("filled");
	public static final StyleAttribute DIAGONALS = new StyleAttribute ("diagonals");
	public static final StyleAttribute ROUNDED = new StyleAttribute ("rounded");

	private StyleAttribute (String value) {
		super ("style", value);
	}

	/**
		use for user defined styles
	*/
	public static StyleAttribute getStyleAttribute (String value) {
		return new StyleAttribute ('"'+value+'"');	
	}
}
