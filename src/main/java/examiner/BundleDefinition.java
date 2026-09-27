package examiner;

import com.webobjects.foundation.NSBundle;

public record BundleDefinition( NSBundle bundle, boolean isMain ) {

	public BundleDefinition( NSBundle bundle ) {
		this( bundle, bundle.equals( NSBundle.mainBundle() ) );
	}

	public String version() {
		return version( bundle() );
	}

	/**
	 * @return The bundle's version from its Info.plist, or null if it doesn't say
	 */
	public static String version( final NSBundle bundle ) {
		final Object version = bundle.infoDictionary() == null ? null : bundle.infoDictionary().objectForKey( "CFBundleShortVersionString" );
		return version == null ? null : version.toString();
	}
}
