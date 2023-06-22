package ch.unisg.library.systemlibrarian.sru;

import ch.unisg.library.systemlibrarian.sru.query.SruQuery;

import java.util.Map;
import java.util.stream.Collectors;

public class SruUrlBuilder {
	private static final String VERSION = "version";
	private static final String OPERATION = "operation";
	private static final String QUERY = "query";
	private static final String START_RECORD = "startRecord";
	private static final String MAXIMUM_RECORDS = "maximumRecords";
	private static final String RECORD_SCHEMA = "recordSchema";
	private final String base;
	private Version version = Version.v1_2;
	private Operation operation = Operation.SEARCH_RETRIEVE;
	private long startRecord = 1;
	private long maximumRecords = 50;
	private RecordSchema recordSchema = RecordSchema.MARCXML;
	private String query;

	public SruUrlBuilder(final String baseUrl) {
		this.base = baseUrl;
	}

	public SruUrlBuilder version(final Version version) {
		this.version = version;
		return this;
	}

	public SruUrlBuilder operation(final Operation operation) {
		this.operation = operation;
		return this;
	}

	public SruUrlBuilder startRecord(final long startRecord) {
		this.startRecord = startRecord;
		return this;
	}

	public SruUrlBuilder maximumRecords(final long maximumRecords) {
		this.maximumRecords = maximumRecords;
		return this;
	}

	public SruUrlBuilder recordSchema(final RecordSchema recordSchema) {
		this.recordSchema = recordSchema;
		return this;
	}

	public SruUrlBuilder query(final SruQuery query) {
		this.query = query.string();
		return this;
	}

	public String build() {
		final Map<String, String> parameters = Map.of(
				VERSION, this.version.getValue(),
				OPERATION, this.operation.getValue(),
				START_RECORD, String.valueOf(this.startRecord),
				MAXIMUM_RECORDS, String.valueOf(this.maximumRecords),
				RECORD_SCHEMA, this.recordSchema.getValue(),
				QUERY, this.query
		);
		return this.base + "?" + joinParameters(parameters);
	}

	private String joinParameters(Map<String, String> parameters) {
		return parameters.entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));
	}

	public enum Version {
		v1_2("1.2");

		private final String value;

		Version(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum Operation {
		EXPLAIN("explain"),
		SEARCH_RETRIEVE("searchRetrieve");

		private final String value;

		Operation(final String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum RecordSchema {
		MARCXML("marcxml"),
		DC("dc"),
		DCX("dcx"),
		MODS("mods"),
		UNIMARCXML("unimarcxml"),
		KORMARCXML("kormarcxml"),
		CNMARCXML("cnmarcxml"),
		ISOHOLD("isohold");

		private final String value;

		RecordSchema(final String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
}
