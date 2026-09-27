package examiner.components;

import java.util.List;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.ElementDefinition;
import examiner.Examination;

/**
 * The components with a template: the application's own, or all of them
 */
public class PLComponentsPage extends WOComponent {

	public boolean showAll;
	public ElementDefinition currentDefinition;

	public PLComponentsPage( WOContext context ) {
		super( context );
	}

	public Examination examination() {
		return Examination.current();
	}

	public List<ElementDefinition> components() {
		return showAll ? examination().components() : examination().applicationComponents();
	}

	public String title() {
		return showAll ? "All components" : examination().applicationName() + "'s components";
	}

	public String applicationTabClass() {
		return showAll ? "nav-link" : "nav-link active";
	}

	public String allTabClass() {
		return showAll ? "nav-link active" : "nav-link";
	}

	/**
	 * @return How the current component's template is written
	 */
	public String currentTemplateKind() {
		return currentDefinition.wodString() == null || currentDefinition.wodString().isBlank() ? "Inline" : "HTML + wod";
	}
}
