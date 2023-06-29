package ch.unisg.library.systemlibrarian.sru.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SruHttpClient {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final HttpClient httpClient;
	private final DocumentBuilderFactory documentBuilderFactory;

	SruHttpClient() {
		this.httpClient = HttpClient.newHttpClient();
		this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
	}

	public Optional<Document> call(URI requestUri) {
		LOG.info("Calling: '{}'", requestUri);
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(requestUri)
				.build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			if (response.statusCode() >= 200 && response.statusCode() < 300) {
				return getDocumentFromXmlString(response.body());
			} else {
				LOG.error("HTTP request to '{}' failed with code '{}'", requestUri, response.statusCode());
				return Optional.empty();
			}
		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException("Could not call '" + requestUri.toString() + "'", e);
		}
	}

	public Optional<Document> getDocumentFromXmlString(String xml) {
		try {
			return Optional.of(documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes())));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			LOG.error("Could not parse xml string", e);
			return Optional.empty();
		}
	}
}
