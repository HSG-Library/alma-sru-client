package ch.unisg.library.systemlibrarian.scripts;

import ch.unisg.library.systemlibrarian.sru.client.SruClientBuilder;
import ch.unisg.library.systemlibrarian.sru.query.SruQuery;
import ch.unisg.library.systemlibrarian.sru.query.SruQueryBuilder;
import ch.unisg.library.systemlibrarian.sru.query.index.Idx;
import ch.unisg.library.systemlibrarian.sru.response.ControlField;
import ch.unisg.library.systemlibrarian.sru.response.MarcRecord;
import ch.unisg.library.systemlibrarian.sru.response.SubField;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindRelatedRecords implements SruExcelInputOutputScript {

	private static final String BASE = "https://eu03.alma.exlibrisgroup.com/view/sru/41SLSP_NETWORK";
	private File input;
	private File output;
	private String column;

	public static void main(String[] args) {
		SruExcelInputOutputScript findRelatedRecords = new FindRelatedRecords()
				.input("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-SLSP-Script-output.xlsx", "A")
				.output("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-verknuepfte.xlsx");
		findRelatedRecords.processFiles();
	}

	@Override
	public SruExcelInputOutputScript input(final String path, final String column) {
		this.input = new File(path);
		this.column = column;
		return this;
	}

	@Override
	public File getInput() {
		return input;
	}

	@Override
	public SruExcelInputOutputScript output(String path) {
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
		Optional<MarcRecord> result = getRecord(mmsIdNz);
		if (result.isEmpty()) {
			return Stream.empty();
		}
		MarcRecord firstMarcRecord = result.get();
		List<SubField> fields35a = firstMarcRecord.findSubFields("035", "a");
		return fields35a.stream()
				.map(SubField::getText);
	}

	private List<String> findRelatedRecord(final Stream<String> systemNumbers) {
		SruQueryBuilder sruQueryBuilder = SruQueryBuilder.create();
		systemNumbers.forEach(number -> sruQueryBuilder.or(Idx.otherSystemNumber().equalTo(number)));
		final SruQuery query = sruQueryBuilder.build();
		Stream<MarcRecord> records = SruClientBuilder.create(getBaseUrl())
				.query(query)
				.getAllRecords();
		return records
				.map(record -> record.getControlField("001"))
				.flatMap(Optional::stream)
				.map(ControlField::getText)
				.collect(Collectors.toList());
	}
}
