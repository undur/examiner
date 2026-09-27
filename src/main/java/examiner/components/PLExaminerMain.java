package examiner.components;

import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import examiner.ElementDefinition;
import examiner.Examination;
import examiner.Examination.ElementUsage;

/**
 * The dashboard's overview: counts per section, and the problems found
 */
public class PLExaminerMain extends WOComponent {

	public ElementDefinition currentDefinition;
	public ElementUsage currentUsage;

	public PLExaminerMain( WOContext context ) {
		super( context );
	}

	public Examination examination() {
		return Examination.current();
	}

	public List<ElementUsage> topElementUsages() {
		final List<ElementUsage> all = examination().elementUsages();
		return new ArrayList<>( all.subList( 0, Math.min( 15, all.size() ) ) );
	}

	/**
	 * @return The line of the current component's parse error, or null if unknown
	 */
	public Integer currentParseErrorLine() {
		return currentDefinition.parseErrorLine() > 0 ? currentDefinition.parseErrorLine() : null;
	}

	public String parseFailureCountClass() {
		return examination().parseFailures().isEmpty() ? "h1 mb-1 text-green" : "h1 mb-1 text-red";
	}
}
