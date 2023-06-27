package ch.unisg.library.systemlibrarian.sru.response;

import ch.unisg.library.systemlibrarian.helper.DomUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Record {
	private final Node recordNode;

	Record(final Node recordNode) {
		this.recordNode = recordNode;
	}

	public Optional<Controlfield> getControlfield(final String tag) {
		final String query = "./controlfield[@tag=\"" + tag + "\"]";
		NodeList controlfields = xPathQuery(recordNode, query);
		return transformToControlfield(controlfields);
	}

	public List<Datafield> findDatafield(final String tag) {
		final String query = "./datafield[@tag=\"" + tag + "\"]";
		NodeList datafields = xPathQuery(recordNode, query);
		return transformToDatafields(datafields);
	}

	public List<Subfield> findSubfield(final String tag, final String code) {
		final String query = "./datafield[@tag=\"" + tag + "\"]/subfield[@code=\"" + code + "\"]";
		NodeList subfields = xPathQuery(recordNode, query);
		return transformToSubfields(subfields);
	}

	private NodeList xPathQuery(final Node node, final String xPathQuery) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			XPathExpression expr = xpath.compile(xPathQuery);
			return (NodeList) expr.evaluate(node, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Datafield> transformToDatafields(final NodeList nodeList) {
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.filter(node -> node.getNodeType() != Node.TEXT_NODE)
				.map(Datafield.Creator::new)
				.map(Datafield.Creator::create)
				.collect(Collectors.toList());
	}

	private List<Subfield> transformToSubfields(final NodeList nodeList) {
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.filter(node -> node.getNodeType() != Node.TEXT_NODE)
				.map(Subfield.Creator::new)
				.map(Subfield.Creator::create)
				.collect(Collectors.toList());
	}

	private Optional<Controlfield> transformToControlfield(final NodeList nodeList) {
		if (nodeList.getLength() < 1) {
			return Optional.empty();
		}
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.filter(node -> node.getNodeType() != Node.TEXT_NODE)
				.map(Controlfield.Creator::new)
				.map(Controlfield.Creator::create)
				.findFirst();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Record record = (Record) o;

		return Objects.equals(recordNode, record.recordNode);
	}

	@Override
	public int hashCode() {
		return recordNode != null ? recordNode.hashCode() : 0;
	}

	@Override
	public String toString() {
		return DomUtil.print(recordNode);
	}

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public Record create() {
			return new Record(node);
		}
	}
}
