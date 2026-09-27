package examiner.components;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.Examination;
import examiner.Examination.RequestHandlerDefinition;

/**
 * The registered request handlers
 */
public class PLRequestHandlersPage extends WOComponent {

	public RequestHandlerDefinition currentHandler;

	public PLRequestHandlersPage( WOContext context ) {
		super( context );
	}

	public Examination examination() {
		return Examination.current();
	}

	public boolean currentIsDefault() {
		return currentHandler.requestHandler() == application().defaultRequestHandler();
	}
}
