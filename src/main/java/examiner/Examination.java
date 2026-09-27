package examiner;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Objects;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation._NSUtilities;

import ng.appserver.templating.parser.model.PBasicNode;
import parsley.ParsleyTagRegistry;

/**
 * One examination of the running application: everything the dashboard shows, collected once (scanning the classpath
 * and parsing every template takes a moment) and kept until the next {@link #rescan()}.
 *
 * Lists are handed out as ArrayLists: templates call size() and isEmpty() on them through KVC, which can't invoke methods
 * of the JDK's non-public list classes (Stream.toList()) unless the host opens java.util.
 */
public class Examination {

	private static Examination _current;

	private final Instant _createdAt = Instant.now();
	private final Duration _duration;

	private final List<ElementDefinition> _elementDefinitions;
	private final List<DirectActionDefinition> _directActionDefinitions;
	private final List<BundleDefinition> _bundleDefinitions;
	private final List<RequestHandlerDefinition> _requestHandlerDefinitions;
	private final List<ElementUsage> _elementUsages;
	private final List<ElementUsage> _allElementUsages;

	/**
	 * A request handler and the key it's registered under
	 */
	public record RequestHandlerDefinition( String key, WORequestHandler requestHandler ) {}

	/**
	 * An element name as written in templates, what it resolves to, and the templates using it
	 *
	 * @param name The element name as written in the templates (a tag alias, a short name or a full name)
	 * @param resolvedName The element name after Parsley's tag aliases
	 * @param elementClass The class the name resolves to at runtime, or null if none was found
	 * @param count The number of uses across all templates
	 * @param users The components whose templates use it
	 */
	public record ElementUsage( String name, String resolvedName, Class<?> elementClass, int count, List<ElementDefinition> users ) {

		public boolean isResolved() {
			return elementClass != null;
		}

		public boolean isAlias() {
			return !name.equals( resolvedName );
		}

		public NSBundle bundle() {
			return elementClass == null ? null : NSBundle.bundleForClass( elementClass );
		}
	}

	@SuppressWarnings("unchecked")
	private Examination() {
		final Instant start = Instant.now();
		// Examiner doesn't examine itself
		_elementDefinitions = ElementDefinitions.elementDefinitions().stream().filter( e -> !isExaminers( e ) ).collect( Collectors.toCollection( ArrayList::new ) );
		_directActionDefinitions = DirectActions.directActionDefinitions();
		_bundleDefinitions = ((List<NSBundle>)NSBundle._allBundlesReally()).stream().map( BundleDefinition::new ).sorted( Comparator.comparing( ( BundleDefinition b ) -> !b.isMain() ).thenComparing( b -> b.bundle().name() ) ).collect( Collectors.toCollection( ArrayList::new ) );
		_requestHandlerDefinitions = requestHandlers();
		_elementUsages = elementUsages( applicationComponents() );
		_allElementUsages = elementUsages( components() );
		_duration = Duration.between( start, Instant.now() );
	}

	/**
	 * @return The current examination, made on first use
	 */
	public static synchronized Examination current() {
		if( _current == null ) {
			_current = new Examination();
		}

		return _current;
	}

	/**
	 * Examines the application again, for changes made since (in development, with templates edited)
	 */
	public static synchronized Examination rescan() {
		_current = new Examination();
		return _current;
	}

	public Instant createdAt() {
		return _createdAt;
	}

	public long durationMillis() {
		return _duration.toMillis();
	}

	public List<ElementDefinition> elementDefinitions() {
		return _elementDefinitions;
	}

	/**
	 * @return The components that have a template, the main bundle's first
	 */
	public List<ElementDefinition> components() {
		return _elementDefinitions
				.stream()
				.filter( ElementDefinition::hasTemplate )
				.sorted( Comparator.comparing( ( ElementDefinition e ) -> !isInMainBundle( e ) ).thenComparing( e -> bundleName( e ) ).thenComparing( ElementDefinition::name ) )
				.collect( Collectors.toCollection( ArrayList::new ) );
	}

