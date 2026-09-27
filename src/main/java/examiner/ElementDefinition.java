package examiner;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WODynamicElement;
import com.webobjects.appserver.WOElement;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation._NSUtilities;

import ng.appserver.templating.parser.NGDeclarationFormatException;
import ng.appserver.templating.parser.NGHTMLFormatException;
import ng.appserver.templating.parser.NGTemplateParser;
import ng.appserver.templating.parser.model.PBasicNode;
import ng.appserver.templating.parser.model.PNode;
import ng.appserver.templating.parser.model.PRootNode;

/**
 * An element or component found on the classpath, with its template (if it has one), read and parsed once.
 */
public final class ElementDefinition {

	public enum ElementType {
		Element,
		DynamicElement,
		Component,
		ClasslessComponent,
		Unknown;
	}

	private final String _name;
	private final Class<? extends WOElement> _elementClass;

	private boolean _templateRead;
	private String _htmlString;
	private String _wodString;

	private boolean _templateParsed;
	private PRootNode _template;
	private String _parseError;
	private int _parseErrorLine = -1;
	private int _parseErrorColumn = -1;

	private List<PBasicNode> _dynamicNodes;

	ElementDefinition( Class<? extends WOElement> elementClass ) {
		this( elementClass.getSimpleName(), elementClass );
	}

	ElementDefinition( String name ) {
		this( name, null );
	}

	private ElementDefinition( String name, Class<? extends WOElement> elementClass ) {
		_name = name;
		_elementClass = elementClass;
	}

	public String name() {
		return _name;
	}

	public Class<? extends WOElement> elementClass() {
		return _elementClass;
	}

	public NSBundle bundle() {
		return _elementClass == null ? null : NSBundle.bundleForClass( _elementClass );
	}

	public String htmlString() {
		readTemplate();
		return _htmlString;
	}

	public String wodString() {
		readTemplate();
		return _wodString;
	}

	/**
	 * @return true if the element has a template (an .html file in a .wo folder)
	 */
	public boolean hasTemplate() {
		return htmlString() != null;
	}

	private void readTemplate() {
		if( !_templateRead ) {
			_htmlString = templateFileString( "html" );
			_wodString = templateFileString( "wod" );
			_templateRead = true;
		}
	}

	/**
	 * @return The contents of the component's template file with the given extension, or null if there's none
	 */
	private String templateFileString( final String extension ) {
		final NSBundle bundle = bundle();

		if( bundle == null ) {
			return null;
		}

		// First, check the most common way of looking for a combined template
		String path = "%s.wo/%s.%s".formatted( name(), name(), extension );
		URL url = bundle.pathURLForResourcePath( path );

		// If we didn't find anything, try looking in NonLocalized.lproj
		// FIXME: We're currently not handling localized components. Might want to consider that
		if( url == null ) {
			path = "NonLocalized.lproj/%s.wo/%s.%s".formatted( name(), name(), extension );
			url = bundle.pathURLForResourcePath( path );
		}

		if( url == null ) {
			return null;
		}

		try( InputStream is = url.openStream() ) {
			return new String( is.readAllBytes() );
		}
		catch( IOException e ) {
			throw new UncheckedIOException( e );
		}
	}

	public String replacementElement() {
		final Class clazz = _NSUtilities.classWithName( name() );

		if( clazz == null ) {
			return null;
		}

		// Same element
		if( clazz.getSimpleName().equals( name() ) ) {
			return null;
		}

		String replacementElementName = clazz.getTypeName();

		// Remove the package name (simpleName will only return the name of the internal class, which isn't really helpful)
		final int lastPeriodIndex = replacementElementName.lastIndexOf( '.' );

		if( lastPeriodIndex != -1 ) {
			replacementElementName = replacementElementName.substring( lastPeriodIndex + 1 );
		}

		return replacementElementName;
	}

	public ElementType type() {

		if( elementClass() == null ) {
			return ElementType.ClasslessComponent;
		}

		if( WOComponent.class.isAssignableFrom( elementClass() ) ) {
			return ElementType.Component;
		}

		if( WODynamicElement.class.isAssignableFrom( elementClass() ) ) {
			return ElementType.DynamicElement;
		}

		if( WOElement.class.isAssignableFrom( elementClass() ) ) {
			return ElementType.Element;
		}

		throw new IllegalArgumentException( "Unknown element type: " + elementClass() );
	}

	/**
	 * FIXME: Implement
	 */
	public boolean isClassless() {
		return false;
	}

	/**
	 * @return The parsed template, or null if the element has none or it failed to parse (see {@link #parseError()})
	 */
	public PRootNode template() {
		parseTemplate();
		return _template;
	}

	/**
	 * @return The parser's message if the template failed to parse, otherwise null
	 */
	public String parseError() {
		parseTemplate();
		return _parseError;
	}

	/**
	 * @return The line of the parse error in the HTML template, or -1 if unknown
	 */
	public int parseErrorLine() {
		parseTemplate();
		return _parseErrorLine;
	}

	/**
	 * @return The column of the parse error in the HTML template, or -1 if unknown
	 */
	public int parseErrorColumn() {
		parseTemplate();
		return _parseErrorColumn;
	}

	/**
	 * @return true if the element has a template that parsed
	 */
	public boolean parses() {
		return template() != null;
	}

	private void parseTemplate() {
		if( _templateParsed ) {
			return;
		}

		_templateParsed = true;

		if( !hasTemplate() ) {
			return;
		}

		try {
			// A component with an inline template has no .wod
			final String wod = wodString() != null ? wodString() : "";
			_template = (PRootNode)new NGTemplateParser( htmlString(), wod ).parse();
		}
		catch( NGHTMLFormatException e ) {
			_parseError = e.getMessage();
			_parseErrorLine = e.line();
			_parseErrorColumn = e.column();
		}
		catch( NGDeclarationFormatException | RuntimeException e ) {
			_parseError = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
		}
	}

	/**
	 * @return A list of all nodes in the template that represent dynamic elements
	 */
	public List<PBasicNode> dynamicNodes() {

		if( _dynamicNodes == null ) {
			_dynamicNodes = new ArrayList<>();

			if( template() != null ) {
				collectBasicNodes( template(), _dynamicNodes );
			}
		}

		return _dynamicNodes;
	}

	private static void collectBasicNodes( PNode node, List<PBasicNode> result ) {

		// FIXME. Sucky sucky, checking twice for child-containing types. We should really have a "hasChildren" interface on the nodes or something // Hugi 2025-06-20

		if( node instanceof PRootNode parent ) {
			for( PNode child : parent.children() ) {
				collectBasicNodes( child, result );
			}
		}

		if( node instanceof PBasicNode current ) {
			result.add( current );
			for( PNode child : current.children() ) {
				collectBasicNodes( child, result );
			}
		}
	}
}
