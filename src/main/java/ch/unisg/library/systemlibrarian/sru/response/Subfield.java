package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;


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
}
