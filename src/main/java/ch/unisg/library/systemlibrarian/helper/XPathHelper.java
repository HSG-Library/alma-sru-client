package ch.unisg.library.systemlibrarian.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import java.lang.invoke.MethodHandles;

public class XPathHelper {

	private final XPathFactory xPathFactory;

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public XPathHelper() {
		this.xPathFactory = XPathFactory.newInstance();
	}

	public NodeList query(final Node node, final String xPathQuery) {
		return evaluate(node, xPathQuery, XPathConstants.NODESET, NodeList.class);
	}

	public Node querySingle(final Node node, final String xPathQuery) {
		return evaluate(node, xPathQuery, XPathConstants.NODE, Node.class);
	}

	public String queryText(final Node node, final String xPathQuery) {
		return evaluate(node, xPathQuery, XPathConstants.STRING, String.class);
	}

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
