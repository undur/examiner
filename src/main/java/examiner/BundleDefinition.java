package examiner;

import com.webobjects.foundation.NSBundle;

public record BundleDefinition( NSBundle bundle, boolean isMain ) {

	public BundleDefinition( NSBundle bundle ) {
		this( bundle, bundle.equals( NSBundle.mainBundle() ) );
	}
}