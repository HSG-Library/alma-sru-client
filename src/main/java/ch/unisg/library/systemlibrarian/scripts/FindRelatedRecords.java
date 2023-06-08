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
	private final File input;
	private final File output;

	public FindRelatedRecords(final String inputXlsxPath, final String outputXlsxPath) {
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

	public void findRelatedRecords(final String column) {
		List<String> mmsIdsFromExcel = getMmsIdsFromExcel(column);
		final Map<String, List<String>> results = new HashMap<>();
		mmsIdsFromExcel.stream().parallel().forEach(mmsId -> {
			Stream<String> systemNumbers = get35a(mmsId);
			List<String> rootRecords = findRelatedRecord(systemNumbers);
			results.put(mmsId, rootRecords);
		});
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
				.map(record -> record.getControlfield("001"))
				.flatMap(Optional::stream)
				.map(Controlfield::getText)
				.collect(Collectors.toList());
	}
}
