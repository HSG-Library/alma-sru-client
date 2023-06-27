package ch.unisg.library.systemlibrarian.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

public class XmlHelper {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public Document combineToDocument(final String rootNodeName, Stream<Node> nodes) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document combinedDocument = builder.newDocument();
			final Node rootNode = combinedDocument.createElement(rootNodeName);
			combinedDocument.appendChild(rootNode);
			nodes.forEach(node -> {
				final Node importedNode = combinedDocument.importNode(node, true);
				rootNode.appendChild(importedNode);
			});
			return combinedDocument;
		} catch (ParserConfigurationException e) {
			LOG.error("Could not combine nodes to a document", e);
			throw new RuntimeException(e);
		}
	}

	public String nodeToString(final Node node) {
		try {
			StringWriter writer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(node), new StreamResult(writer));
			return writer.toString();
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}
