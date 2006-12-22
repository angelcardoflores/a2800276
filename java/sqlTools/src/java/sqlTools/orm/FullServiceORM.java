package sqlTools.orm;

import java.lang.reflect.*;
import java.util.*;

public abstract class FullServiceORM extends ReflectionORMObject {
	
	
	protected OneToOneRelation[] getOneToOneRelations() {
		if (rel121 == null) {
			initRelations();	
		}
		return rel121;		
	}
	private OneToOneRelation [] rel121; 
	private static final Object [] ONE_TO_ONE_ARR = new OneToOneRelation[0];

	private void initRelations() {
		Field [] fields = this.getClass().getDeclaredFields();
		LinkedList oneToOneRel = new LinkedList();
		for (int i=0; i!=fields.length; ++i) {

			if (ORMObject.class.isAssignableFrom(fields[i].getType())) {
				oneToOneRel.add(new ReflectionOneToOneRelation(fields[i]));
			}
		}
		
		rel121 = (OneToOneRelation[])oneToOneRel.toArray(ONE_TO_ONE_ARR);	
		//OR ARRAY...
		// TODO this is a joined table...
		
	}
}
