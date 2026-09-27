package examiner.components;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.DirectActionDefinition;
import examiner.Examination;

/**
 * The direct action classes and their actions
 */
public class PLDirectActionsPage extends WOComponent {

	public DirectActionDefinition currentDirectAction;

	public PLDirectActionsPage( WOContext context ) {
		super( context );
	}

	public Examination examination() {
		return Examination.current();
	}

	/**
	 * @return The current class's action names (without the "Action" suffix), as they appear in URLs
	 */
	public String currentActionNames() {
		return currentDirectAction
				.directActionMethods()
				.stream()
				.map( Method::getName )
				.map( n -> n.substring( 0, n.length() - "Action".length() ) )
				.sorted()
				.collect( Collectors.joining( ", " ) );
	}
}
