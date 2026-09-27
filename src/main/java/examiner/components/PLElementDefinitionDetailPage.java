package examiner.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.ElementDefinition;
import ng.appserver.templating.parser.NGDeclaration.NGBindingValue;
import ng.appserver.templating.parser.model.PBasicNode;

public class PLElementDefinitionDetailPage extends WOComponent {

	public ElementDefinition selectedObject;
	public PBasicNode currentNode;

	public PLElementDefinitionDetailPage( WOContext context ) {
		super( context );
	}

	public String currentBindingsString() {
		final List<String> descriptions = new ArrayList<>();

		for( Entry<String, NGBindingValue> binding : currentNode.bindings().entrySet() ) {
			descriptions.add( switch( binding.getValue() ) {
				case NGBindingValue.Value v -> binding.getKey() + "=" + (v.isQuoted() ? "\"" + v.value() + "\"" : v.value());
				case NGBindingValue.BooleanPresence b -> binding.getKey();
			} );
		}

		return String.join( ", ", descriptions );
	}

	/**
	 * @return The name of the declaration, if it's not an inline declaration
	 */
	public String currentDeclarationName() {
		return currentNode.isInline() ? null : currentNode.declarationName();
	}
}