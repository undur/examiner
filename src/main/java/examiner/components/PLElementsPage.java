package examiner.components;

import java.util.List;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.Examination;
import examiner.Examination.ElementUsage;

/**
 * The element names the templates use
 */
public class PLElementsPage extends WOComponent {

	public boolean showAll;
	public ElementUsage currentUsage;

	public PLElementsPage( WOContext context ) {
		super( context );
	}

	public Examination examination() {
		return Examination.current();
	}

	public List<ElementUsage> usages() {
		return showAll ? examination().allElementUsages() : examination().elementUsages();
	}

	public String applicationTabClass() {
		return showAll ? "nav-link" : "nav-link active";
	}

	public String allTabClass() {
		return showAll ? "nav-link active" : "nav-link";
	}
}
