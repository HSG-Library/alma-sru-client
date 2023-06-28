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

	/**
	 * Returns the first or only record of the query result.
	 *
	 * @param sruUrl SruUrl with query
	 * @return Optional record, depending on the query, the only result or the first of the result
	 */
	public Optional<MarcRecord> getSingleRecord(final SruUrl sruUrl) {
		return call(sruUrl.withMaximumRecords(1))
				.map(this::extractRecords)
				.orElse(Stream.empty())
				.findFirst();
	}

	/**
	 * Returns the results of the query, according to startRecord and maximumRecords
	 * configured in the SruUrl
	 *
	 * @param sruUrl SruUrl with query
	 * @return The resulting records between startRecord and maximumRecords
	 */
	public Stream<MarcRecord> getRecords(final SruUrl sruUrl) {
		int pageSize = 50;
		return call(sruUrl)
				.map(this::extractRecords)
				.orElse(Stream.empty());
	}

	/**
	 * Returns the complete result set of the query, ignoring the maximumRecords parameter
	 *
	 * @param sruUrl SruUrl with query
	 * @return All resulting records without paging
	 */
	public Stream<MarcRecord> getAllRecords(final SruUrl sruUrl) {
		int pageSize = 50;
		return IntStream.iterate(1, i -> i + pageSize)  // Creates an infinite Stream with elements 0, 50, 100, 150, ...
				.mapToObj(offset -> call(sruUrl, offset, pageSize))
				.takeWhile(Optional::isPresent)
				.flatMap(Optional::stream)
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
}
