package examiner.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import com.webobjects.appserver.WOComponent;
import ng.appserver.templating.parser.model.PBasicNode;
import ng.appserver.templating.parser.model.PCommentNode;
import ng.appserver.templating.parser.model.PHTMLNode;
import ng.appserver.templating.parser.model.PNode;
import ng.appserver.templating.parser.model.PRawNode;
import ng.appserver.templating.parser.model.PRootNode;

public class PLNodePreview extends WOComponent {

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}


	public PNode node() {
		return (PNode)valueForBinding( "node" );
	}

	public PNode currentNode;

	public PLNodePreview( WOContext context ) {
		super( context );
	}

	public List<PNode> currentChildren() {
		if( node() instanceof PRootNode p ) {
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
			case PRootNode n -> "PRootNode";
			case PBasicNode n -> n.type(); /* + "<br>" + n.tag().declaration().bindings().toString(); */
			case PCommentNode n -> "";
			case PHTMLNode n -> n.value();
			case PRawNode n -> "Raw node";
		};
	}

	public boolean nodeIsHTMLString() {
		return node() instanceof PHTMLNode;
	}

	/**
	 * @return true if the node is static HTML consisting only of whitespace, which the preview leaves out
	 */
	public boolean isBlank() {
		return node() instanceof PHTMLNode n && n.value().isBlank();
	}

	public boolean omitTags() {
		return switch( node() ) {
			case PRootNode n -> false;
			case PBasicNode n -> {
				final List<PNode> children = n.children();
				yield children == null || children.isEmpty();
			}
			case PCommentNode n -> true;
			default -> true;
		};
	}
}