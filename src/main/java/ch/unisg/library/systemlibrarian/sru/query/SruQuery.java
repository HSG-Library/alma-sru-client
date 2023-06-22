package ch.unisg.library.systemlibrarian.sru.query;

public class SruQuery {

	private final String query;

	public SruQuery(final String query) {
		this.query = query;
	}

	public String string() {
		return query;
	}
}
