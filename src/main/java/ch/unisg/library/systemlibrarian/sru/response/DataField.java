package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataField {
	private final String tag;
	private final String ind1;
	private final String ind2;
	private final List<SubField> subFields;

	DataField(final String tag, final String ind1, final String ind2, final List<SubField> subFields) {
		this.tag = tag;
		this.ind1 = ind1;
		this.ind2 = ind2;
		this.subFields = subFields;
	}

	public String getTag() {
		return tag;
	}

	public String getInd1() {
		return ind1;
	}

	public String getInd2() {
		return ind2;
	}

	public List<SubField> getSubfields() {
		return subFields;
	}

	public Optional<SubField> findSubfield(final String code) {
		return subFields.stream()
				.filter(subfield -> subfield.getCode().equals(code))
				.findFirst();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataField datafield = (DataField) o;

		if (!Objects.equals(tag, datafield.tag)) return false;
		if (!Objects.equals(ind1, datafield.ind1)) return false;
		if (!Objects.equals(ind2, datafield.ind2)) return false;
		return Objects.equals(subFields, datafield.subFields);
	}

	@Override
	public int hashCode() {
		int result = tag != null ? tag.hashCode() : 0;
		result = 31 * result + (ind1 != null ? ind1.hashCode() : 0);
		result = 31 * result + (ind2 != null ? ind2.hashCode() : 0);
		result = 31 * result + (subFields != null ? subFields.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Datafield{" +
				"tag='" + tag + '\'' +
				", ind1='" + ind1 + '\'' +
				", ind2='" + ind2 + '\'' +
				", subfields=" + subFields +
				'}';
	}

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public DataField create() {
			final String tag = node.getAttributes().getNamedItem("tag").getNodeValue();
			final String ind1 = node.getAttributes().getNamedItem("ind1").getNodeValue();
			final String ind2 = node.getAttributes().getNamedItem("ind2").getNodeValue();
			NodeList childNodes = node.getChildNodes();
			List<SubField> subFields = IntStream.range(0, childNodes.getLength())
					.mapToObj(childNodes::item)
					.filter(node -> node.getNodeType() != Node.TEXT_NODE)
					.map(SubField.Creator::new)
					.map(SubField.Creator::create)
					.collect(Collectors.toList());
			return new DataField(tag, ind1, ind2, subFields);
		}
	}
}
