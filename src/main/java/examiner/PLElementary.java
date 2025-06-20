package examiner;

import static org.reflections.scanners.Scanners.SubTypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;

import er.extensions.components.ERXComponent;

// List Direct Action classes
// List Bundles

public class PLElementary extends ERXComponent {

	public ElementDefinition currentElementDefinition;

	public PLElementary( WOContext context ) {
		super( context );
	}

	public List<Class<? extends WOElement>> elementClasses() {

		final Reflections reflections = new Reflections( new ConfigurationBuilder().setUrls( ClasspathHelper.forJavaClassPath() ).addScanners( new SubTypesScanner() ) );

		final Set elementClasses = reflections.get( SubTypes.of( WOElement.class ).asClass() );

		Comparator c1 = Comparator.comparing( ( Class c ) -> c.getPackageName() );
		Comparator c2 = Comparator.comparing( ( Class c ) -> c.getSimpleName() );

		List sorted = elementClasses
				.stream()
				.sorted( c1.thenComparing( c2 ) )
				.toList();

		return new ArrayList<>( sorted );
	}

	public WOActionResults view() {
		final PLElementDefinitionDetailPage nextPage = pageWithName( PLElementDefinitionDetailPage.class );
		nextPage.selectedObject = currentElementDefinition;
		return nextPage;
	}

	/**
	 * FIXME: Missing implementation of search for classless components
	 */
	public List<String> classlessComponents() {
		return List.of();
	}

	public List<ElementDefinition> elementDefinitions() {
		final List<ElementDefinition> list = new ArrayList<>();

		list.addAll( elementClasses()
				.stream()
				.map( ElementDefinition::new )
				.toList() );

		list.addAll( classlessComponents()
				.stream()
				.map( ElementDefinition::new )
				.toList() );

		return list;
	}
}