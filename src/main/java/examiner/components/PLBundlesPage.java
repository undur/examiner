package examiner.components;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.BundleDefinition;
import examiner.Examination;

/**
 * The loaded bundles
 */
public class PLBundlesPage extends WOComponent {

	public BundleDefinition currentBundle;

	public PLBundlesPage( WOContext context ) {
		super( context );
	}

	public Examination examination() {
		return Examination.current();
	}
}
