package examiner;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;
import examiner.components.PLExaminerMain;

public class PLExaminerAction extends ERXDirectAction {

	public PLExaminerAction( WORequest aRequest ) {
		super( aRequest );

	}

	@Override
	public WOActionResults defaultAction() {
		return pageWithName( PLExaminerMain.class );
	}
}