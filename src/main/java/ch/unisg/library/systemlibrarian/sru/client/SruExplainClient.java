package ch.unisg.library.systemlibrarian.sru.client;

import io.mikael.urlbuilder.UrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SruExplainClient {

	private final URI sruUri;

	public SruExplainClient(final String baseUrl, final Version version) {
		this.sruUri = UrlBuilder.fromString(baseUrl, StandardCharsets.UTF_8)
				.addParameter(Operation.KEY, Operation.EXPLAIN.getValue())
				.addParameter(Version.KEY, version.getValue())
				.toUri();
	}

	public Optional<Document> getXmlResponse() {
		return new SruHttpClient().call(sruUri);
	}
}
