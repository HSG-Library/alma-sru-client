package ch.unisg.library.systemlibrarian.sru.query;

public enum Sort {

	ASCENDING("sort.ascending"),
	DESCENDING("sort.descending");

	private final String value;

	Sort(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
