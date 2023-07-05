package ch.unisg.library.systemlibrarian.sru.response;

import ch.unisg.library.systemlibrarian.helper.XmlHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MarcRecord {
	private final Node recordNode;

	MarcRecord(final Node recordNode) {
		this.recordNode = recordNode;
	}

	public Optional<ControlField> getControlField(final String tag) {
		final String query = "./controlfield[@tag=\"" + tag + "\"]";
		NodeList controlfields = xPathQuery(recordNode, query);
		return transformToControlField(controlfields);
	}

	public List<DataField> findDataFields(final String tag) {
		final String query = "./datafield[@tag=\"" + tag + "\"]";
		NodeList datafields = xPathQuery(recordNode, query);
		return transformToDataFields(datafields);
	}

	public List<SubField> findSubFields(final String tag, final String code) {
		final String query = "./datafield[@tag=\"" + tag + "\"]/subfield[@code=\"" + code + "\"]";
		NodeList subfields = xPathQuery(recordNode, query);
		return transformToSubFields(subfields);
	}

	public Node getRecordNode() {
		return recordNode;
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

	private List<DataField> transformToDataFields(final NodeList nodeList) {
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.filter(node -> node.getNodeType() != Node.TEXT_NODE)
				.map(DataField.Creator::new)
				.map(DataField.Creator::create)
				.collect(Collectors.toList());
	}

	private List<SubField> transformToSubFields(final NodeList nodeList) {
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.filter(node -> node.getNodeType() != Node.TEXT_NODE)
				.map(SubField.Creator::new)
				.map(SubField.Creator::create)
				.collect(Collectors.toList());
	}

	private Optional<ControlField> transformToControlField(final NodeList nodeList) {
		if (nodeList.getLength() < 1) {
			return Optional.empty();
		}
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.filter(node -> node.getNodeType() != Node.TEXT_NODE)
				.map(ControlField.Creator::new)
				.map(ControlField.Creator::create)
				.findFirst();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MarcRecord marcRecord = (MarcRecord) o;

		return Objects.equals(recordNode, marcRecord.recordNode);
	}

	@Override
	public int hashCode() {
		return recordNode != null ? recordNode.hashCode() : 0;
	}

	@Override
	public String toString() {
		return new XmlHelper().nodeToString(recordNode);
	}

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public MarcRecord create() {
			return new MarcRecord(node);
		}
	}
}
