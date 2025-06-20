package examiner;

import java.util.List;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

// List Direct Action classes
// List Bundles

public class PLElementary extends ERXComponent {

	public ElementDefinition currentElementDefinition;

	public PLElementary( WOContext context ) {
		super( context );
	}

	public WOActionResults view() {
		final PLElementDefinitionDetailPage nextPage = pageWithName( PLElementDefinitionDetailPage.class );
		nextPage.selectedObject = currentElementDefinition;
		return nextPage;
	}

	public List<ElementDefinition> elementDefinitions() {
		return ElementDefinitions.elementDefinitions();
	}
}