package ch.unisg.library.systemlibrarian.sru.client;

import ch.unisg.library.systemlibrarian.helper.XPathHelper;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import io.mikael.urlbuilder.UrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SruQueryClient {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	static final String QUERY = "query";
	static final String START_RECORD = "startRecord";
	static final int DEFAULT_START_RECORD = 1;
	static final String MAXIMUM_RECORDS = "maximumRecords";
	static final int DEFAULT_MAXIMUM_RECORDS = 50;
	private final String baseUrl;
	private final Version version;
	private final SruQuery query;
	private int startRecord = DEFAULT_START_RECORD;
	private int maximumRecords = DEFAULT_MAXIMUM_RECORDS;
	private RecordSchema recordSchema = RecordSchema.MARCXML;

	SruQueryClient(
			final String baseUrl,
			final Version version,
			final SruQuery sruQuery) {
		this.baseUrl = baseUrl;
		this.version = version;
		this.query = sruQuery;
	}

	public SruQueryClient startRecord(final int startRecord) {
		this.startRecord = startRecord;
		return this;
	}

	public SruQueryClient maximumRecords(final int maximumRecords) {
		if (maximumRecords > 50) {
			LOG.warn("maximumRecords must be between 1 and 50. Falling back to 50.");
		}
		this.maximumRecords = maximumRecords;
		return this;
	}

	public SruQueryClient recordSchema(final RecordSchema recordSchema) {
		this.recordSchema = recordSchema;
		return this;
	}

	public Optional<Document> getXmlResponse() {
		return call(getSruUri());
	}

	/**
	 * Returns the first or only record of the query result.
	 *
	 * @return Optional record, depending on the query, the only result or the first of the result
	 */
	public Optional<MarcRecord> getSingleRecord() {
		LOG.warn("Retrieving only one record, ignoring maximumRecords");
		URI callUri = UrlBuilder.fromUri(getSruUri())
				.setParameter(MAXIMUM_RECORDS, String.valueOf(1))
				.toUri();
		return call(callUri)
				.map(this::extractRecords)
				.orElse(Stream.empty())
				.findFirst();
	}

	/**
	 * Returns the results of the query, according to startRecord and maximumRecords
	 * configured in the SruUrl
	 *
	 * @return The resulting records between startRecord and maximumRecords
	 */
	public Stream<MarcRecord> getRecords() {
		return call(getSruUri())
				.map(this::extractRecords)
				.orElse(Stream.empty());
	}

	/**
	 * Returns the complete result set of the query, ignoring the maximumRecords parameter
	 *
	 * @return All resulting records without paging
	 */
	public Stream<MarcRecord> getAllRecords() {
		LOG.warn("Retrieving all available records, ignoring maximumRecords");
		return IntStream.iterate(1, i -> i + DEFAULT_MAXIMUM_RECORDS)  // Creates an infinite Stream with elements 0, 50, 100, 150, ...
				.mapToObj(offset -> call(getSruUri(), offset, DEFAULT_MAXIMUM_RECORDS))
				.takeWhile(result -> result.isPresent() && hasRecords(result.get()))
				.flatMap(Optional::stream)
				.flatMap(this::extractRecords);
	}

	private Stream<MarcRecord> extractRecords(final Document sruDocument) {
		NodeList nodeList = new XPathHelper().query(sruDocument, "//recordData/*");
		return IntStream.range(0, nodeList.getLength())
				.mapToObj(nodeList::item)
				.map(MarcRecord.Creator::new)
				.map(MarcRecord.Creator::create);
	}

	private Optional<Document> call(final URI sruUri) {
		return call(sruUri, null, null);
	}

	private Optional<Document> call(final URI sruUri, final Integer offset, final Integer pageSize) {
		if (offset != null && pageSize != null) {
			final URI callUri = UrlBuilder.fromUri(sruUri)
					.setParameter(START_RECORD, String.valueOf(offset))
					.setParameter(MAXIMUM_RECORDS, String.valueOf(pageSize))
					.toUri();
			return new SruHttpClient().call(callUri);
		}
		return new SruHttpClient().call(sruUri);
	}

	private boolean hasRecords(final Document document) {
		return document.getElementsByTagName("record").getLength() > 0;
	}

	public URI getSruUri() {
		return UrlBuilder.fromString(baseUrl, StandardCharsets.UTF_8)
				.addParameter(Operation.KEY, Operation.SEARCH_RETRIEVE.getValue())
				.addParameter(Version.KEY, version.getValue())
				.addParameter(QUERY, query.string())
				.addParameter(MAXIMUM_RECORDS, String.valueOf(maximumRecords))
				.addParameter(START_RECORD, String.valueOf(startRecord))
				.addParameter(RecordSchema.KEY, recordSchema.getValue())
				.toUri();
	}
}
