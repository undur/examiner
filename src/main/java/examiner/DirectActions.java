package examiner;

import java.util.List;

import com.webobjects.appserver.WODirectAction;

public class DirectActions {

	public static List<Class<? extends WODirectAction>> directActionClasses() {
		return Util.locateSubclassesOf( WODirectAction.class );
	}

	public static List<DirectActionDefinition> directActionDefinitions() {
		return directActionClasses()
				.stream()
				.map( DirectActionDefinition::new )
				.toList();
	}
}