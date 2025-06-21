package examiner;

import static org.reflections.scanners.Scanners.SubTypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class Util {

	/**
	 * @return A list of all subclasses of the given class on the classpath
	 */
	public static List locateSubclassesOf( Class clazz ) {
		final ConfigurationBuilder config = new ConfigurationBuilder().setUrls( ClasspathHelper.forJavaClassPath() ).addScanners( Scanners.SubTypes );
		final Reflections reflections = new Reflections( config );

		final Set elementClasses = reflections.get( SubTypes.of( clazz ).asClass() );

		Comparator c1 = Comparator.comparing( ( Class c ) -> c.getPackageName() );
		Comparator c2 = Comparator.comparing( ( Class c ) -> c.getSimpleName() );

		List sorted = elementClasses
				.stream()
				.sorted( c1.thenComparing( c2 ) )
				.toList();

		return new ArrayList<>( sorted );
	}
}