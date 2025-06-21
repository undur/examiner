package examiner.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.ElementDefinition;
import ng.appserver.templating.parser.NGDeclaration;
import ng.appserver.templating.parser.NGDeclaration.NGBindingValue;
import ng.appserver.templating.parser.model.PBasicNode;

public class PLElementDefinitionDetailPage extends WOComponent {

	public ElementDefinition selectedObject;
	public PBasicNode currentNode;

	public PLElementDefinitionDetailPage( WOContext context ) {
		super( context );
	}

	public String currentBindingsString() {
		List<String> descriptions = new ArrayList<>();

		for( Entry<String, NGBindingValue> binding : currentNode.bindings().entrySet() ) {
			StringBuilder b = new StringBuilder();
			b.append( binding.getKey() );
			b.append( "=" );

			if( binding.getValue().isQuoted() ) {
				b.append( "\"%s\"".formatted( binding.getValue().value() ) );
			}
			else {
				b.append( "$" + binding.getValue().value() );
			}

			descriptions.add( b.toString() );
		}

		return String.join( ", ", descriptions );
	}

	/**
	 * @return The name of the declaration, if it's not an inline declaration
	 */
	public String currentDeclarationName() {
		final NGDeclaration d = currentNode.tag().declaration();
		return d.isInline() ? null : d.name();
	}
}