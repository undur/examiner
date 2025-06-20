package examiner;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;

public class Elementary extends ERXDirectAction {

	public Elementary( WORequest aRequest ) {
		super( aRequest );

	}

	@Override
	public WOActionResults defaultAction() {
		return pageWithName( PLElementary.class );
	}
}