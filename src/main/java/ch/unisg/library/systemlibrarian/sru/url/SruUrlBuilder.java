package ch.unisg.library.systemlibrarian.sru.url;

import ch.unisg.library.systemlibrarian.sru.query.SruQuery;

import java.util.Map;

public class SruUrlBuilder {
	private final String base;
	private Version version = Version.v1_2;
	private Operation operation = Operation.SEARCH_RETRIEVE;
	private int startRecord = 1;
	private int maximumRecords = 50;
	private RecordSchema recordSchema = RecordSchema.MARCXML;
	private String query;

	private SruUrlBuilder(final String baseUrl) {
		this.base = baseUrl;
	}

	public static SruUrlBuilder create(final String baseUrl) {
		return new SruUrlBuilder(baseUrl);
	}

	public SruUrlBuilder version(final Version version) {
		this.version = version;
		return this;
	}

	public SruUrlBuilder operation(final Operation operation) {
		this.operation = operation;
		return this;
	}

	public SruUrlBuilder startRecord(final int startRecord) {
		this.startRecord = startRecord;
		return this;
	}

	public SruUrlBuilder maximumRecords(final int maximumRecords) {
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

	public SruUrl build() {
		final Map<String, String> parameters = Map.of(
				SruUrl.VERSION, this.version.getValue(),
				SruUrl.OPERATION, this.operation.getValue(),
				SruUrl.START_RECORD, String.valueOf(this.startRecord),
				SruUrl.MAXIMUM_RECORDS, String.valueOf(this.maximumRecords),
				SruUrl.RECORD_SCHEMA, this.recordSchema.getValue(),
				SruUrl.QUERY, this.query
		);
		return new SruUrl(this.base, parameters);
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
