package examiner;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import ng.appserver.templating.parser.model.PBasicNode;

public class PLElementDefinitionDetailPage extends WOComponent {

	public ElementDefinition selectedObject;
	public PBasicNode currentNode;

	public PLElementDefinitionDetailPage( WOContext context ) {
		super( context );
	}
}