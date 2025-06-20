package examiner;

import java.util.List;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;

import er.extensions.components.ERXComponent;

// List Direct Action classes
// List Bundles

public class PLElementary extends ERXComponent {

	public ElementDefinition currentElementDefinition;
	public Class currentDirectActionClass;

	public PLElementary( WOContext context ) {
		super( context );
	}

	public WOActionResults viewElementDefinition() {
		final PLElementDefinitionDetailPage nextPage = pageWithName( PLElementDefinitionDetailPage.class );
		nextPage.selectedObject = currentElementDefinition;
		return nextPage;
	}

	public List<ElementDefinition> elementDefinitions() {
		return ElementDefinitions.elementDefinitions();
	}

	public List<Class<? extends WOElement>> directActionClasses() {
		return DirectActions.directActionClasses();
	}
}