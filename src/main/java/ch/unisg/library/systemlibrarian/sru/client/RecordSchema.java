package ch.unisg.library.systemlibrarian.sru.client;

public enum RecordSchema {
	MARCXML("marcxml"),
	DC("dc"),
	DCX("dcx"),
	MODS("mods"),
	UNIMARCXML("unimarcxml"),
	KORMARCXML("kormarcxml"),
	CNMARCXML("cnmarcxml"),
	ISOHOLD("isohold");

	public static final String KEY = "recordSchema";
	private final String value;

	RecordSchema(final String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
