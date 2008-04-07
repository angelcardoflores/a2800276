package dot;

public class Node extends AttributedElement {

	public Node (String name) {
		if (name == null) throw new RuntimeException("Bullshit!");
		setName(name);
	}

	public Node (String name, String label) {
		setName(name);
		setLabel(label);
	}

	public String pack () {
		StringBuffer buf = new StringBuffer();
		buf.append ("\""+getName()+"\"");
		if (getAttributes()!=null) {
			buf.append (" [");
			for (ElementIterator i = new ElementIterator(getAttributes().iterator());i.hasNext();){
				buf.append (i.nextElement().pack());
				if (i.hasNext()){
					buf.append (',');
				}
			}

			buf.append("]");
		}
		buf.append (";\n");
		return buf.toString();

	}
}
