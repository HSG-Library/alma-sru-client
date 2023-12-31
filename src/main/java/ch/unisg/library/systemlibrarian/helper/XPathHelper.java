package ch.unisg.library.systemlibrarian.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

public class XPathHelper {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final XPathFactory xPathFactory;

	public XPathHelper() {
		this.xPathFactory = XPathFactory.newInstance();
	}

	/**
	 * Returns matching nodes as NodeList
	 * If the query does not match any node, an empty NodeList is returned
	 *
	 * @param node       node to be queried
	 * @param xPathQuery xpath query
	 * @return NodeList with matching nodes
	 */
	public NodeList query(final Node node, final String xPathQuery) {
		return evaluate(node, xPathQuery, XPathConstants.NODESET, NodeList.class);
	}

	/**
	 * Returns the first matching node.
	 * If the query does not match any node, an empty Optional is returned
	 *
	 * @param node       node to be queried
	 * @param xPathQuery xpath query
	 * @return optional Node
	 */
	public Optional<Node> querySingle(final Node node, final String xPathQuery) {
		if (queryExists(node, xPathQuery)) {
			return Optional.of(evaluate(node, xPathQuery, XPathConstants.NODE, Node.class));
		}
		return Optional.empty();
	}

	/**
	 * Returns the text content of the first matching node.
	 * If the query does not match any node, an empty Optional is returned
	 *
	 * @param node       node to be queried
	 * @param xPathQuery xpath query
	 * @return optional String
	 */
	public Optional<String> queryText(final Node node, final String xPathQuery) {
		if (queryExists(node, xPathQuery)) {
			return Optional.of(evaluate(node, xPathQuery, XPathConstants.STRING, String.class));
		}
		return Optional.empty();
	}

	/**
	 * Returns true if the query matches any node, otherwise false.
	 *
	 * @param node       node to be queried
	 * @param xPathQuery xpath query
	 * @return true if query matches, false if not
	 */
	public boolean queryExists(final Node node, final String xPathQuery) {
		return evaluate(node, xPathQuery, XPathConstants.BOOLEAN, Boolean.class);
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluate(final Node node, final String query, final QName returnType, final Class<T> type) {
		XPath xpath = xPathFactory.newXPath();
		try {
			XPathExpression expr = xpath.compile(query);
			return (T) expr.evaluate(node, returnType);
		} catch (XPathExpressionException e) {
			LOG.error("Evaluating xPath expression '{}' failed.", query);
			throw new IllegalStateException(e);
		}
	}
}
