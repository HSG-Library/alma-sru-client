package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.sru.SruClient;
import ch.unisg.library.systemlibrarian.sru.SruUrlBuilder;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.response.Controlfield;
import ch.unisg.library.systemlibrarian.sru.response.Record;
import ch.unisg.library.systemlibrarian.sru.response.Subfield;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FindRootRecords implements SruScript {
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
			Stream<String> systemNumbers = getOtherSystemNumbers(mmsId);
			List<String> rootRecords = searchRootRecords(systemNumbers);
			results.put(mmsId, rootRecords);
		});
		return results;
	}

	@Override
	public void processFiles() {
		final List<String> mmsIdNz = getMmsIdsFromExcel(column);
		final Map<String, List<String>> results = getResults(mmsIdNz);
		writeToExcel(results);
	}

	private Stream<String> getOtherSystemNumbers(String mmsIdNz) {
		Optional<Record> result = getRecord(mmsIdNz);
		if (result.isEmpty()) {
			return Stream.empty();
		}
		Record firstRecord = result.get();

		List<Subfield> fields773 = firstRecord.findSubfield("773", "w");
		List<Subfield> fields800 = firstRecord.findSubfield("800", "w");
		List<Subfield> fields810 = firstRecord.findSubfield("810", "w");
		List<Subfield> fields811 = firstRecord.findSubfield("811", "w");
		List<Subfield> fields830 = firstRecord.findSubfield("830", "w");

		return Stream.of(fields773.stream(),
						fields800.stream(),
						fields810.stream(),
						fields811.stream(),
						fields830.stream())
				.reduce(Stream::concat)
				.orElseGet(Stream::empty)
				.map(Subfield::getText);
	}

	private List<String> searchRootRecords(Stream<String> systemNumbers) {
		return systemNumbers
				.map(this::findRootRecord)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private List<String> findRootRecord(final String id) {
		final List<String> queryParts = new ArrayList<>();
		// search in 001/mms_id only if id is numerical, otherwise SRU will throw an error
		if (id.matches("[0-9]+")) {
			queryParts.add("mms_id=" + id);
		}
		// always search in 035$$a
		queryParts.add("other_system_number_active_035==" + id);
		final String query = String.join(" OR ", queryParts);
		final SruUrlBuilder urlBuilder = new SruUrlBuilder(BASE)
				.query(new SruQuery(query));
		SruClient sru = new SruClient();
		Stream<Record> records = sru.getAllRecords(urlBuilder);
		return records
				.map(record -> record.getControlfield("001"))
				.flatMap(Optional::stream)
				.map(Controlfield::getText)
				.collect(Collectors.toList());
	}
}
