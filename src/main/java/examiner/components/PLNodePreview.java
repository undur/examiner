package examiner.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXNonSynchronizingComponent;
import ng.appserver.templating.parser.model.PBasicNode;
import ng.appserver.templating.parser.model.PCommentNode;
import ng.appserver.templating.parser.model.PGroupNode;
import ng.appserver.templating.parser.model.PHTMLNode;
import ng.appserver.templating.parser.model.PNode;

public class PLNodePreview extends ERXNonSynchronizingComponent {

	public PNode node() {
		return (PNode)valueForBinding( "node" );
	}

	public PNode currentNode;

	public PLNodePreview( WOContext context ) {
		super( context );
	}

	public List<PNode> currentChildren() {
		if( node() instanceof PGroupNode p ) {
			return p.children();
		}

		if( node() instanceof PBasicNode p ) {
			return p.children();
		}

		return null;
	}

	public String nodeString() {
		if( node() == null ) {
			return null;
		}

		return switch( node() ) {
			case PGroupNode n -> "PGroupNode";
			case PBasicNode n -> n.type(); /* + "<br>" + n.tag().declaration().bindings().toString(); */
			case PCommentNode n -> "";
			case PHTMLNode n -> n.value();
		};
	}

	public boolean nodeIsHTMLString() {
		return node() instanceof PHTMLNode;
	}

	public boolean escapeHTML() {
		return !nodeIsHTMLString();
	}

	public boolean omitTags() {
		return switch( node() ) {
			case PGroupNode n -> false;
			case PBasicNode n -> {
				final List<PNode> children = n.children();
				yield children == null || children.isEmpty();
			}
			case PCommentNode n -> true;
			default -> true;
		};
	}
}