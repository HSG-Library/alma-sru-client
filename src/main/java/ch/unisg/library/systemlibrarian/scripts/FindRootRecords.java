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


public class FindRootRecords implements SruExcelInputOutputScript {
	private static final String BASE = "https://eu03.alma.exlibrisgroup.com/view/sru/41SLSP_NETWORK";
	private File input;
	private File output;
	private String column;

	public static void main(String[] args) {
		SruExcelInputOutputScript findRootRecords = new FindRootRecords()
				.input("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-SLSP-Script-output.xlsx", "A")
				.output("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-oberaufnahmen.xlsx");
		findRootRecords.processFiles();
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
		Optional<MarcRecord> result = getRecord(mmsIdNz);
		if (result.isEmpty()) {
			return Stream.empty();
		}
		MarcRecord firstMarcRecord = result.get();

		List<SubField> fields773 = firstMarcRecord.findSubFields("773", "w");
		List<SubField> fields800 = firstMarcRecord.findSubFields("800", "w");
		List<SubField> fields810 = firstMarcRecord.findSubFields("810", "w");
		List<SubField> fields811 = firstMarcRecord.findSubFields("811", "w");
		List<SubField> fields830 = firstMarcRecord.findSubFields("830", "w");

		return Stream.of(fields773.stream(),
						fields800.stream(),
						fields810.stream(),
						fields811.stream(),
						fields830.stream())
				.reduce(Stream::concat)
				.orElseGet(Stream::empty)
				.map(SubField::getText);
	}

	private List<String> searchRootRecords(Stream<String> systemNumbers) {
		return systemNumbers
				.map(this::findRootRecord)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private List<String> findRootRecord(final String id) {
		final SruQueryBuilder sruQueryBuilder = SruQueryBuilder.create(Idx.otherSystemNumberActive_035().equalTo(id));
		// search in 001/mms_id only if id is numerical, otherwise SRU will throw an error
		if (id.matches("[0-9]+")) {
			sruQueryBuilder.or(Idx.mmsId().equalTo(id));
		}
		// always search in 035$$a
		final SruQuery query = sruQueryBuilder.build();
		Stream<MarcRecord> records = SruClientBuilder.create(BASE)
				.query(query)
				.getAllRecords();
		return records
				.map(record -> record.getControlField("001"))
				.flatMap(Optional::stream)
				.map(ControlField::getText)
				.collect(Collectors.toList());
	}
}
