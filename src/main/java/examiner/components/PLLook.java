package examiner.components;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class PLLook extends WOComponent {

	public String searchString;

	public PLLook( WOContext context ) {
		super( context );
	}

	/**
	 * FIXME: This should be configurable. Should be null for a non-fluid layout
	 */
	public String bodyClass() {
		return "layout-fluid";
		//		return null;
	}

	public boolean showTopButtons() {
		return false;
	}

	public String envString() {
		return "Góður kóði slf. 2025";
	}

	//	public NGActionResults search() {
	//		final WCSearchPage nextPage = pageWithName( WCSearchPage.class );
	//		nextPage.searchString = searchString;
	//		nextPage.search();
	//		return nextPage;
	//	}
}