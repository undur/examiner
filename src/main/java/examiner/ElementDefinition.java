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
import ng.appserver.templating.parser.model.PGroupNode;
import ng.appserver.templating.parser.model.PNode;

public record ElementDefinition( String name, Class<? extends WOElement> elementClass ) {

	public enum ElementType {
		Element,
		DynamicElement,
		Component,
		ClasslessComponent,
		Unknown;
	}

	ElementDefinition( Class<? extends WOElement> elementClass ) {
		this( elementClass.getSimpleName(), elementClass );
	}

	ElementDefinition( String name ) {
		this( name, null );
	}

	public NSBundle bundle() {
		return NSBundle.bundleForClass( elementClass() );
	}

	public String htmlString() {
		final NSBundle bundle = bundle();

		if( bundle == null ) {
			return null;
		}

		// First, check the most common way of looking for a combined template
		String path = "%s.wo/%s.html".formatted( name(), name() );
		URL url = bundle.pathURLForResourcePath( path );

		// If we didn't find anything, try looking in NonLocalized.lproj
		// FIXME: We're currently not handling localized components. Might want to consider that
		if( url == null ) {
			path = "NonLocalized.lproj/%s.wo/%s.html".formatted( name(), name() );
			url = bundle.pathURLForResourcePath( path );
		}

		if( url != null ) {
			try( InputStream is = url.openStream()) {
				return new String( is.readAllBytes() );
			}
			catch( IOException e ) {
				throw new UncheckedIOException( e );
			}
		}

		return null;
	}

	public String wodString() {
		final NSBundle bundle = bundle();

		if( bundle == null ) {
			return null;
		}

		// First, check the most common way of looking for a combined template
		String path = "%s.wo/%s.wod".formatted( name(), name() );
		URL url = bundle.pathURLForResourcePath( path );

		// If we didn't find anything, try looking in NonLocalized.lproj
		// FIXME: We're currently not handling localized components. Might want to consider that
		if( url == null ) {
			path = "NonLocalized.lproj/%s.wo/%s.wod".formatted( name(), name() );
			url = bundle.pathURLForResourcePath( path );
		}

		if( url != null ) {
			try( InputStream is = url.openStream()) {
				return new String( is.readAllBytes() );
			}
			catch( IOException e ) {
				throw new UncheckedIOException( e );
			}
		}

		return null;
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
		// single-file template
		// htmlString
		// wodString
		// apiString
	}

	/**
	 * FIXME: Implement
	 */
	public boolean isClassless() {
		return false;
	}

	public PGroupNode template() {

		if( htmlString() == null ) {
			return null;
		}

		try {
			return (PGroupNode)new NGTemplateParser( htmlString(), wodString() ).parse();
		}
		catch( NGDeclarationFormatException | NGHTMLFormatException e ) {
			throw new RuntimeException( e );
		}
	}

	/**
	 * @return A list of all nodes in the template that represent dynamic elements
	 */
	public List<PBasicNode> dynamicNodes() {

		if( template() == null ) {
			return new ArrayList<>();
		}

		final List<PBasicNode> nodes = new ArrayList<>();
		collectBasicNodes( template(), nodes );
		return nodes;
	}

	private void collectBasicNodes( PNode node, List<PBasicNode> result ) {

		// FIXME. Sucky sucky, checking twice for child-containing types. We should really have a "hasChildren" interface on the nodes or something // Hugi 2025-06-20

		if( node instanceof PGroupNode parent ) {
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