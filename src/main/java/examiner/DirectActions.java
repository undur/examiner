package examiner;

import java.util.List;

import com.webobjects.appserver.WODirectAction;
import com.webobjects.appserver.WOElement;

public class DirectActions {

	public static List<Class<? extends WOElement>> directActionClasses() {
		return Util.locateSubclassesOf( WODirectAction.class );
	}
}