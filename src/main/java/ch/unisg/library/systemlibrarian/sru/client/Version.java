package ch.unisg.library.systemlibrarian.sru.client;

public enum Version {
	v1_2("1.2");

	public static final String KEY = "version";

	private final String value;

	Version(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
