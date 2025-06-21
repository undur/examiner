package examiner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WOElement;

public class DirectActions {

	public static List<Class<? extends WOElement>> directActionClasses() {
		return Util.locateSubclassesOf( WODirectAction.class );
	}

	public record DirectActionDefinition( Class<? extends WODirectAction> directActionClass, List<Method> directActionMethods ) {

		DirectActionDefinition( Class<? extends WODirectAction> directActionClass ) {
			this( directActionClass, actionMethods( directActionClass ) );
		}
	}

	private static List<Method> actionMethods( Class clazz ) {
		final List<Method> list = new ArrayList<>();

		for( final Method method : clazz.getDeclaredMethods() ) {
			// FIXME: Should we bother checking for the return type and access specifiers as well? // Hugi 2025-06-21
			if( method.getName().endsWith( "Action" ) ) {
				list.add( method );
			}
		}

		return list;
	}
}