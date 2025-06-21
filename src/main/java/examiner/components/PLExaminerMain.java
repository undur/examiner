package examiner.components;

import java.util.List;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSBundle;

import er.extensions.components.ERXComponent;
import examiner.BundleDefinition;
import examiner.DirectActions;
import examiner.DirectActions.DirectActionDefinition;
import examiner.ElementDefinition;
import examiner.ElementDefinitions;

// List Direct Action classes
// List Bundles

public class PLExaminerMain extends ERXComponent {

	public ElementDefinition currentElementDefinition;
	public DirectActionDefinition currentDirectAction;
	public BundleDefinition currentBundle;

	private List<ElementDefinition> _elementDefinitions;

	public PLExaminerMain( WOContext context ) {
		super( context );
	}

	public List<ElementDefinition> elementDefinitions() {
		if( _elementDefinitions == null ) {
			_elementDefinitions = ElementDefinitions.elementDefinitions();

		}
		return _elementDefinitions;
	}

	public List<DirectActionDefinition> directActionDefinitions() {
		return DirectActions.directActionDefinitions();
	}

	public List<BundleDefinition> bundleDefinitions() {
		return NSBundle
				._allBundlesReally()
				.stream()
				.map( BundleDefinition::new )
				.toList();
	}

	public WOActionResults viewElementDefinition() {
		final PLElementDefinitionDetailPage nextPage = pageWithName( PLElementDefinitionDetailPage.class );
		nextPage.selectedObject = currentElementDefinition;
		return nextPage;
	}
}