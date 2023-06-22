package ch.unisg.library.systemlibrarian.sru.query;

public enum BoolOp {

	AND("AND"),
	OR("OR"),
	NONE("NONE");

	private final String value;

	BoolOp(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
