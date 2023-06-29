package ch.unisg.library.systemlibrarian.sru.client;

import ch.unisg.library.systemlibrarian.sru.query.SruQuery;

public class SruClientBuilder {

	private final String baseUrl;

	private Version version = Version.v1_2;

	private SruClientBuilder(final String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public static SruClientBuilder create(final String baseUrl) {
		return new SruClientBuilder(baseUrl);
	}

	public SruClientBuilder version(final Version version) {
		this.version = version;
		return this;
	}

	public SruQueryClient query(final SruQuery sruQuery) {
		return new SruQueryClient(baseUrl, version, sruQuery);
	}

	public SruExplainClient explain() {
		return new SruExplainClient(baseUrl, version);
	}
}
