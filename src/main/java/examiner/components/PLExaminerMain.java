package examiner.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.foundation.NSBundle;

import er.extensions.components.ERXComponent;
import examiner.BundleDefinition;
import examiner.DirectActionDefinition;
import examiner.DirectActions;
import examiner.ElementDefinition;
import examiner.ElementDefinitions;
import ng.appserver.templating.parser.model.PBasicNode;

// List Direct Action classes
// List Bundles
// List Request handlers

public class PLExaminerMain extends ERXComponent {

	public ElementDefinition currentElementDefinition;
	public DirectActionDefinition currentDirectAction;
	public RequestHandlerDefinition currentRequestHandler;
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

	public Map.Entry<String, Integer> entry;

	public List<Map.Entry<String, Integer>> entries() {
		Comparator<Entry<String, Integer>> c = Comparator.comparing( Map.Entry::getValue );
		c = c.reversed();

		return usedElements()
				.entrySet()
				.stream()
				.sorted( c )
				.toList();
	}

	public Map<String, Integer> usedElements() {
		final Map<String, Integer> map = new HashMap<>();

		for( final ElementDefinition elementDefinition : elementDefinitions() ) {
			for( PBasicNode node : elementDefinition.dynamicNodes() ) {
				map.merge( node.type(), 0, ( x, y ) -> x + 1 );
			}
		}

		return map;
	}

	// FIXME: Move to separate file
	public record RequestHandlerDefinition( String key, WORequestHandler requestHandler ) {}

	public List<RequestHandlerDefinition> requestHandlerDefinitions() {
		final List<RequestHandlerDefinition> l = new ArrayList<>();

		for( Object object : application().registeredRequestHandlerKeys() ) {
			final String key = object.toString();
			final WORequestHandler handler = application().requestHandlerForKey( key );
			l.add( new RequestHandlerDefinition( key, handler ) );
		}

		Collections.sort( l, Comparator.comparing( r -> r.requestHandler().getClass().getName() ) );

		return l;
	}
}