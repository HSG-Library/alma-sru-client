package ch.unisg.library.systemlibrarian.sru.query;

public interface SruIndex {
	String getTitle();

	String getName();

	String getSet();

	boolean isSortable();

	boolean isRelationSupported(final Relation relation);
}
