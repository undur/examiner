package examiner;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WORequest;

import examiner.components.PLBundlesPage;
import examiner.components.PLComponentsPage;
import examiner.components.PLDirectActionsPage;
import examiner.components.PLElementDefinitionDetailPage;
import examiner.components.PLElementUsagePage;
import examiner.components.PLElementsPage;
import examiner.components.PLExaminerMain;
import examiner.components.PLRequestHandlersPage;

/**
 * The dashboard's entry points, one per section, so every page has a URL of its own.
 */
public class PLExaminerAction extends WODirectAction {

	public PLExaminerAction( WORequest aRequest ) {
		super( aRequest );
	}

	@Override
	public WOActionResults defaultAction() {
		return page( PLExaminerMain.class );
	}

	public WOActionResults componentsAction() {
		final PLComponentsPage page = page( PLComponentsPage.class );
		page.showAll = "all".equals( request().stringFormValueForKey( "scope" ) );
		return page;
	}

	/**
	 * A component, by name (?name=)
	 */
	public WOActionResults componentAction() {
		final ElementDefinition definition = Examination.current().elementDefinitionNamed( request().stringFormValueForKey( "name" ) );

		if( definition == null ) {
			return page( PLComponentsPage.class );
		}

		final PLElementDefinitionDetailPage page = page( PLElementDefinitionDetailPage.class );
		page.selectedObject = definition;
		return page;
	}

	public WOActionResults elementsAction() {
		final PLElementsPage page = page( PLElementsPage.class );
		page.showAll = "all".equals( request().stringFormValueForKey( "scope" ) );
		return page;
	}

	/**
	 * An element's uses, by name (?name=)
	 */
	public WOActionResults elementAction() {
		final Examination.ElementUsage usage = Examination.current().elementUsageNamed( request().stringFormValueForKey( "name" ) );

		if( usage == null ) {
			return page( PLElementsPage.class );
		}

		final PLElementUsagePage page = page( PLElementUsagePage.class );
		page.usage = usage;
		return page;
	}

	public WOActionResults directActionsAction() {
		return page( PLDirectActionsPage.class );
	}

	public WOActionResults requestHandlersAction() {
		return page( PLRequestHandlersPage.class );
	}

	public WOActionResults bundlesAction() {
		return page( PLBundlesPage.class );
	}

	/**
	 * Examines the application again, then shows the overview
	 */
	public WOActionResults rescanAction() {
		Examination.rescan();
		return page( PLExaminerMain.class );
	}

	@SuppressWarnings("unchecked")
	private <E extends WOComponent> E page( final Class<E> pageClass ) {
		return (E)pageWithName( pageClass.getName() );
	}
}
