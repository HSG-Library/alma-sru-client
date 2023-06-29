package ch.unisg.library.systemlibrarian.sru.client;

public enum Operation {

	EXPLAIN("explain"),
	SEARCH_RETRIEVE("searchRetrieve");

	public static final String KEY = "operation";
	private final String value;

	Operation(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
