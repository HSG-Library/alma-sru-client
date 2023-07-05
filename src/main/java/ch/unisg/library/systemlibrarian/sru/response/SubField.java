package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;

import java.util.Objects;


public class SubField {
	private final Node subFieldNode;
	private final String code;
	private final String text;

	SubField(final Node subFieldNode, final String code, final String text) {
		this.subFieldNode = subFieldNode;
		this.code = code;
		this.text = text;
	}

	public Node getSubFieldNode() {
		return subFieldNode;
	}

	public String getCode() {
		return code;
	}

	public String getText() {
		return text;
	}

	public SubField setText(final String value) {
		subFieldNode.setTextContent(value);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SubField subfield = (SubField) o;

		if (!Objects.equals(code, subfield.code)) return false;
		return Objects.equals(text, subfield.text);
	}

	@Override
	public int hashCode() {
		int result = code != null ? code.hashCode() : 0;
		result = 31 * result + (text != null ? text.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Subfield{" +
				"code='" + code + '\'' +
				", text='" + text + '\'' +
				'}';
	}

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public SubField create() {
			final String code = node.getAttributes().getNamedItem("code").getNodeValue();
			final String text = node.getTextContent();
			return new SubField(node, code, text);
		}
	}
}
