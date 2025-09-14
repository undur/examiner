package examiner;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WODirectAction;
import com.webobjects.foundation.NSBundle;

public record DirectActionDefinition( Class<? extends WODirectAction> directActionClass, List<Method> directActionMethods ) {

	public DirectActionDefinition( Class<? extends WODirectAction> directActionClass ) {
		this( directActionClass, actionMethods( directActionClass ) );
	}

	public NSBundle bundle() {
		return NSBundle.bundleForClass( directActionClass() );
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

	public boolean isAbstract() {
		return Modifier.isAbstract( directActionClass().getModifiers() );
	}
}