package ch.unisg.library.systemlibrarian.sru.generator;

import ch.unisg.library.systemlibrarian.helper.XPathHelper;
import ch.unisg.library.systemlibrarian.sru.query.Relation;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class SruIndexMeta {
	private final String title;
	private final String name;
	private final String set;
	private final List<Relation> relations;
	private final boolean emptyTerm;
	private final boolean sort;

	public SruIndexMeta(
			final String title,
			final String name,
			final String set,
			final List<Relation> relations,
			final boolean emptyTerm,
			final boolean sort) {
		this.title = title;
		this.name = name;
		this.set = set;
		this.relations = relations;
		this.emptyTerm = emptyTerm;
		this.sort = sort;
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public String getSet() {
		return set;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public boolean isEmptyTerm() {
		return emptyTerm;
	}

	public boolean isSort() {
		return sort;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SruIndexMeta that = (SruIndexMeta) o;

		if (emptyTerm != that.emptyTerm) return false;
		if (sort != that.sort) return false;
		if (!Objects.equals(title, that.title)) return false;
		if (!Objects.equals(name, that.name)) return false;
		if (!Objects.equals(set, that.set)) return false;
		return Objects.equals(relations, that.relations);
	}

	@Override
	public int hashCode() {
		int result = title != null ? title.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (set != null ? set.hashCode() : 0);
		result = 31 * result + (relations != null ? relations.hashCode() : 0);
		result = 31 * result + (emptyTerm ? 1 : 0);
		result = 31 * result + (sort ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SruIndexMeta{" +
				"title='" + title + '\'' +
				", name='" + name + '\'' +
				", set='" + set + '\'' +
				", relations=" + relations +
				", emptyTerm=" + emptyTerm +
				", sort=" + sort +
				'}';
	}

	public static class Factory {
		public SruIndexMeta create(final Node indexNode) {
			final XPathHelper xPathHelper = new XPathHelper();
			final String title = xPathHelper.queryText(indexNode, "./title/text()").orElse("no-title");
			final boolean sortable = xPathHelper.queryExists(indexNode, "./@sort");
			final String name = xPathHelper.queryText(indexNode, "./map/name/text()").orElse("no-name");
			final String indexSet = xPathHelper.queryText(indexNode, "./map/name/@set").orElse("no-set");
			final List<Relation> relations = new ArrayList<>();
			final boolean emptyTerm = xPathHelper.queryExists(indexNode, "./configInfo/supports[@type='emptyTerm']");
			final NodeList supports = xPathHelper.query(indexNode, "./configInfo/supports");
			IntStream.range(0, supports.getLength())
					.mapToObj(supports::item)
					.forEach(supportsNode -> {
						final String type = xPathHelper.queryText(supportsNode, "./@type").orElse("no-type");
						if ("relation".equals(type)) {
							final String relationValue = supportsNode.getTextContent();
							Relation.findByOperator(relationValue)
									.ifPresent(relations::add);
						}
					});
			return new SruIndexMeta(title, name, indexSet, relations, emptyTerm, sortable);
		}
	}
}
