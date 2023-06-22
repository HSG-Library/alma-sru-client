package ch.unisg.library.systemlibrarian.sru;

import ch.unisg.library.systemlibrarian.helper.HttpXmlClientHelper;
import ch.unisg.library.systemlibrarian.helper.XPathHelper;
import ch.unisg.library.systemlibrarian.sru.response.Record;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class SruClient {

	public Optional<Record> getSingleRecord(SruUrlBuilder urlBuilder) {
		urlBuilder.maximumRecords(1L);
		return call(urlBuilder)
				.map(this::getRecords)
				.orElse(Stream.empty())
				.findFirst();
	}

	public Stream<Record> getAllRecords(SruUrlBuilder urlBuilder) {
		long pageSize = 50L;
		return LongStream.iterate(0L, i -> i + pageSize)  // Creates an infinite Stream with elements 0, 4, 8, 12, ...
				.mapToObj(offset -> call(urlBuilder, offset, pageSize))
				.flatMap(Optional::stream)
				.flatMap(this::getRecords);
	}

	public Stream<Record> getRecords(final Document sruDocument) {
		NodeList nodeList = new XPathHelper().query(sruDocument, "//recordData/*");
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.map(Record.Creator::new)
				.map(Record.Creator::create);
	}

	private Optional<Document> call(SruUrlBuilder urlBuilder) {
		return call(urlBuilder, null, null);
	}

	private Optional<Document> call(SruUrlBuilder urlBuilder, Long offset, Long pageSize) {
		if (offset != null && pageSize != null) {
			urlBuilder.startRecord(offset);
			urlBuilder.maximumRecords(pageSize);
		}
		URI requestUri = getRequestUri(urlBuilder);
		return new HttpXmlClientHelper().call(requestUri);
	}

	private URI getRequestUri(final SruUrlBuilder urlBuilder) {
		final String url = urlBuilder.build();
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
