package html;

public class ClassicLeftRightNaviFrameset extends Frameset {
	
	public ClassicLeftRightNaviFrameset (HtmlDocument navi, HtmlDocument content) {
		add (new Frame (navi.getFileName(), "navigation"));
		add (new Frame (content.getFileName(), "content"));
		setVertical();
		setProportions ("15%,*");
	}
}
