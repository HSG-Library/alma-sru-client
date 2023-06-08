package ch.unisg.library.systemlibrarian.sru;

import ch.unisg.library.systemlibrarian.sru.response.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class SruClient {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final HttpClient httpClient;
	private final DocumentBuilderFactory documentBuilderFactory;
	private final XPathFactory xPathFactory;

	public SruClient() {
		this.httpClient = HttpClient.newHttpClient();
		this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
		this.xPathFactory = XPathFactory.newInstance();
	}

	public Optional<Record> getSingleRecord(SruUrlBuilder urlBuilder) {
		urlBuilder.maximumRecords(1L);
		return call(urlBuilder)
				.map(this::getRecordsFromResponse)
				.orElse(Stream.empty())
				.findFirst();
	}

	public Stream<Record> getAllRecords(SruUrlBuilder urlBuilder) {
		long pageSize = 50L;
		return LongStream.iterate(0L, i -> i + pageSize)  // Creates an infinite Stream with elements 0, 4, 8, 12, ...
				.mapToObj(offset -> call(urlBuilder, offset, pageSize))
				.flatMap(Optional::stream)
				.flatMap(this::getRecordsFromResponse);
	}

	public Optional<String> request(SruUrlBuilder urlBuilder) {
		return call(urlBuilder);
	}

	private Optional<String> call(SruUrlBuilder urlBuilder) {
		return call(urlBuilder, null, null);
	}

	private Optional<String> call(SruUrlBuilder urlBuilder, Long offset, Long pageSize) {
		if(offset != null && pageSize != null) {
			urlBuilder.startRecord(offset);
			urlBuilder.maximumRecords(pageSize);
		}

		URI requestUri = getRequestUri(urlBuilder);
		LOG.info("Calling: '{}'", requestUri);
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(requestUri)
				.build();
		try {
			HttpResponse<String> sruResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (sruResponse.statusCode() >= 200 && sruResponse.statusCode() < 300) {
				return Optional.ofNullable(sruResponse.body());
			}
			return Optional.empty();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}




	public Stream<Record> getRecordsFromResponse(final String response) {
		Document domDoc = getDomDoc(response);
		return getRecords(domDoc);
	}

	private URI getRequestUri(final SruUrlBuilder urlBuilder) {
		final String url = urlBuilder.build();
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Document getDomDoc(String sruXml) {
		try {
			return documentBuilderFactory.newDocumentBuilder().parse(new ByteArrayInputStream(sruXml.getBytes()));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Stream<Record> getRecords(final Document sruDocument) {
		NodeList nodeList = xPathQuery(sruDocument, "//recordData/*");
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.map(Record.Creator::new)
				.map(Record.Creator::create);
	}

	private NodeList xPathQuery(final Document doc, final String xPathQuery) {
		XPath xpath = xPathFactory.newXPath();
		try {
			XPathExpression expr = xpath.compile(xPathQuery);
			return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
