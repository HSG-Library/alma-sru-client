package ch.unisg.library.systemlibrarian.sru.response;

import org.w3c.dom.Node;

import java.util.Objects;

public class ControlField {

	private final Node controlFieldNode;
	private final String tag;
	private final String text;


	ControlField(final Node controlFieldNode, final String tag, final String text) {
		this.controlFieldNode = controlFieldNode;
		this.tag = tag;
		this.text = text;
	}

	public Node getControlFieldNode() {
		return controlFieldNode;
	}

	public String getTag() {
		return tag;
	}

	public String getText() {
		return text;
	}

	public ControlField setText(final String value) {
		controlFieldNode.setTextContent(value);
		return this;
	}

	public void remove() {
		controlFieldNode.getParentNode().removeChild(controlFieldNode);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ControlField that = (ControlField) o;

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

	public static class Creator {
		private final Node node;

		public Creator(final Node node) {
			this.node = node;
		}

		public ControlField create() {
			final String tag = node.getAttributes().getNamedItem("tag").getNodeValue();
			final String text = node.getTextContent();
			return new ControlField(node, tag, text);
		}
	}
}
