package examiner.components;

import java.util.List;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSBundle;

import examiner.Examination;

/**
 * The dashboard's page layout: the examined application's name, the section navigation and a page header.
 *
 * Bindings: [section] the key of the active section, [title] the page title, [pretitle] and [subtitle] optional lines
 * above and below it.
 */
public class PLLook extends WOComponent {

	/**
	 * A section of the dashboard
	 */
	public record NavItem( String key, String label, String directActionName ) {}

	private static final List<NavItem> NAV_ITEMS = List.of(
			new NavItem( "overview", "Overview", "default" ),
			new NavItem( "components", "Components", "components" ),
			new NavItem( "elements", "Elements", "elements" ),
			new NavItem( "directActions", "Direct actions", "directActions" ),
			new NavItem( "requestHandlers", "Request handlers", "requestHandlers" ),
			new NavItem( "bundles", "Bundles", "bundles" ) );

	public NavItem currentNavItem;

	public PLLook( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	public Examination examination() {
		return Examination.current();
	}

	public List<NavItem> navItems() {
		return NAV_ITEMS;
	}

	public String currentNavItemClass() {
		return currentNavItem.key().equals( valueForBinding( "section" ) ) ? "nav-item active" : "nav-item";
	}

	public String title() {
		return (String)valueForBinding( "title" );
	}

	public String pretitle() {
		return (String)valueForBinding( "pretitle" );
	}

	public String subtitle() {
		return (String)valueForBinding( "subtitle" );
	}

	public String documentTitle() {
		return title() == null ? "Examiner" : title() + " · Examiner";
	}

	public String webObjectsVersion() {
		final NSBundle bundle = NSBundle.bundleForName( "JavaWebObjects" );
		return bundle == null ? "?" : bundle.versionString();
	}

	public String javaVersion() {
		return System.getProperty( "java.version" );
	}
}
