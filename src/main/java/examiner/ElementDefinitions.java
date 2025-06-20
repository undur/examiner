package examiner;

import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WOElement;

public class ElementDefinitions {

	public static List<ElementDefinition> elementDefinitions() {
		final List<ElementDefinition> list = new ArrayList<>();

		list.addAll( elementClasses()
				.stream()
				.map( ElementDefinition::new )
				.toList() );

		list.addAll( classlessComponents()
				.stream()
				.map( ElementDefinition::new )
				.toList() );

		return list;
	}

	private static List<Class<? extends WOElement>> elementClasses() {
		return Util.locateSubclassesOf( WOElement.class );
	}

	/**
	 * FIXME: Missing implementation of search for classless components
	 */
	public static List<String> classlessComponents() {
		return List.of();
	}
}