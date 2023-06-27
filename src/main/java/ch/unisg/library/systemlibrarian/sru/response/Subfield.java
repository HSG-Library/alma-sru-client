package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;

import java.util.Objects;


public class Subfield {
	private final String code;
	private final String text;

	Subfield(final String code, final String text) {
		this.code = code;
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public String getText() {
		return text;
	}

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public Subfield create() {
			final String code = node.getAttributes().getNamedItem("code").getNodeValue();
			final String text = node.getTextContent();
			return new Subfield(code, text);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Subfield subfield = (Subfield) o;

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
}
