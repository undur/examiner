package examiner.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.ElementDefinition;
import ng.appserver.templating.parser.NGDeclaration.NGBindingValue;
import ng.appserver.templating.parser.model.PBasicNode;

/**
 * One component: its template, whether it parses, and the elements it uses
 */
public class PLElementDefinitionDetailPage extends WOComponent {

	/**
	 * A line of a source file
	 */
	public record Line( int number, String text ) {}

	public ElementDefinition selectedObject;
	public PBasicNode currentNode;
	public Line currentLine;

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

	public boolean hasWod() {
		return selectedObject.wodString() != null && !selectedObject.wodString().isBlank();
	}

	public String templateKind() {
		if( !selectedObject.hasTemplate() ) {
			return "None";
		}

		return hasWod() ? "HTML + wod" : "Inline";
	}

	/**
	 * @return The line of the parse error, or null if unknown
	 */
	public Integer errorLine() {
		return selectedObject.parseErrorLine() > 0 ? selectedObject.parseErrorLine() : null;
	}

	public List<Line> htmlLines() {
		return lines( selectedObject.htmlString() );
	}

	public List<Line> wodLines() {
		return lines( selectedObject.wodString() );
	}

	public String currentLineClass() {
		return errorLine() != null && currentLine.number() == errorLine() ? "line error" : "line";
	}

	private static List<Line> lines( final String source ) {
		final List<Line> lines = new ArrayList<>();

		if( source != null ) {
			final String[] split = source.split( "\r?\n", -1 );

			for( int i = 0; i < split.length; i++ ) {
				lines.add( new Line( i + 1, split[i] ) );
			}
		}

		return lines;
	}
}
