package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;

public class Controlfield {

	private final String tag;
	private final String text;

	Controlfield(final String tag, final String text) {
		this.tag = tag;
		this.text = text;
	}

	public String getTag() {
		return tag;
	}

	public String getText() {
		return text;
	}

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public Controlfield create() {
			final String tag = node.getAttributes().getNamedItem("tag").getNodeValue();
			final String text = node.getTextContent();
			return new Controlfield(tag, text);
		}
	}
}