	/**
	 * @return The components of the application itself (its main bundle)
	 */
	public List<ElementDefinition> applicationComponents() {
		return components().stream().filter( Examination::isInMainBundle ).collect( Collectors.toCollection( ArrayList::new ) );
	}

	/**
	 * @return The components whose template doesn't parse
	 */
	public List<ElementDefinition> parseFailures() {
		return components().stream().filter( e -> e.parseError() != null ).collect( Collectors.toCollection( ArrayList::new ) );
	}

	public List<DirectActionDefinition> directActionDefinitions() {
		return _directActionDefinitions;
	}

	public List<BundleDefinition> bundleDefinitions() {
		return _bundleDefinitions;
	}

	public List<RequestHandlerDefinition> requestHandlerDefinitions() {
		return _requestHandlerDefinitions;
	}

	/**
	 * @return The elements used in the application's own templates, most used first
	 */
	public List<ElementUsage> elementUsages() {
		return _elementUsages;
	}

	/**
	 * @return The elements used in all templates, the frameworks' included, most used first
	 */
	public List<ElementUsage> allElementUsages() {
		return _allElementUsages;
	}

	/**
	 * @return The element names that don't resolve to a class at runtime
	 */
	public List<ElementUsage> unresolvedElementUsages() {
		return _elementUsages.stream().filter( u -> !u.isResolved() ).collect( Collectors.toCollection( ArrayList::new ) );
	}

	public ElementDefinition elementDefinitionNamed( final String name ) {
		return _elementDefinitions.stream().filter( e -> e.name().equals( name ) ).findFirst().orElse( null );
	}

	/**
	 * @return The element's uses across all templates
	 */
	public ElementUsage elementUsageNamed( final String name ) {
		return _allElementUsages.stream().filter( e -> e.name().equals( name ) ).findFirst().orElse( null );
	}

	/**
	 * @return The main bundle's name: the examined application's
	 */
	public String applicationName() {
		return NSBundle.mainBundle().name();
	}

	public static boolean isInMainBundle( final ElementDefinition e ) {
		return Objects.equals( e.bundle(), NSBundle.mainBundle() );
	}

	private static boolean isExaminers( final ElementDefinition e ) {
		return e.elementClass() != null && e.elementClass().getName().startsWith( "examiner." );
	}

	private static String bundleName( final ElementDefinition e ) {
		return e.bundle() == null ? "" : e.bundle().name();
	}

	private static List<RequestHandlerDefinition> requestHandlers() {
		final List<RequestHandlerDefinition> result = new ArrayList<>();
		final WOApplication application = WOApplication.application();

		for( Object object : application.registeredRequestHandlerKeys() ) {
			final String key = object.toString();
			result.add( new RequestHandlerDefinition( key, application.requestHandlerForKey( key ) ) );
		}

		result.sort( Comparator.comparing( RequestHandlerDefinition::key ) );
		return result;
	}

	private static List<ElementUsage> elementUsages( final Collection<ElementDefinition> definitions ) {
		final Map<String, List<ElementDefinition>> users = new LinkedHashMap<>();
		final Map<String, Integer> counts = new LinkedHashMap<>();

		for( final ElementDefinition definition : definitions ) {
			for( final PBasicNode node : definition.dynamicNodes() ) {
				counts.merge( node.type(), 1, Integer::sum );
				final List<ElementDefinition> list = users.computeIfAbsent( node.type(), k -> new ArrayList<>() );

				if( !list.contains( definition ) ) {
					list.add( definition );
				}
			}
		}

		return counts
				.entrySet()
				.stream()
				.map( e -> {
					final String resolvedName = ParsleyTagRegistry.resolve( e.getKey() );
					final Class<?> elementClass = _NSUtilities.classWithName( resolvedName );
					return new ElementUsage( e.getKey(), resolvedName, elementClass, e.getValue(), users.get( e.getKey() ) );
				} )
				.sorted( Comparator.comparing( ElementUsage::count ).reversed().thenComparing( ElementUsage::name ) )
				.collect( Collectors.toCollection( ArrayList::new ) );
	}
}
