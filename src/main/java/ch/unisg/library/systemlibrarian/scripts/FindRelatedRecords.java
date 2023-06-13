package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.SruUrlBuilder;
import ch.unisg.library.systemlibrarian.sru.response.Controlfield;
import ch.unisg.library.systemlibrarian.sru.response.Record;
import ch.unisg.library.systemlibrarian.sru.response.Subfield;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindRelatedRecords implements SruScript {

	private static final String BASE = "https://eu03.alma.exlibrisgroup.com/view/sru/41SLSP_NETWORK";
	private File input;
	private File output;
	private String column;

	@Override
	public SruScript input(final String path, final String column) {
		this.input = new File(path);
		this.column = column;
		return this;
	}

	@Override
	public File getInput() {
		return input;
	}

	@Override
	public SruScript output(String path) {
		this.output = new File(path);
		return this;
	}

	@Override
	public File getOutput() {
		return output;
	}

	@Override
	public String getBaseUrl() {
		return BASE;
	}

	@Override
	public Map<String, List<String>> getResults(List<String> mmsIds) {
		final Map<String, List<String>> results = new HashMap<>();
		mmsIds.stream().parallel().forEach(mmsId -> {
			Stream<String> systemNumbers = get35a(mmsId);
			List<String> related = findRelatedRecord(systemNumbers);
			related.remove(mmsId);
			results.put(mmsId, related);
		});
		return results;
	}

	@Override
	public void processFiles() {
		final List<String> mmsIdsFromExcel = getMmsIdsFromExcel(column);
		final Map<String, List<String>> results = getResults(mmsIdsFromExcel);
		writeToExcel(results);
	}

	private Stream<String> get35a(String mmsIdNz) {
		Optional<Record> result = getRecord(mmsIdNz);
		if (result.isEmpty()) {
			return Stream.empty();
		}
		Record firstRecord = result.get();
		List<Subfield> fields35a = firstRecord.findSubfield("035", "a");
		return fields35a.stream()
				.map(Subfield::getText);
	}

	private List<String> findRelatedRecord(final Stream<String> systemNumbers) {
		final String query = systemNumbers
				.map(number -> "other_system_number==" + number)
				.collect(Collectors.joining(" OR "));
		final SruUrlBuilder urlBuilder = new SruUrlBuilder(getBaseUrl())
				.query(query);
		SruClient sru = new SruClient();
		Optional<String> response = sru.request(urlBuilder);
		Stream<Record> records = response.map(sru::getRecordsFromResponse)
				.orElse(Stream.empty());
		return records
				.skip(1)
				.map(record -> record.getControlfield("001"))
				.flatMap(Optional::stream)
				.map(Controlfield::getText)
				.collect(Collectors.toList());
	}
}
