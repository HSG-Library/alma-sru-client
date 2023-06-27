package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Controlfield that = (Controlfield) o;

		if (!Objects.equals(tag, that.tag)) return false;
		return Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		int result = tag != null ? tag.hashCode() : 0;
		result = 31 * result + (text != null ? text.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Controlfield{" +
				"tag='" + tag + '\'' +
				", text='" + text + '\'' +
				'}';
	}
}
