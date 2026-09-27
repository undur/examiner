package examiner;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

import parsley.ParsleyConfiguration;

/**
 * Examiner's principal class, loaded with the framework. Makes Parsley the template parser once the application has
 * finished its own initialization, so it replaces whatever parser the application installed (WOOgnl in a Project Wonder
 * application): Examiner's own templates are written for Parsley, and the application's templates are parsed the way
 * Examiner assesses them. Examiner is added to an application temporarily, for an examination, so this is by design.
 */
public class ExaminerPrincipal {

	/**
	 * NSNotificationCenter holds its observers weakly
	 */
	private static final ExaminerPrincipal OBSERVER = new ExaminerPrincipal();

	static {
		NSNotificationCenter.defaultCenter().addObserver( OBSERVER, new NSSelector<>( "applicationWillFinishLaunching", new Class[] { NSNotification.class } ), WOApplication.ApplicationWillFinishLaunchingNotification, null );
	}

	public void applicationWillFinishLaunching( final NSNotification notification ) {
		ParsleyConfiguration.defaultDevConfiguration().register();
	}
}
