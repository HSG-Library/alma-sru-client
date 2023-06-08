package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Datafield {
	private final String tag;
	private final String ind1;
	private final String ind2;
	private final List<Subfield> subfields;

	Datafield(final String tag, final String ind1, final String ind2, final List<Subfield> subfields) {
		this.tag = tag;
		this.ind1 = ind1;
		this.ind2 = ind2;
		this.subfields = subfields;
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

	public List<Subfield> getSubfields() {
		return subfields;
	}

	public Optional<Subfield> findSubfield(final String code) {
		return subfields.stream()
				.filter(subfield -> subfield.getCode().equals(code))
				.findFirst();
	}

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public Datafield create() {
			final String tag = node.getAttributes().getNamedItem("tag").getNodeValue();
			final String ind1 = node.getAttributes().getNamedItem("ind1").getNodeValue();
			final String ind2 = node.getAttributes().getNamedItem("ind2").getNodeValue();
			NodeList childNodes = node.getChildNodes();
			List<Subfield> subfields = IntStream.range(0, childNodes.getLength())
					.mapToObj(childNodes::item)
					.filter(node -> node.getNodeType() != Node.TEXT_NODE)
					.map(Subfield.Creator::new)
					.map(Subfield.Creator::create)
					.collect(Collectors.toList());
			return new Datafield(tag, ind1, ind2, subfields);
		}
	}
}
