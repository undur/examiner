package examiner.components;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.ElementDefinition;
import examiner.Examination.ElementUsage;

/**
 * One element name: what it resolves to, and the templates using it
 */
public class PLElementUsagePage extends WOComponent {

	public ElementUsage usage;
	public ElementDefinition currentDefinition;

	public PLElementUsagePage( WOContext context ) {
		super( context );
	}

	public String subtitle() {
		return "Used %d times in %d components.".formatted( usage.count(), usage.users().size() );
	}

	/**
	 * @return How many times the current component's template uses the element
	 */
	public long currentUseCount() {
		return currentDefinition.dynamicNodes().stream().filter( n -> n.type().equals( usage.name() ) ).count();
	}
}
