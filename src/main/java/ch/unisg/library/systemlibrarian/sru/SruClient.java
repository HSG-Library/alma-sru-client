package ch.unisg.library.systemlibrarian.sru;

import ch.unisg.library.systemlibrarian.helper.HttpXmlClientHelper;
import ch.unisg.library.systemlibrarian.helper.XPathHelper;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import ch.unisg.library.systemlibrarian.sru.url.SruUrl;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SruClient {

	public Optional<MarcRecord> getSingleRecord(final SruUrl sruUrl) {
		return call(sruUrl.withMaximumRecords(1))
				.map(this::extractRecords)
				.orElse(Stream.empty())
				.findFirst();
	}

	public Stream<MarcRecord> getAllRecords(final SruUrl sruUrl) {
		int pageSize = 50;
		return IntStream.iterate(0, i -> i + pageSize)  // Creates an infinite Stream with elements 0, 4, 8, 12, ...
				.mapToObj(offset -> call(sruUrl, offset, pageSize))
				.flatMap(Optional::stream)
				.takeWhile(this::hasRecords)
				.flatMap(this::extractRecords);
	}

	public Stream<MarcRecord> extractRecords(final Document sruDocument) {
		NodeList nodeList = new XPathHelper().query(sruDocument, "//recordData/*");
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.map(MarcRecord.Creator::new)
				.map(MarcRecord.Creator::create);
	}

	private Optional<Document> call(final SruUrl sruUrl) {
		return call(sruUrl, null, null);
	}

	private Optional<Document> call(final SruUrl sruUrl, final Integer offset, final Integer pageSize) {
		if (offset != null && pageSize != null) {
			final SruUrl sruUrlWithOffset = sruUrl
					.withStartRecord(offset)
					.withMaximumRecords(pageSize);
			return new HttpXmlClientHelper().call(sruUrlWithOffset.getUrl());
		}
		return new HttpXmlClientHelper().call(sruUrl.getUrl());
	}

	private boolean hasRecords(final Document document) {
		final NodeList nodeList = new XPathHelper().query(document, "//recordData/*");
		return nodeList.getLength() > 0;
	}
}
