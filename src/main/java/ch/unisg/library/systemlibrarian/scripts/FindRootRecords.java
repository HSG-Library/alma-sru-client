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


public class FindRootRecords implements SruScript {
	private static final String BASE = "https://eu03.alma.exlibrisgroup.com/view/sru/41SLSP_NETWORK";
	private final File input;
	private final File output;

	public FindRootRecords(final String inputXlsxPath, final String outputXlsxPath) {
		this.input = new File(inputXlsxPath);
		this.output = new File(outputXlsxPath);
	}

	@Override
	public File getInput() {
		return input;
	}

	@Override
	public File getOutput() {
		return output;
	}

	@Override
	public String getBaseUrl() {
		return BASE;
	}

	public void getRootRecords(final String mmsIdColumn) {
		List<String> mmsIdNz = getMmsIdsFromExcel(mmsIdColumn);
		final Map<String, List<String>> results = new HashMap<>();
		mmsIdNz.stream().parallel().forEach(mmsId -> {
			Stream<String> systemNumbers = getOtherSystemNumbers(mmsId);
			List<String> rootRecords = searchRootRecords(systemNumbers);
			results.put(mmsId, rootRecords);
		});
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
				.query(query);
		SruClient sru = new SruClient();
		Optional<String> response = sru.request(urlBuilder);
		Stream<Record> records = response.map(sru::getRecordsFromResponse)
				.orElse(Stream.empty());
		return records
				.map(record -> record.getControlfield("001"))
				.flatMap(Optional::stream)
				.map(Controlfield::getText)
				.collect(Collectors.toList());
	}
}
