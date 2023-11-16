package ch.unisg.library.systemlibrarian.sru.response;

import ch.unisg.library.systemlibrarian.helper.XPathHelper;
import ch.unisg.library.systemlibrarian.helper.XmlHelper;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MarcRecord {
	private final Node recordNode;
	private final XPathHelper xPath;

	MarcRecord(final Node recordNode) {
		this.recordNode = recordNode;
		this.xPath = new XPathHelper();
	}

	public Optional<Leader> getLeader() {
		final String query = "./leader";
		final Optional<String> leader = xPath.queryText(recordNode, query);
		return leader
				.filter(l -> l.length() == Leader.LEADER_LENGTH)
				.map(Leader::new);
	}

	public Optional<ControlField> getControlField(final String tag) {
		final String query = "./controlfield[@tag=\"" + tag + "\"]";
		NodeList controlFields = xPath.query(recordNode, query);
		return transformToControlField(controlFields);
	}

	public List<DataField> getAllDataFields() {
		final String query = "./datafield";
		NodeList dataFields = xPath.query(recordNode, query);
		return transformToDataFields(dataFields);
	}

	public List<DataField> findDataFields(final String tag) {
		final String query = "./datafield[@tag=\"" + tag + "\"]";
		NodeList datafields = xPath.query(recordNode, query);
		return transformToDataFields(datafields);
	}

	public List<DataField> findDataFields(
			final String tag,
			@Nullable final String ind1,
			@Nullable final String ind2
	) {
		final String query = "./datafield[@tag=\"" + tag + "\"]" +
				"[@ind1=\"" + StringUtils.defaultIfBlank(ind1, StringUtils.SPACE) + "\"]" +
				"[@ind2=\"" + StringUtils.defaultIfBlank(ind2, StringUtils.SPACE) + "\"]";
		NodeList dataFields = xPath.query(recordNode, query);
		return transformToDataFields(dataFields);
	}

	public List<SubField> findSubFields(final String tag, final String code) {
		final String query = "./datafield[@tag=\"" + tag + "\"]/subfield[@code=\"" + code + "\"]";
		NodeList subfields = xPath.query(recordNode, query);
		return transformToSubFields(subfields);
	}

	public List<SubField> findSubFields(
			final String tag,
			@Nullable final String ind1,
			@Nullable final String ind2,
			final String code
	) {
		final String query = "./datafield[@tag=\"" + tag + "\"]" +
				"[@ind1=\"" + StringUtils.defaultIfBlank(ind1, StringUtils.SPACE) + "\"]" +
				"[@ind2=\"" + StringUtils.defaultIfBlank(ind2, StringUtils.SPACE) + "\"]" +
				"/subfield[@code=\"" + code + "\"]";
		NodeList subfields = xPath.query(recordNode, query);
		return transformToSubFields(subfields);
	}

	public DataField addDataField(final String tag) {
		return addDataField(tag, " ", " ");
	}

	public DataField addDataField(final String tag, final String ind1, final String ind2) {
		Element dataField = recordNode.getOwnerDocument().createElement(DataField.ELEMENT);
		dataField.setAttribute(DataField.TAG_ATTRIBUTE, tag);
		dataField.setAttribute(DataField.IND1_ATTRIBUTE, ind1);
		dataField.setAttribute(DataField.IND2_ATTRIBUTE, ind2);
		recordNode.appendChild(dataField);
		return new DataField.Creator(dataField).create();
	}

	public Node getRecordNode() {
		return recordNode;
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
